package com.project.project.domain.dto.response;

import com.project.project.domain.entity.TaskStatus;
import java.util.Date;

public record TaskResponseDto(
        Long id,
        String name,
        String description,
        TaskStatus status,
        Long projectId,
        Long assigneeId,
        Date dateOfCreation
) {}