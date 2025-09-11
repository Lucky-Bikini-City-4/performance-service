package com.dayaeyak.performance.domain.hall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateHallResponseDto(
        @Schema(description = "공연장 ID", example = "1")
        Long hallId
) {
}
