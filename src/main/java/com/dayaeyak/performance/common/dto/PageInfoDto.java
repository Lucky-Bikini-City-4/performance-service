package com.dayaeyak.performance.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageInfoDto(
        @Schema(description = "현재 페이지 번호", example = "1")
        int page,

        @Schema(description = "한 페이지에 보여지는 데이터 수", example = "10")
        int size,

        @Schema(description = "총 데이터 수", example = "120")
        long totalElements,

        @Schema(description = "총 페이지 수", example = "13")
        int totalPages,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean last) {
}