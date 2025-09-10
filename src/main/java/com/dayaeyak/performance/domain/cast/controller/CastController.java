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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
