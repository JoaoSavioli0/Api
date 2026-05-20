package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.post.Ticket.CreateTicketRequest;
import com.condolives.api.dto.post.Ticket.RawTicketResponse;
import com.condolives.api.dto.post.Ticket.TicketDetailResponse;
import com.condolives.api.dto.post.Ticket.TicketDetailResponseAdmin;
import com.condolives.api.dto.post.Ticket.TicketResponse;

public interface TicketService {
    RawTicketResponse createTicket(CreateTicketRequest request, List<MultipartFile> imageFiles,
            UUID residentId, UUID condominiumId);
    List<TicketResponse> listTickets(UUID condominiumId);
    TicketDetailResponse getTicket(UUID ticketId, UUID condominiumId);
    TicketDetailResponseAdmin getTicketAdmin(UUID ticketId, UUID condominiumId);
    void deleteTicket(UUID ticketId, UUID residentId, UUID condominiumId, boolean isAdmin);
    TicketDetailResponseAdmin updateStatus(UUID ticketId, String statusStr, UUID condominiumId);
}
