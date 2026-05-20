package com.condolives.api.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.notice.CreateNoticeRequest;
import com.condolives.api.dto.notice.NoticeResponse;
import com.condolives.api.dto.notice.UnreadNoticeResponse;
import com.condolives.api.entity.Post.Notice;
import com.condolives.api.entity.Post.NoticeRead;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.enums.Importance;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.NoticeReadRepository;
import com.condolives.api.repository.Post.NoticeRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private static final Set<String> VALID_TARGET_TYPES = Set.of("all", "street", "block", "unit");
    private static final Set<String> VALID_IMPORTANCES = Set.of("high", "medium", "low");

    private final NoticeRepository noticeRepository;
    private final NoticeReadRepository noticeReadRepository;
    private final CondoMemberRepository condoMemberRepository;
    private final UnitRepository unitRepository;

    public List<NoticeResponse> list(UUID condominiumId) {
        return noticeRepository.findByCondominiumIdOrderByCreatedAtDesc(condominiumId)
                .stream()
                .map(n -> NoticeResponse.from(n,
                        noticeReadRepository.countByNoticeId(n.getId()),
                        countTarget(condominiumId, n.getTargetType(), n.getTargetValue())))
                .toList();
    }

    @Transactional
    public NoticeResponse create(CreateNoticeRequest request, UUID condominiumId, UUID memberId) {
        validateImportance(request.importance());
        validateTargetType(request.targetType(), request.targetValue());

        Notice notice = noticeRepository.save(Notice.builder()
                .condominiumId(condominiumId)
                .memberId(memberId)
                .visible(true)
                .title(request.title())
                .description(request.description())
                .importance(Importance.fromDbValue(request.importance()))
                .targetType(request.targetType())
                .targetValue("all".equals(request.targetType()) ? null : request.targetValue())
                .build());

        return NoticeResponse.from(notice, 0,
                countTarget(condominiumId, notice.getTargetType(), notice.getTargetValue()));
    }

    @Transactional
    public NoticeResponse update(UUID id, CreateNoticeRequest request, UUID condominiumId) {
        validateImportance(request.importance());
        validateTargetType(request.targetType(), request.targetValue());

        Notice notice = noticeRepository.findByIdAndCondominiumId(id, condominiumId)
                .orElseThrow(() -> new ServiceException("Aviso não encontrado", 404));

        notice.setTitle(request.title());
        notice.setDescription(request.description());
        notice.setImportance(Importance.fromDbValue(request.importance()));
        notice.setTargetType(request.targetType());
        notice.setTargetValue("all".equals(request.targetType()) ? null : request.targetValue());

        Notice saved = noticeRepository.save(notice);
        return NoticeResponse.from(saved,
                noticeReadRepository.countByNoticeId(saved.getId()),
                countTarget(condominiumId, saved.getTargetType(), saved.getTargetValue()));
    }

    @Transactional
    public void delete(UUID id, UUID condominiumId) {
        Notice notice = noticeRepository.findByIdAndCondominiumId(id, condominiumId)
                .orElseThrow(() -> new ServiceException("Aviso não encontrado", 404));
        noticeRepository.delete(notice);
    }

    // ── Resident endpoints ────────────────────────────────────────────────────

    public List<UnreadNoticeResponse> listUnread(UUID memberId, UUID condominiumId) {
        CondoMember member = condoMemberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .orElseThrow(() -> new ServiceException("Morador não encontrado", 404));
        String unitAddress = member.getUnitAddress() != null ? member.getUnitAddress() : "";
        return noticeRepository.findUnreadForMember(condominiumId, memberId, unitAddress)
                .stream()
                .map(UnreadNoticeResponse::from)
                .toList();
    }

    @Transactional
    public void markAsRead(UUID noticeId, UUID memberId, UUID condominiumId) {
        noticeRepository.findByIdAndCondominiumId(noticeId, condominiumId)
                .orElseThrow(() -> new ServiceException("Aviso não encontrado", 404));
        if (!noticeReadRepository.existsByNoticeIdAndMemberId(noticeId, memberId)) {
            noticeReadRepository.save(NoticeRead.builder()
                    .noticeId(noticeId)
                    .memberId(memberId)
                    .readAt(Instant.now())
                    .build());
        }
    }

    public List<UnreadNoticeResponse> listAllForMember(UUID memberId, UUID condominiumId) {
        CondoMember member = condoMemberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .orElseThrow(() -> new ServiceException("Morador não encontrado", 404));
        String unitAddress = member.getUnitAddress() != null ? member.getUnitAddress() : "";
        return noticeRepository.findAllForMember(condominiumId, unitAddress)
                .stream()
                .map(UnreadNoticeResponse::from)
                .toList();
    }

    // ── Admin helper ──────────────────────────────────────────────────────────

    public List<String> getTargetOptions(UUID condominiumId) {
        return unitRepository.findAllByCondominiumIdOrderByTypeAscIdentifierAsc(condominiumId)
                .stream()
                .map(u -> u.getDisplayAddress())
                .distinct()
                .toList();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private long countTarget(UUID condominiumId, String targetType, String targetValue) {
        if ("all".equals(targetType) || targetValue == null || targetValue.isBlank()) {
            return condoMemberRepository.countByCondominiumIdAndRoleNotIn(condominiumId,
                    List.of(MemberRole.ADMIN, MemberRole.STAFF));
        }
        return condoMemberRepository.countByCondominiumIdAndUnitAddressContaining(condominiumId, targetValue);
    }

    private void validateImportance(String importance) {
        if (!VALID_IMPORTANCES.contains(importance)) {
            throw new ServiceException("Importância inválida. Use: high, medium, low", 422);
        }
    }

    private void validateTargetType(String targetType, String targetValue) {
        if (!VALID_TARGET_TYPES.contains(targetType)) {
            throw new ServiceException("Tipo de destinatário inválido. Use: all, street, block, unit", 422);
        }
        if (!"all".equals(targetType) && (targetValue == null || targetValue.isBlank())) {
            throw new ServiceException("targetValue é obrigatório para o tipo: " + targetType, 422);
        }
    }
}
