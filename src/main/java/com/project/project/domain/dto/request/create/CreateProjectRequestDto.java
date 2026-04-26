package com.project.project.domain.dto.request.create;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequestDto(
        @NotBlank(message = "Name of Project required")
        String name,
        String description) {
}
