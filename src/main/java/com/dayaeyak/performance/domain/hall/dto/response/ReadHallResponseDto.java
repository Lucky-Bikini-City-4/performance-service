package com.dayaeyak.performance.domain.hall.dto.response;

import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.enums.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReadHallResponseDto(
        @Schema(description = "공연장 ID", example = "1")
        Long hallId,

        @Schema(description = "공연장 이름", example = "KSPO DOME")
        String hallName,

        @Schema(description = "공연장 주소", example = "서울특별시 송파구 올림픽로 424")
        String address,

        @Schema(description = "공연장 소재 지역", example = "SEOUL")
        Region region,

        @Schema(description = "수용 인원", example = "12000")
        Integer capacity
) {

    public static ReadHallResponseDto from(Hall hall) {
        return ReadHallResponseDto.builder()
                .hallId(hall.getHallId())
                .hallName(hall.getHallName())
                .address(hall.getAddress())
                .region(hall.getRegion())
                .capacity(hall.getCapacity())
                .build();

    }
}
