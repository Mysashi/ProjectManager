package com.project.project.domain.dto.request.create;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskRequestDto(

        @NotNull(message = "Project id required")
        Long projectId,

        @NotBlank(message = "Project name is required")
        String name,

        String description,
        Long userId) {
}
