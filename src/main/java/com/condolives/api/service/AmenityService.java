package com.condolives.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.amenity.AmenityDetailResponse;
import com.condolives.api.dto.amenity.AmenityDetailResponseAdmin;
import com.condolives.api.dto.amenity.AmenityResponse;
import com.condolives.api.dto.amenity.CreateAmenityRequest;
import com.condolives.api.dto.amenity.CreateExceptionRequest;
import com.condolives.api.dto.amenity.ExceptionResponse;
import com.condolives.api.dto.amenity.ScheduleResponse;
import com.condolives.api.dto.amenity.SetScheduleRequest;
import com.condolives.api.dto.amenity.UpdateAmenityRequest;
import com.condolives.api.entity.Amenity.Amenity;
import com.condolives.api.entity.Amenity.AmenityException;
import com.condolives.api.entity.Amenity.AmenitySchedule;
import com.condolives.api.entity.Amenity.Booking;
import com.condolives.api.entity.User.Notification;
import com.condolives.api.enums.BookingStatus;
import com.condolives.api.enums.NotificationType;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Amenity.AmenityExceptionRepository;
import com.condolives.api.repository.Amenity.AmenityRepository;
import com.condolives.api.repository.Amenity.AmenityScheduleRepository;
import com.condolives.api.repository.Amenity.BookingRepository;
import com.condolives.api.repository.User.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityScheduleRepository scheduleRepository;
    private final AmenityExceptionRepository exceptionRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public AmenityResponse create(CreateAmenityRequest request, UUID condominiumId) {
        if (amenityRepository.existsByCondominiumIdAndNameIgnoreCase(condominiumId, request.name())) {
            throw new ServiceException("Já existe um espaço com este nome no condomínio", 409);
        }

        Amenity amenity = Amenity.builder()
                .condominiumId(condominiumId)
                .name(request.name())
                .maxCapacity(request.maxCapacity())
                .maxBookingDuration(request.maxBookingDuration())
                .description(request.description())
                .active(true)
                .build();

        return AmenityResponse.from(amenityRepository.save(amenity));
    }

    public List<AmenityResponse> list(UUID condominiumId, Boolean activeOnly) {
        List<Amenity> amenities = Boolean.TRUE.equals(activeOnly)
                ? amenityRepository.findAllByCondominiumIdAndActiveOrderByName(condominiumId, true)
                : amenityRepository.findAllByCondominiumIdOrderByName(condominiumId);

        return amenities.stream().map(AmenityResponse::from).toList();
    }

    public AmenityDetailResponse getDetail(UUID id, UUID condominiumId) {
        Amenity amenity = findOrThrow(id, condominiumId);

        var schedules = scheduleRepository.findAllByAmenityIdOrderByDayOfWeek(id);
        var exceptions = exceptionRepository
                .findAllByAmenityIdAndDateGreaterThanEqualOrderByDate(id, LocalDate.now());
        var bookings = bookingRepository.findByAmenityIdAndDateGreaterThanEqualAndStatus(id, LocalDate.now(),
                BookingStatus.PENDENTE);

        return AmenityDetailResponse.from(amenity, schedules, exceptions, bookings);
    }

    @Transactional(readOnly = true)
    public AmenityDetailResponseAdmin getDetailAdmin(UUID id, UUID condominiumId) {
        Amenity amenity = findOrThrow(id, condominiumId);

        var schedules = scheduleRepository.findAllByAmenityIdOrderByDayOfWeek(id);
        var exceptions = exceptionRepository
                .findAllByAmenityIdAndDateGreaterThanEqualOrderByDate(id, LocalDate.now());
        var bookings = bookingRepository.findAllByAmenityId(id);

        return AmenityDetailResponseAdmin.from(amenity, schedules, exceptions, bookings);
    }

    @Transactional
    public AmenityResponse update(UUID id, UpdateAmenityRequest request, UUID condominiumId) {
        Amenity existing = findOrThrow(id, condominiumId);

        String newName = request.name() != null ? request.name() : existing.getName();

        if (request.name() != null && !request.name().equalsIgnoreCase(existing.getName())
                && amenityRepository.existsByCondominiumIdAndNameIgnoreCase(condominiumId, request.name())) {
            throw new ServiceException("Já existe um espaço com este nome no condomínio", 409);
        }

        Amenity updated = Amenity.builder()
                .id(existing.getId())
                .condominiumId(existing.getCondominiumId())
                .name(newName)
                .maxCapacity(request.maxCapacity() != null ? request.maxCapacity() : existing.getMaxCapacity())
                .maxBookingDuration(request.maxBookingDuration() != null ? request.maxBookingDuration()
                        : existing.getMaxBookingDuration())
                .description(request.description() != null ? request.description() : existing.getDescription())
                .observation(request.observation() != null ? request.observation() : existing.getObservation())
                .active(request.active() != null ? request.active() : existing.getActive())
                .build();

        if (request.active() != null && !request.active()) {
            List<Booking> conflicts = bookingRepository.findByAmenityIdAndStatus(id, BookingStatus.PENDENTE);

            for (Booking b : conflicts) {
                notificationRepository.save(Notification.builder()
                        .condominiumId(condominiumId)
                        .memberId(b.getMember().getId())
                        .type(NotificationType.DELIVERY)
                        .title("Reserva cancelada")
                        .body("Sua reserva para o espaço " + b.getAmenity().getName()
                                + " em " + b.getDate() + " das " + b.getStartTime() + " às " + b.getEndTime()
                                + " foi cancelada devido a desativação do espaço.")
                        .referenceId(b.getId())
                        .referenceTable("booking")
                        .read(false)
                        .build());

                b.setStatus(BookingStatus.CANCELADO);
                bookingRepository.save(b);
            }
        }

        return AmenityResponse.from(amenityRepository.save(updated));
    }

    @Transactional
    public ScheduleResponse setSchedule(UUID amenityId, short dayOfWeek, SetScheduleRequest request,
            UUID condominiumId) {
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            throw new ServiceException("Dia da semana deve ser entre 0 (domingo) e 6 (sábado)", 422);
        }

        findOrThrow(amenityId, condominiumId);

        if (!request.closed() && (request.opensAt() == null || request.closesAt() == null)) {
            throw new ServiceException("Horários de abertura e fechamento são obrigatórios quando o espaço está aberto",
                    422);
        }

        if (!request.closed() && !request.closesAt().isAfter(request.opensAt())) {
            throw new ServiceException("Horário de fechamento deve ser posterior ao de abertura", 422);
        }

        var existing = scheduleRepository.findByAmenityIdAndDayOfWeek(amenityId, dayOfWeek);

        AmenitySchedule schedule = AmenitySchedule.builder()
                .id(existing.map(AmenitySchedule::getId).orElse(null))
                .amenityId(amenityId)
                .dayOfWeek(dayOfWeek)
                .closed(request.closed())
                .opensAt(request.closed() ? null : request.opensAt())
                .closesAt(request.closed() ? null : request.closesAt())
                .build();

        // verifica se existem reservas conflitantes ao fechar ou alterar horário de
        // funcionamento
        if (request.closed() || (request.opensAt() != null && request.closesAt() != null)) {
            List<Booking> conflicts = bookingRepository.findByAmenityIdAndStatus(
                    amenityId, BookingStatus.PENDENTE);

            for (Booking b : conflicts) {
                if (b.getDate().getDayOfWeek().getValue() % 7 == dayOfWeek) {
                    if (request.closed()
                            || (b.getStartTime().isBefore(request.opensAt())
                                    || b.getEndTime().isAfter(request.closesAt()))) {

                        notificationRepository.save(Notification.builder()
                                .condominiumId(condominiumId)
                                .memberId(b.getMember().getId())
                                .type(NotificationType.DELIVERY)
                                .title("Reserva cancelada")
                                .body("Sua reserva para o espaço " + b.getAmenity().getName()
                                        + " em " + b.getDate() + " das " + b.getStartTime() + " às " + b.getEndTime()
                                        + " foi cancelada devido a alterações no horário de funcionamento.")
                                .referenceId(b.getId())
                                .referenceTable("booking")
                                .read(false)
                                .build());

                        b.setStatus(BookingStatus.CANCELADO);
                        bookingRepository.save(b);
                    }
                }
            }
        }

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(UUID amenityId, short dayOfWeek, UUID condominiumId) {
        findOrThrow(amenityId, condominiumId);
        scheduleRepository.deleteByAmenityIdAndDayOfWeek(amenityId, dayOfWeek);
    }

    @Transactional
    public ExceptionResponse addException(UUID amenityId, CreateExceptionRequest request, UUID condominiumId) {
        findOrThrow(amenityId, condominiumId);

        if (exceptionRepository.existsByAmenityIdAndDate(amenityId, request.date())) {
            throw new ServiceException("Já existe uma exceção cadastrada para esta data", 409);
        }

        if (!request.closed() && (request.opensAt() == null || request.closesAt() == null)) {
            throw new ServiceException("Horários de abertura e fechamento são obrigatórios quando o espaço está aberto",
                    422);
        }

        if (!request.closed() && !request.closesAt().isAfter(request.opensAt())) {
            throw new ServiceException("Horário de fechamento deve ser posterior ao de abertura", 422);
        }

        AmenityException exception = AmenityException.builder()
                .amenityId(amenityId)
                .date(request.date())
                .closed(request.closed())
                .opensAt(request.closed() ? null : request.opensAt())
                .closesAt(request.closed() ? null : request.closesAt())
                .reason(request.reason())
                .build();

        if (request.closed() || (request.opensAt() != null && request.closesAt() != null)) {
            List<Booking> conflicts = bookingRepository.findByAmenityIdAndDateAndStatus(
                    amenityId, request.date(), BookingStatus.PENDENTE);

            for (Booking b : conflicts) {
                if (request.closed()
                        || (b.getStartTime().isBefore(request.opensAt())
                                || b.getEndTime().isAfter(request.closesAt()))) {

                    notificationRepository.save(Notification.builder()
                            .condominiumId(condominiumId)
                            .memberId(b.getMember().getId())
                            .type(NotificationType.DELIVERY)
                            .title("Reserva cancelada")
                            .body("Sua reserva para o espaço " + b.getAmenity().getName()
                                    + " foi cancelada devido a alterações no horário de funcionamento.")
                            .referenceId(b.getId())
                            .referenceTable("booking")
                            .read(false)
                            .build());

                    b.setStatus(BookingStatus.CANCELADO);
                    bookingRepository.save(b);
                }
            }
        }
        return ExceptionResponse.from(exceptionRepository.save(exception));

    }

    @Transactional
    public void deleteException(UUID exceptionId, UUID condominiumId) {
        AmenityException exception = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new ServiceException("Exceção não encontrada", 404));

        exceptionRepository.delete(exception);
    }

    private Amenity findOrThrow(UUID id, UUID condominiumId) {
        return amenityRepository.findByIdAndCondominiumId(id, condominiumId)
                .orElseThrow(() -> new ServiceException("Espaço não encontrado", 404));
    }
}
