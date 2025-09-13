
package com.dayaeyak.performance.domain.performance.controller;

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

@RestController
@RequestMapping("/performances/{performanceId}/sessions/{sessionId}/sections/{sectionId}/seats")
@RequiredArgsConstructor
@Tag(name = "Performance Seat API")
public class SeatController {
    private final SeatService seatService;

    @Operation(summary = "Change IsSoldOut", description = "좌석의 품절 여부를 변경합니다.")
    @PatchMapping("/{seatId}")
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
}
