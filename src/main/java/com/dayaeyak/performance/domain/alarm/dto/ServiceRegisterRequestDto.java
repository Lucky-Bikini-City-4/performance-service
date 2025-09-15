package com.dayaeyak.performance.domain.alarm.dto;

import com.dayaeyak.performance.domain.alarm.enums.ServiceType;
import com.dayaeyak.performance.domain.alarm.enums.Status;

public record ServiceRegisterRequestDto(
        Long userId,
        ServiceType serviceType, //PERFORMANCE
        Long serviceId,
        String userName,
        Status status  //REQUESTED로 고정

) {
}