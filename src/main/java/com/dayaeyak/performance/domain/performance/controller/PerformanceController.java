package com.dayaeyak.performance.domain.performance.controller;

import com.dayaeyak.performance.domain.performance.dto.request.CreatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreatePerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.service.PerformanceService;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
@Tag(name = "Performance API")
public class PerformanceController {
    private final PerformanceService performanceService;

    @Operation(summary = "Create Performance", description = "새로운 공연을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePerformanceResponseDto>> createPerformance(
            @Validated @RequestBody CreatePerformanceRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED.value(),
                "공연이 생성되었습니다.",
                performanceService.createPerformance(requestDto));
    }
}
