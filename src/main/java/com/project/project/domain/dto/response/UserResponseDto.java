package com.project.project.domain.dto.response;


public record UserResponseDto(Long id,
                              String username,
                              String refreshToken,
                              String accessToken) {
}
