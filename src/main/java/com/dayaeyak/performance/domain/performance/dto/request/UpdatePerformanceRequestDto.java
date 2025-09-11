package com.dayaeyak.performance.domain.performance.dto.request;

import com.dayaeyak.performance.domain.performance.enums.Grade;
import com.dayaeyak.performance.domain.performance.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdatePerformanceRequestDto(
        @Schema(description = "공연 이름", example = "오페라의 유령")
        @Size(max = 100, message = "공연 이름은 100자까지 입력 가능합니다.")
        String performanceName,

        @Schema(description = "공연장 ID", example = "4")
        Long hallId,

        @Schema(description = "출연진 목록", example = "아이유, 박효신, NCT 127")
        String castList,

        @Schema(description = "공연 설명", example = "이거완전쩔어용")
        @Size(max = 255, message = "공연 설명은 255자까지 입력 가능합니다.")
        String description,

        @Schema(description = "공연 타입", example = "MUSICAL")
        Type type,

        @Schema(description = "관람 가능 연령", example = "ALL")
        Grade grade,

        @Schema(description = "공연 시작일", example = "2025-10-01")
        LocalDate startDate,

        @Schema(description = "공연 종료일", example = "2025-10-31")
        LocalDate endDate,

        @Schema(description = "티켓 오픈 일시", example = "2025-09-01T09:00:00")
        LocalDateTime ticketOpenAt,

        @Schema(description = "티켓 마감 일시", example = "2025-10-30T23:59:59")
        LocalDateTime ticketCloseAt
) {
}
