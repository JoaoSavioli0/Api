package com.condolives.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.post.Ticket.CreateTicketRequest;
import com.condolives.api.dto.post.Ticket.RawTicketResponse;
import com.condolives.api.dto.post.Ticket.TicketDetailResponse;
import com.condolives.api.dto.post.Ticket.TicketDetailResponseAdmin;
import com.condolives.api.dto.post.Ticket.TicketResponse;
import com.condolives.api.entity.Post.Comment;
import com.condolives.api.entity.Post.Ticket.Category;
import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.entity.Post.Ticket.TicketImage;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.enums.PostStatus;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.CommentRepository;
import com.condolives.api.repository.Post.LikeRepository;
import com.condolives.api.repository.Post.Ticket.CategoryRepository;
import com.condolives.api.repository.Post.Ticket.TicketImageRepository;
import com.condolives.api.repository.Post.Ticket.TicketRepository;
import com.condolives.api.repository.User.CondoMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private static final int MAX_IMAGES_PER_TICKET = 5;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png");

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final TicketImageRepository ticketImageRepository;
    private final ImageStorageService imageStorageService;
    private final CondoMemberRepository memberRepository;

    @Transactional
    public RawTicketResponse createTicket(CreateTicketRequest request, List<MultipartFile> imageFiles,
            UUID residentId, UUID condominiumId) {

        List<UUID> categoryIds = parseCategoryIds(request.getCategoryIds());

        List<Category> categories = categoryRepository
                .findAllByIdInAndCondominiumId(categoryIds, condominiumId);

        if (categories.size() != categoryIds.size()) {
            throw new ServiceException("Uma ou mais categorias não existem neste condomínio", 422);
        }

        if (imageFiles != null && !imageFiles.isEmpty()) {
            if (imageFiles.size() > MAX_IMAGES_PER_TICKET) {
                throw new ServiceException("Máximo de " + MAX_IMAGES_PER_TICKET + " imagens por ticket", 422);
            }
            for (MultipartFile f : imageFiles) {
                if (!ALLOWED_IMAGE_TYPES.contains(f.getContentType())) {
                    throw new ServiceException("Apenas imagens JPEG ou PNG são permitidas", 422);
                }
            }
        }

        Ticket ticket = Ticket.builder()
                .condominiumId(condominiumId)
                .memberId(residentId)
                .visible(true)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .status(PostStatus.ABERTO)
                .categories(categories)
                .showName(request.isShowName())
                .build();

        Ticket saved = ticketRepository.save(ticket);

        List<String> imageUrls = List.of();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageUrls = imageStorageService.uploadImages(imageFiles);
            List<TicketImage> images = imageUrls.stream()
                    .map(url -> TicketImage.builder().ticket(saved).url(url).build())
                    .toList();
            ticketImageRepository.saveAll(images);
        }

        return RawTicketResponse.from(saved, imageUrls);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listTickets(UUID condominiumId) {
        List<Ticket> tickets = ticketRepository.findAllByCondominiumId(condominiumId);
        if (tickets.isEmpty())
            return List.of();

        List<UUID> ids = tickets.stream().map(Ticket::getId).toList();

        Map<UUID, Long> commentCounts = commentRepository.countByPostIdIn(ids).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> (Long) r[1]));
        Map<UUID, Long> likeCounts = likeRepository.countByPostIdIn(ids).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> (Long) r[1]));

        return tickets.stream()
                .map(t -> TicketResponse.from(t,
                        commentCounts.getOrDefault(t.getId(), 0L),
                        likeCounts.getOrDefault(t.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicket(UUID ticketId, UUID condominiumId) {
        Ticket ticket = ticketRepository.findWithMemberByIdAndCondominiumId(ticketId, condominiumId)
                .orElseThrow(() -> new ServiceException("Ticket não encontrado", 404));

        List<Comment> comments = commentRepository.findAllByPostId(ticketId);
        long likeCount = likeRepository.countByPostId(ticketId);

        List<UUID> commentMemberIds = comments.stream()
                .map(Comment::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<UUID, CondoMember> memberMap = new HashMap<>();
        if (!commentMemberIds.isEmpty()) {
            memberRepository.findAllById(commentMemberIds)
                    .forEach(m -> memberMap.put(m.getId(), m));
        }

        return TicketDetailResponse.from(ticket, likeCount, comments, memberMap);
    }

    @Transactional(readOnly = true)
    public TicketDetailResponseAdmin getTicketAdmin(UUID ticketId, UUID condominiumId) {
        Ticket ticket = ticketRepository.findWithMemberByIdAndCondominiumId(ticketId, condominiumId)
                .orElseThrow(() -> new ServiceException("Ticket não encontrado", 404));

        List<Comment> comments = commentRepository.findAllByPostId(ticketId);
        long likeCount = likeRepository.countByPostId(ticketId);

        return TicketDetailResponseAdmin.from(ticket, likeCount, comments);
    }

    @Transactional
    public void deleteTicket(UUID ticketId, UUID residentId, UUID condominiumId, boolean isAdmin) {
        Objects.requireNonNull(condominiumId, "condominiumId is null");
        Objects.requireNonNull(residentId, "residentId is null");

        Ticket ticket = ticketRepository.findByIdAndCondominiumId(ticketId, condominiumId)
                .orElseThrow(() -> new ServiceException("Ticket não encontrado", 404));

        if (ticket.getMemberId() != residentId && !isAdmin) {
            throw new ServiceException("Usuário não tem permissão", 403);
        }

        ticket.setVisible(false);

        ticketRepository.save(ticket);
    }

    private List<UUID> parseCategoryIds(String raw) {
        try {
            String trimmed = raw.trim();
            if (trimmed.startsWith("[")) {
                trimmed = trimmed.substring(1, trimmed.length() - 1);
            }
            return java.util.Arrays.stream(trimmed.split(","))
                    .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                    .filter(s -> !s.isEmpty())
                    .map(UUID::fromString)
                    .toList();
        } catch (Exception e) {
            throw new ServiceException("categoryIds deve ser um array JSON de UUIDs: [\"uuid1\",\"uuid2\"]", 422);
        }
    }

    @Transactional
    public TicketDetailResponseAdmin updateStatus(UUID ticketId, String statusStr, UUID condominiumId) {
        Ticket ticket = ticketRepository.findWithMemberByIdAndCondominiumId(ticketId, condominiumId)
                .orElseThrow(() -> new ServiceException("Ticket não encontrado", 404));

        PostStatus status;
        try {
            status = PostStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Status inválido. Valores permitidos: ABERTO, EM_ANDAMENTO, RESOLVIDO", 422);
        }

        ticket.setStatus(status);
        ticketRepository.save(ticket);

        List<Comment> comments = commentRepository.findAllByPostId(ticketId);
        long likeCount = likeRepository.countByPostId(ticketId);
        return TicketDetailResponseAdmin.from(ticket, likeCount, comments);
    }
}
