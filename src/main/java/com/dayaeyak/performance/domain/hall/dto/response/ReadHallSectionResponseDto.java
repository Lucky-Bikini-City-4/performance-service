package com.dayaeyak.performance.domain.hall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReadHallSectionResponseDto(
        @Schema(description = "소속된 공연장 ID", example = "2")
        Long hallId,

        @Schema(description = "공연장 구역 ID", example = "1")
        Long hallSectionId,

        @Schema(description = "구역 이름", example = "101")
        String sectionName,

        @Schema(description = "구역의 좌석수", example = "300")
        Integer seats,

        @Schema(description = "좌석당 가격", example = "156000")
        Integer seatPrice
) {
}
