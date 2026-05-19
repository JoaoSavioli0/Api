package com.condolives.api.repository.Post.Ticket;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.Post.Ticket.TicketImage;

public interface TicketImageRepository extends JpaRepository<TicketImage, UUID> {
}
