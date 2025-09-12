package com.dayaeyak.performance.domain.cast.dto.response;

import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.performance.entity.Performance;
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
        public static ReadCastResponseDto from(Cast cast){
                List<Long> performances = cast.getPerformanceList().stream()
                        .map(Performance::getPerformanceId)
                        .toList();
                return new ReadCastResponseDto(cast.getCastId(), cast.getCastName(), performances);
        }
}
