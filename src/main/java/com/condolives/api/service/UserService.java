package com.condolives.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.member.MemberDetailResponseAdmin;
import com.condolives.api.dto.member.MemberListResponse;
import com.condolives.api.dto.member.MemberResponse;
import com.condolives.api.dto.member.UpdateMemberRequest;
import com.condolives.api.dto.post.Ticket.TicketResponse;
import com.condolives.api.entity.Post.Comment;
import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.CommentRepository;
import com.condolives.api.repository.Post.LikeRepository;
import com.condolives.api.repository.Post.PostRepository;
import com.condolives.api.repository.Post.Ticket.TicketRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.UserAccountRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png");

    private final CondoMemberRepository memberRepository;
    private final UserAccountRepository userAccountRepository;
    private final ImageStorageService imageStorageService;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findFirstByUserEmail(email)
                .map(m -> User.withUsername(m.getUser().getEmail())
                        .password(m.getUser().getPasswordHash())
                        .authorities("ROLE_RESIDENT")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Membro não encontrado: " + email));
    }

    public List<MemberListResponse> listCollaborators(UUID condominiumId) {
        return memberRepository.findAllByCondominiumIdAndRoleIn(condominiumId, List.of(MemberRole.ADMIN, MemberRole.STAFF))
                .stream()
                .map(MemberListResponse::from)
                .toList();
    }

    @Transactional
    public List<MemberListResponse> listMembers(UUID condominiumId) {
        return memberRepository.findAllByCondominiumIdAndRole(condominiumId, MemberRole.RESIDENT)
                .stream()
                .map(MemberListResponse::from)
                .toList();
    }

    @Transactional
    public MemberListResponse updateMember(UUID memberId, UUID condominiumId, UpdateMemberRequest request) {
        CondoMember member = memberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));

        if (request.unitId() != null) {
            member.setUnitId(request.unitId());
        }
        var user = member.getUser();
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone().isBlank() ? null : request.phone());
        }
        userAccountRepository.save(user);
        return MemberListResponse.from(memberRepository.save(member));
    }

    @Transactional
    public void deactivateMember(UUID memberId, UUID condominiumId) {
        CondoMember member = memberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));
        member.setActive(false);
        memberRepository.save(member);
        postRepository.hideAllByMemberIdAndCondominiumId(memberId, condominiumId);
    }

    @Transactional
    public void activateMember(UUID memberId, UUID condominiumId) {
        CondoMember member = memberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));
        member.setActive(true);
        memberRepository.save(member);
    }

    public MemberResponse getMember(UUID memberId, UUID condominiumId) {
        return memberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .map(MemberResponse::from)
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));
    }

    @Transactional
    public MemberDetailResponseAdmin getMemberAdmin(UUID memberId, UUID condominiumId) {
        List<Ticket> tickets = ticketRepository.findAllByMemberIdAndCondominiumId(memberId, condominiumId);

        List<UUID> ids = tickets.stream().map(Ticket::getId).toList();

        Map<UUID, Long> commentCounts = commentRepository.countByPostIdIn(ids).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> (Long) r[1]));
        Map<UUID, Long> likeCounts = likeRepository.countByPostIdIn(ids).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> (Long) r[1]));

        List<TicketResponse> ticketsResponse = tickets.stream()
                .map(t -> TicketResponse.from(t,
                        commentCounts.getOrDefault(t.getId(), 0L),
                        likeCounts.getOrDefault(t.getId(), 0L)))
                .toList();

        List<Comment> comments = commentRepository.findAllByMemberIdAndCondominiumId(memberId, condominiumId);

        return memberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .map(m -> MemberDetailResponseAdmin.from(m, comments, ticketsResponse, commentCounts, likeCounts))
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));
    }

    public MemberResponse updateProfile(UUID memberId, UUID condominiumId, MultipartFile avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            if (avatar.getSize() > MAX_IMAGE_SIZE) {
                throw new ServiceException("Tamanho máximo de imagem excedido", 422);
            }
            if (!ALLOWED_IMAGE_TYPES.contains(avatar.getContentType())) {
                throw new ServiceException("Apenas imagens JPEG ou PNG são permitidas", 422);
            }
        }

        List<String> imageUrls = imageStorageService.uploadImages(List.of(avatar));

        return memberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .map(member -> {
                    var user = member.getUser();
                    user.setAvatarUrl(imageUrls.get(0));
                    userAccountRepository.save(user);
                    return MemberResponse.from(member);
                })
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));
    }
}
