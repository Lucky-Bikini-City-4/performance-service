package com.dayaeyak.performance.domain.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateSeatSoldOutRequestDto(
        @Schema(description = "좌석 품절 여부", example = "true")
        boolean isSoldOut
) {
}
