package com.condolives.api.dto.post.Ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTicketRequest {
    @NotBlank
    private String title;
    private String description;
    private String location;
    @NotBlank
    private String categoryIds; // JSON array string: ["uuid1","uuid2"]
    private boolean showName = true;
}
