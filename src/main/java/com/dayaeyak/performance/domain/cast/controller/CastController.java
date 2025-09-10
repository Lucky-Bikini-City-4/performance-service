package com.dayaeyak.performance.domain.cast.controller;

import com.dayaeyak.performance.domain.cast.dto.request.CreateCastRequestDto;
import com.dayaeyak.performance.domain.cast.dto.response.CreateCastResponseDto;
import com.dayaeyak.performance.domain.cast.service.CastService;
import com.dayaeyak.performance.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cast")
@RequiredArgsConstructor
@Tag(name = "Cast API")
public class CastController {
    private final CastService castService;

    @Operation(summary = "Create Cast", description = "새로운 출연진을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateCastResponseDto>> createCast(
            @Validated @RequestBody CreateCastRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED.value(),
                "출연진이 생성되었습니다",
                castService.createCast(requestDto));
    }

    @Operation(summary = "Update Cast", description = "기존 출연진의 정보를 수정합니다")
    @PatchMapping("/{castId}")
    public ResponseEntity<ApiResponse<CreateCastResponseDto>> updateCast(
            @PathVariable Long castId,
            @Validated @RequestBody CreateCastRequestDto requestDto){
        return ApiResponse.success(HttpStatus.OK.value(),
                "출연진 정보가 수정되었습니다.",
                castService.updateCast(castId, requestDto));
    }

}
