package com.dayaeyak.performance.domain.cast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateCastResponseDto(
        @Schema(description = "출연진 ID", example = "1")
        Long castId,

        @Schema(description = "출연진 이름", example = "아이유")
        String castName
) {
}
