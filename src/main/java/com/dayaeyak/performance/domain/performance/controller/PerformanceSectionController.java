package com.dayaeyak.performance.domain.performance.controller;

import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformanceSectionResponseDto;
import com.dayaeyak.performance.domain.performance.service.PerformanceSectionService;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/performances/{performanceId}/sessions/{sessionId}/sections")
@RequiredArgsConstructor
@Tag(name = "Performance Section API")
public class PerformanceSectionController {
    private final PerformanceSectionService performanceSectionService;

    @Operation(summary = "Read Performance Section", description = "단건 회차 구역 정보를 조회합니다.")
    @GetMapping("/{sectionId}")
    public ResponseEntity<ApiResponse<ReadPerformanceSectionResponseDto>> readPerformanceSection(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long sectionId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "해당 회차 구역 정보를 조회합니다.",
                performanceSectionService.readPerformanceSection(performanceId, sessionId, sectionId));
    }

    @Operation(summary = "Read All Performance Sections", description = "공연 회차의 전체 구역을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadPerformanceSectionResponseDto>>> readPerformanceSections(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "해당 공연 회차의 전체 구역 정보를 조회합니다.",
                performanceSectionService.readPerformanceSections(performanceId, sessionId));
    }
}
