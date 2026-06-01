package com.project.project.domain.dto.response;



public record ProjectMemberResponseDto(
        Long id,
        String name,
        String projectRole
) { }