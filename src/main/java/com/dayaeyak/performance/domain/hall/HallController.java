package com.dayaeyak.performance.domain.hall;

import com.dayaeyak.performance.domain.hall.dto.request.CreateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.response.CreateHallResponseDto;
import com.dayaeyak.performance.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
public class HallController {
    private final HallService hallService;

    /*공연장 생성 API*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateHallResponseDto> createHall(@Validated @RequestBody CreateHallRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED, "공연장이 생성되었습니다.", hallService.createHall(requestDto));
    }
}
