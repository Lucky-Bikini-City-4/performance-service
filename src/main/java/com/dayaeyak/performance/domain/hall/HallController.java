package com.dayaeyak.performance.domain.hall;

import com.dayaeyak.performance.domain.hall.dto.request.CreateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.request.UpdateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.response.CreateHallResponseDto;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
@Tag(name = "Hall API")
public class HallController {
    private final HallService hallService;

    /*공연장 생성 API*/
    @Operation(summary = "Create Hall", description = "공연장, 구역 정보를 받아 새로운 공연장을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateHallResponseDto>> createHall(
            @Validated @RequestBody CreateHallRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED.value(),
                "공연장이 생성되었습니다.",
                hallService.createHall(requestDto));
    }

    @Operation(summary = "Update Hall", description = "기존 공연장의 정보를 수정합니다.")
    @PutMapping("/{hallId}")
    public ResponseEntity<ApiResponse<CreateHallResponseDto>> updateHall(
            @PathVariable Long hallId,
            @Validated @RequestBody UpdateHallRequestDto requestDto){
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연장 정보가 수정되었습니다.",
                hallService.updateHall(hallId, requestDto));
    }

}
