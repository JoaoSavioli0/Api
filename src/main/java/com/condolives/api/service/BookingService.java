package com.condolives.api.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.booking.BookingResponse;
import com.condolives.api.dto.booking.CreateBookingRequest;
import com.condolives.api.entity.Amenity.Amenity;
import com.condolives.api.entity.Amenity.AmenityException;
import com.condolives.api.entity.Amenity.AmenitySchedule;
import com.condolives.api.entity.Amenity.Booking;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.Notification;
import com.condolives.api.enums.BookingStatus;
import com.condolives.api.enums.NotificationType;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Amenity.AmenityExceptionRepository;
import com.condolives.api.repository.Amenity.AmenityRepository;
import com.condolives.api.repository.Amenity.AmenityScheduleRepository;
import com.condolives.api.repository.Amenity.BookingRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final AmenityRepository amenityRepository;
    private final AmenityScheduleRepository scheduleRepository;
    private final AmenityExceptionRepository exceptionRepository;
    private final BookingRepository bookingRepository;
    private final CondoMemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public BookingResponse create(CreateBookingRequest request, UUID memberId, UUID condominiumId) {
        Amenity amenity = amenityRepository
                .findByIdAndCondominiumId(request.amenityId(), condominiumId)
                .orElseThrow(() -> new ServiceException("Espaço não encontrado", 404));

        CondoMember member = memberRepository
                .findByIdAndCondominiumId(memberId, condominiumId)
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));

        if (!amenity.getActive()) {
            throw new ServiceException("Espaço inativo, reservas não são aceitas", 422);
        }

        if (request.endTime().equals(request.startTime()) || request.endTime().isBefore(request.startTime())) {
            throw new ServiceException("Horário de término deve ser posterior ao de início", 422);
        }

        LocalTime opensAt;
        LocalTime closesAt;

        // Exceção do dia tem prioridade sobre o horário semanal
        var exception = exceptionRepository.findByAmenityIdAndDate(amenity.getId(), request.date());
        if (exception.isPresent()) {
            AmenityException ex = exception.get();
            if (ex.getClosed()) {
                throw new ServiceException("Espaço fechado nesta data: " + formatReason(ex.getReason()), 422);
            }
            opensAt = ex.getOpensAt();
            closesAt = ex.getClosesAt();
        } else {
            short dayOfWeek = toPosixDayOfWeek(request.date().getDayOfWeek());
            AmenitySchedule schedule = scheduleRepository
                    .findByAmenityIdAndDayOfWeek(amenity.getId(), dayOfWeek)
                    .orElseThrow(() -> new ServiceException(
                            "Espaço não tem horário configurado para este dia da semana", 422));

            if (schedule.getClosed()) {
                throw new ServiceException("Espaço fechado neste dia da semana", 422);
            }
            opensAt = schedule.getOpensAt();
            closesAt = schedule.getClosesAt();
        }

        if (opensAt != null && request.startTime().isBefore(opensAt)) {
            throw new ServiceException("Horário de início anterior à abertura do espaço (" + opensAt + ")", 422);
        }
        if (closesAt != null && request.endTime().isAfter(closesAt)) {
            throw new ServiceException("Horário de término posterior ao fechamento do espaço (" + closesAt + ")", 422);
        }

        if (request.guestCount() != null && request.guestCount() > amenity.getMaxCapacity()) {
            throw new ServiceException(
                    "Número de convidados (" + request.guestCount() + ") excede a capacidade do espaço ("
                            + amenity.getMaxCapacity() + ")",
                    422);
        }

        List<Booking> conflicts = bookingRepository.findOverlapping(
                amenity.getId(), request.date(), request.startTime(), request.endTime(), BookingStatus.CANCELADO);
        if (!conflicts.isEmpty()) {
            throw new ServiceException("Já existe uma reserva neste horário para o espaço selecionado", 409);
        }

        Booking booking = Booking.builder()
                .amenity(amenity)
                .member(member)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .guestCount(request.guestCount())
                .status(BookingStatus.PENDENTE)
                .build();

        return BookingResponse.from(bookingRepository.save(booking), amenity);
    }

    public List<BookingResponse> listAll(UUID condominiumId) {
        return bookingRepository.findAllByCondominiumId(condominiumId)
                .stream()
                .map(b -> amenityRepository
                        .findByIdAndCondominiumId(b.getAmenity().getId(), condominiumId)
                        .map(a -> BookingResponse.from(b, a))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public List<BookingResponse> listMyBookings(UUID memberId, UUID condominiumId) {
        return bookingRepository
                .findByMemberIdOrderByDateDescStartTimeDesc(memberId)
                .stream()
                .map(b -> amenityRepository
                        .findByIdAndCondominiumId(b.getAmenity().getId(), condominiumId)
                        .map(a -> BookingResponse.from(b, a))
                        .orElse(null))
                .filter(b -> b != null)
                .toList();
    }

    @Transactional
    public void setPending(UUID id, UUID condominiumId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Reserva não encontrada", 404));

        if (!booking.getAmenity().getCondominiumId().equals(condominiumId)) {
            throw new ServiceException("Reserva não encontrada", 404);
        }

        booking.setStatus(BookingStatus.PENDENTE);
        bookingRepository.save(booking);

        notificationRepository.save(Notification.builder()
                .condominiumId(condominiumId)
                .memberId(booking.getMember().getId())
                .type(NotificationType.DELIVERY)
                .title("Reserva reativada")
                .body("Sua reserva para o espaço " + booking.getAmenity().getName()
                        + " em " + booking.getDate() + " das " + booking.getStartTime() + " às " + booking.getEndTime()
                        + " foi reativada.")
                .referenceId(booking.getId())
                .referenceTable("booking")
                .read(false)
                .build());
    }

    @Transactional
    public void setRejected(UUID id, UUID condominiumId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Reserva não encontrada", 404));

        if (!booking.getAmenity().getCondominiumId().equals(condominiumId)) {
            throw new ServiceException("Reserva não encontrada", 404);
        }

        booking.setStatus(BookingStatus.REPROVADO);
        bookingRepository.save(booking);

        notificationRepository.save(Notification.builder()
                .condominiumId(condominiumId)
                .memberId(booking.getMember().getId())
                .type(NotificationType.DELIVERY)
                .title("Reserva cancelada")
                .body("Sua reserva para o espaço " + booking.getAmenity().getName()
                        + " em " + booking.getDate() + " das " + booking.getStartTime() + " às " + booking.getEndTime()
                        + " foi cancelada devido a alterações no horário de funcionamento.")
                .referenceId(booking.getId())
                .referenceTable("booking")
                .read(false)
                .build());
    }

    // Converte DayOfWeek ISO (Mon=1..Sun=7) para POSIX (Sun=0..Sat=6)
    private short toPosixDayOfWeek(DayOfWeek dayOfWeek) {
        return (short) (dayOfWeek.getValue() % 7);
    }

    private String formatReason(String reason) {
        return reason != null ? reason : "sem motivo informado";
    }
}
