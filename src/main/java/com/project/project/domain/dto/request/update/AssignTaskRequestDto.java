package com.project.project.domain.dto.request.update;

public record AssignTaskRequestDto(Long taskId,
                                   Long projectId,
                                   Long userId) {
}
