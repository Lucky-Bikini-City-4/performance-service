
package com.dayaeyak.performance.domain.performance.controller;

import com.dayaeyak.performance.annotation.Authorize;
import com.dayaeyak.performance.common.enums.UserRole;
import com.dayaeyak.performance.domain.performance.dto.request.UpdateSeatSoldOutRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.SeatResponseDto;
import com.dayaeyak.performance.domain.performance.service.SeatService;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/performances/{performanceId}/sessions/{sessionId}/sections/{sectionId}/seats")
@RequiredArgsConstructor
@Tag(name = "Performance Seat API")
public class SeatController {
    private final SeatService seatService;

    @Operation(summary = "Change IsSoldOut", description = "좌석의 품절 여부를 변경합니다.")
    @PostMapping("/{seatId}")
    @Authorize(roles = { UserRole.MASTER })
    public ResponseEntity<ApiResponse<SeatResponseDto>> changeIsSoldOut(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long sectionId,
            @PathVariable Long seatId,
            @RequestBody UpdateSeatSoldOutRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "해당 좌석의 품절 여부를 변경했습니다.",
                seatService.changeIsSoldOut(performanceId, sessionId, sectionId, seatId, requestDto));
    }

    @Operation(summary = "Read Performance Seat", description = "해당 좌석의 상세 정보를 조회합니다.")
    @GetMapping("/{seatId}")
    public ResponseEntity<ApiResponse<SeatResponseDto>> readPerformanceSeat(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long sectionId,
            @PathVariable Long seatId) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "좌석 정보를 조회합니다.",
                seatService.readPerformanceSeat(performanceId, sessionId, sectionId, seatId));
    }

    @Operation(summary = "Get IsSoldOut of all Seats", description = "전체 좌석의 품절 여부를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Boolean>>> readPerformanceSeats(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long sectionId) {
        return ApiResponse.success(HttpStatus.OK.value(),
                "해당 구역 전체 좌석의 품절 여부를 좌석 번호 순서대로 조회합니다.",
                seatService.readPerformanceSeats(performanceId, sessionId, sectionId));
    }


}
