package com.condolives.api.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestService {

    // private final TicketRepository ticketRepository;
    // private final CategoryRepository categoryRepository;

    // @Transactional
    // public TicketResponse createTicket(CreateTicketRequest request, UUID
    // residentId, UUID condominiumId) {
    // List<Category> categories = categoryRepository
    // .findAllByIdInAndCondominiumId(request.categoryIds(), condominiumId);

    // if (categories.size() != request.categoryIds().size()) {
    // throw new ServiceException("Uma ou mais categorias não existem neste
    // condomínio", 422);
    // }

    // Ticket ticket = Ticket.builder()
    // .condominiumId(condominiumId)
    // .residentId(residentId)
    // .visible(true)
    // .title(request.title())
    // .description(request.description())
    // .location(request.location())
    // .status(PostStatus.ABERTO)
    // .categories(categories)
    // .build();

    // return TicketResponse.from(ticketRepository.save(ticket));
    // }
}
