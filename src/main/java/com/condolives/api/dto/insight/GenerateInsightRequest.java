package com.condolives.api.dto.insight;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record GenerateInsightRequest(
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd,
        @NotBlank String analysisDepth,
        @NotEmpty List<String> dataSources) {
}
