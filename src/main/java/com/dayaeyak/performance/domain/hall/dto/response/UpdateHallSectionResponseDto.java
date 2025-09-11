package com.dayaeyak.performance.domain.hall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateHallSectionResponseDto(
        @Schema(description = "공연장 구역 ID", example = "1")
        Long hallSectionId
) {
}
