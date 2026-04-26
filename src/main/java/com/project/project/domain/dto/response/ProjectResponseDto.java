package com.project.project.domain.dto.response;

import java.util.Date;

public record ProjectResponseDto(Long id, String name, String description, String status, Date dateOfCreation) {
}
