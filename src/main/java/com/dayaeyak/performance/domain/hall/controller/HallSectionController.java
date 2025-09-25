package com.dayaeyak.performance.domain.hall.controller;

import com.dayaeyak.performance.annotation.Authorize;
import com.dayaeyak.performance.common.enums.UserRole;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallSectionDto;
import com.dayaeyak.performance.domain.hall.dto.response.ReadHallSectionResponseDto;
import com.dayaeyak.performance.domain.hall.dto.response.UpdateHallSectionResponseDto;
import com.dayaeyak.performance.domain.hall.service.HallSectionService;
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
@RequestMapping("/halls/{hallId}/sections")
@RequiredArgsConstructor
@Tag(name = "Hall Section API")
public class HallSectionController {
    private final HallSectionService hallSectionService;

    @Operation(summary = "Update Hall Section", description = "공연장 구역 정보를 수정합니다.")
    @PutMapping("/{hallSectionId}")
    @Authorize(roles = { UserRole.MASTER })
    public ResponseEntity<ApiResponse<UpdateHallSectionResponseDto>> updateHallSection(
            @PathVariable Long hallId,
            @PathVariable Long hallSectionId,
            @Validated @RequestBody CreateHallSectionDto requestDto){
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연장 구역 정보가 수정되었습니다.",
                hallSectionService.updateHallSection(hallId, hallSectionId, requestDto));
    }

    @Operation(summary = "Read Hall Section", description = "공연장의 단건 구역 정보를 조회합니다.")
    @GetMapping("/{hallSectionId}")
    public ResponseEntity<ApiResponse<ReadHallSectionResponseDto>> readHallSection(
            @PathVariable Long hallId,
            @PathVariable Long hallSectionId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "공연장 구역 정보를 조회합니다.",
                hallSectionService.readHallSection(hallId, hallSectionId));
    }

    @Operation(summary = "Read All Hall Sections", description = "공연장의 전체 구역 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadHallSectionResponseDto>>> readHallSections(
            @PathVariable Long hallId){
        return ApiResponse.success(HttpStatus.OK.value(),
                "해당 공연장의 전체 구역을 조회합니다.",
                hallSectionService.readHallSections(hallId));
    }
}
