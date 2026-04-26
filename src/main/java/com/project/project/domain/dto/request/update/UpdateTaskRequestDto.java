package com.project.project.domain.dto.request.update;

import com.project.project.domain.entity.TaskStatus;

public record UpdateTaskRequestDto(Long id, Long projectId, String name, String description, TaskStatus status) {
}
