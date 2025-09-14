package com.dayaeyak.performance.domain.performance.dto.response;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReadPerformanceSectionResponseDto(
        @Schema(description = "공연 ID", example = "1")
        Long performanceId,

        @Schema(description = "공연 회차 ID", example = "3")
        Long sessionId,

        @Schema(description = "회차 구역 ID", example = "6")
        Long sectionId,

        @Schema(description = "구역명", example = "101")
        String sectionName,

        @Schema(description = "남은 좌석 수", example = "210")
        Integer remainingSeats
) {

    public static ReadPerformanceSectionResponseDto from(PerformanceSection performanceSection) {
        return ReadPerformanceSectionResponseDto.builder()
                .performanceId(performanceSection.getPerformanceSession().getPerformance().getPerformanceId())
                .sessionId(performanceSection.getPerformanceSession().getPerformanceSessionId())
                .sectionId(performanceSection.getPerformanceSectionId())
                .sectionName(performanceSection.getSectionName())
                .remainingSeats(performanceSection.getRemainingSeats())
                .build();
    }
}
