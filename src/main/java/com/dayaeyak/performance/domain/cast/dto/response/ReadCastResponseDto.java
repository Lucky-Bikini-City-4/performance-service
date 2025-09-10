package com.dayaeyak.performance.domain.cast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReadCastResponseDto (
        @Schema(description = "출연진 ID", example = "1")
        Long castId,

        @Schema(description = "출연진 이름", example = "아이유")
        String castName,

        @Schema(description = "출연 공연 목록 (공연 ID)")
        List<Long> performanceList
){
}
