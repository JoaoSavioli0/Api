package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.auth.LoginResponse;
import com.condolives.api.dto.invite.ClaimInviteRequest;
import com.condolives.api.dto.invite.CreateInviteRequest;
import com.condolives.api.dto.invite.InvitePreviewResponse;
import com.condolives.api.dto.invite.InviteResponse;

public interface InviteService {
    InviteResponse createInvite(CreateInviteRequest request, UUID condominiumId);
    List<InviteResponse> listInvites(UUID condominiumId);
    InvitePreviewResponse getPreview(String token);
    LoginResponse claimInvite(String token, ClaimInviteRequest request);
    void revokeInvite(UUID inviteId, UUID condominiumId);
}
