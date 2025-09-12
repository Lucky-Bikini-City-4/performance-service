package com.dayaeyak.performance.domain.performance.controller;

import com.dayaeyak.performance.domain.performance.dto.request.CreateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.UpdateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreateSessionResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadSessionResponseDto;
import com.dayaeyak.performance.domain.performance.service.PerformanceSessionService;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/performances/{performanceId}/sessions")
@RequiredArgsConstructor
@Tag(name = "Performance Session API")
public class PerformanceSessionController {
    private final PerformanceSessionService performanceSessionService;

    @Operation(summary = "Create Performance Session", description = "공연 회차를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateSessionResponseDto>> createSession(
            @PathVariable Long performanceId,
            @Validated @RequestBody CreateSessionRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED.value(),
                "공연 회차가 생성되었습니다.",
                performanceSessionService.createSession(performanceId, requestDto));
    }

    @Operation(summary = "Update Performance Session", description = "공연 회차 정보를 수정합니다.")
    @PatchMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<CreateSessionResponseDto>> updateSession(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @Validated @RequestBody UpdateSessionRequestDto requestDto){
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연 회차 정보를 수정했습니다.",
                performanceSessionService.updateSession(performanceId, sessionId, requestDto));
    }

    @Operation(summary = "Read Performance Session", description = "단건 공연 회차 정보를 조회합니다.")
    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<ReadSessionResponseDto>> readSession(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "단건 공연 회차 정보를 조회합니다.",
                performanceSessionService.readSession(performanceId, sessionId));
    }

    @Operation(summary = "Read All Performance Sessions", description = "공연의 전체 회차를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadSessionResponseDto>>> readSessionList(
            @PathVariable Long performanceId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "해당 공연의 전체 회차 정보를 조회합니다.",
                performanceSessionService.readSessionList(performanceId));
    }

}
