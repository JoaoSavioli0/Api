package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.member.MemberDetailResponseAdmin;
import com.condolives.api.dto.member.MemberListResponse;
import com.condolives.api.dto.member.MemberResponse;
import com.condolives.api.dto.member.UpdateMemberRequest;

public interface UserService extends UserDetailsService {
    List<MemberListResponse> listCollaborators(UUID condominiumId);
    List<MemberListResponse> listMembers(UUID condominiumId);
    MemberListResponse updateMember(UUID memberId, UUID condominiumId, UpdateMemberRequest request);
    void deactivateMember(UUID memberId, UUID condominiumId);
    void activateMember(UUID memberId, UUID condominiumId);
    MemberResponse getMember(UUID memberId, UUID condominiumId);
    MemberDetailResponseAdmin getMemberAdmin(UUID memberId, UUID condominiumId);
    MemberResponse updateProfile(UUID memberId, UUID condominiumId, MultipartFile avatar);
}
