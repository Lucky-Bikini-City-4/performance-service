package com.dayaeyak.performance.domain.performance.dto.response;

import com.dayaeyak.performance.common.dto.PageInfoDto;

import java.util.List;

public record ReadPerformancePageResponseDto(
        PageInfoDto pageInfo,
        List<ReadPerformanceResponseDto> performances
) {
}
