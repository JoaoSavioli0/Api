package com.condolives.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class RequestController {

    // private final RequestService requestService;

    // @PostMapping
    // public ResponseEntity<TicketResponse> create(
    // @RequestBody @Valid CreateTicketRequest request,
    // Authentication authentication) {

    // UUID residentId = UUID.fromString((String) authentication.getPrincipal());
    // UUID condominiumId = condominiumId(authentication);

    // return ResponseEntity
    // .status(HttpStatus.CREATED)
    // .body(requestService.createTicket(request, residentId, condominiumId));
    // }

    // @SuppressWarnings("unchecked")
    // private UUID condominiumId(Authentication authentication) {
    // var details = (Map<String, Object>) authentication.getDetails();
    // return UUID.fromString((String) details.get("condominiumId"));
    // }
}
