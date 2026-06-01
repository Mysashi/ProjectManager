package com.project.project.domain.dto.request.update;

public record TransferProjectRequestDto(Long projectId, Long userSourceId, Long userDestId) {
}
