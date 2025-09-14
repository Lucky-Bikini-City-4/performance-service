package com.dayaeyak.performance.domain.performance.dto.response;

import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.enums.Grade;
import com.dayaeyak.performance.domain.performance.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Builder
public record ReadPerformanceResponseDto(
        @Schema(description = "공연 ID", example = "1")
        Long performanceId,

        @Schema(description = "판매자 ID", example = "3")
        Long sellerId,

        @Schema(description = "공연 이름", example = "오페라의 유령")
        String performanceName,

        @Schema(description = "공연장 ID", example = "4")
        Long hallId,

        @Schema(description = "출연진 목록", example = "아이유,박효신,NCT 127")
        List<CastDto> castList,

        @Schema(description = "공연 설명", example = "이거완전쩔어용")
        String description,

        @Schema(description = "공연 타입", example = "MUSICAL")
        Type type,

        @Schema(description = "관람 가능 연령", example = "ALL")
        Grade grade,

        @Schema(description = "공연 시작일", example = "2025-10-01")
        Date startDate,

        @Schema(description = "공연 종료일", example = "2025-10-31")
        Date endDate,

        @Schema(description = "티켓 오픈 일시", example = "2025-09-01T09:00:00")
        Timestamp ticketOpenAt,

        @Schema(description = "티켓 마감 일시", example = "2025-10-30T23:59:59")
        Timestamp ticketCloseAt
) {

    public static ReadPerformanceResponseDto from(Performance performance) {
        List<CastDto> castDtos = performance.getCastList().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(CastDto::from)
                .toList();

        return ReadPerformanceResponseDto.builder()
                .performanceId(performance.getPerformanceId())
                .sellerId(performance.getSellerId())
                .performanceName(performance.getPerformanceName())
                .hallId(performance.getHall().getHallId())
                .castList(castDtos)
                .description(performance.getDescription())
                .type(performance.getType())
                .grade(performance.getGrade())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .ticketOpenAt(performance.getTicketOpenAt())
                .ticketCloseAt(performance.getTicketCloseAt())
                .build();
    }
}
