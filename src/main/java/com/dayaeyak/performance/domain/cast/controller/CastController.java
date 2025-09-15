package com.dayaeyak.performance.domain.cast.controller;

import com.dayaeyak.performance.domain.cast.dto.request.CreateCastRequestDto;
import com.dayaeyak.performance.domain.cast.dto.response.CreateCastResponseDto;
import com.dayaeyak.performance.domain.cast.dto.response.ReadCastPageResponseDto;
import com.dayaeyak.performance.domain.cast.dto.response.ReadCastResponseDto;
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
            @RequestHeader("X-User-Role") String roles,
            @Validated @RequestBody CreateCastRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED.value(),
                "출연진이 생성되었습니다",
                castService.createCast(roles, requestDto));
    }

    @Operation(summary = "Update Cast", description = "기존 출연진의 정보를 수정합니다")
    @PatchMapping("/{castId}")
    public ResponseEntity<ApiResponse<CreateCastResponseDto>> updateCast(
            @RequestHeader("X-User-Role") String roles,
            @PathVariable Long castId,
            @Validated @RequestBody CreateCastRequestDto requestDto){
        return ApiResponse.success(HttpStatus.OK.value(),
                "출연진 정보가 수정되었습니다.",
                castService.updateCast(roles, castId, requestDto));
    }

    @Operation(summary = "Read Cast", description = "단건 출연진의 정보를 조회합니다.")
    @GetMapping("/{castId}")
    public ResponseEntity<ApiResponse<ReadCastResponseDto>> readCast(@PathVariable Long castId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "출연진 정보를 조회합니다.",
                castService.readCast(castId));
    }

    @Operation(summary = "Read Cast List", description = "출연진(기본 정보) 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<ReadCastPageResponseDto>> readCastList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ApiResponse.success(HttpStatus.OK.value(),
                "출연진 목록을 조회합니다.",
                castService.readCastList(page, size));
    }

    @Operation(summary = "Delete Cast", description = "출연진 정보를 삭제합니다.")
    @DeleteMapping("/{castId}")
    public ResponseEntity<ApiResponse<Void>> deleteCast(
            @RequestHeader("X-User-Role") String roles,
            @PathVariable Long castId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "출연진을 삭제했습니다",
                castService.deleteCast(roles, castId));
    }

}
