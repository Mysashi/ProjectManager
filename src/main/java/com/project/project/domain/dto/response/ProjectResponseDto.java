package com.project.project.domain.dto.response;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record ProjectResponseDto(Long id, String name, String description, String status,
                                 Date dateOfCreation, LocalDateTime dateOfDeletion,
                                 List<ProjectMemberResponseDto> members) {
}
