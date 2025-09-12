package com.dayaeyak.performance.domain.performance.controller;

import com.dayaeyak.performance.domain.performance.dto.request.ChangePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.CreatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.UpdatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreatePerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformancePageResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.enums.Type;
import com.dayaeyak.performance.domain.performance.service.PerformanceService;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Update Performance", description = "공연 정보를 수정합니다.")
    @PatchMapping("/{performanceId}")
    public ResponseEntity<ApiResponse<CreatePerformanceResponseDto>> updatePerformance(
            @PathVariable Long performanceId,
            @RequestBody UpdatePerformanceRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연을 수정했습니다.",
                performanceService.updatePerformance(performanceId, requestDto));
    }

    @Operation(summary = "Change IsActivated", description = "공연의 활성화 상태를 변경합니다")
    @PatchMapping("/{performanceId}/activation")
    public ResponseEntity<ApiResponse<Boolean>> changeIsActivated(
            @PathVariable Long performanceId,
            @Validated @RequestBody ChangePerformanceRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연의 활성화 상태가 다음과 같이 변경되었습니다.",
                performanceService.changeIsActivated(performanceId, requestDto));
    }

    @Operation(summary = "Read Performance", description = "단건 공연을 조회합니다.")
    @GetMapping("/{performanceId}")
    public ResponseEntity<ApiResponse<ReadPerformanceResponseDto>> readPerformance(@PathVariable Long performanceId) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연 정보를 조회합니다.",
                performanceService.readPerformance(performanceId));
    }

    @Operation(summary = "Read Performance List", description = "공연 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<ReadPerformancePageResponseDto>> readPerformanceList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Type type) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연 목록을 조회합니다.",
                performanceService.readPerformanceList(page, size, type));
    }

    @Operation(summary = "Delete Performance", description = "공연을 삭제합니다.")
    @DeleteMapping("/{performanceId}")
    public ResponseEntity<ApiResponse<Void>> deletePerformance(@PathVariable Long performanceId) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연을 삭제했습니다.",
                performanceService.deletePerformance(performanceId));
    }
}
