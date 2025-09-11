package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.cast.exception.CastErrorCode;
import com.dayaeyak.performance.domain.cast.repository.CastRepository;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.performance.dto.request.CreatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreatePerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final HallRepository hallRepository;
    private final CastRepository castRepository;

    /* 공연 생성 */
    @Transactional
    public CreatePerformanceResponseDto createPerformance(CreatePerformanceRequestDto requestDto){
        // 공연장 ID로 공연장 객체 찾기
        Hall hall = hallRepository.findByHallIdAndDeletedAtIsNull(requestDto.hallId())
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        // 출연진 목록 List로 변환
        List<String> castNames = Arrays.stream(requestDto.castList().split(",")).toList();

        // 출연진 이름으로 출연진 찾아서 리스트화
        List<Cast> casts = castNames.stream()
                .map(name -> castRepository.findByCastNameAndDeletedAtIsNull(name)
                        .orElseThrow(()->new CustomException(CastErrorCode.CAST_NOT_FOUND)))
                .toList();

        // 공연 엔티티 생성 및 저장
        Performance performance = Performance.builder()
                .sellerId(requestDto.sellerId())
                .hall(hall)
                .performanceName(requestDto.performanceName())
                .description(requestDto.description())
                .type(requestDto.type())
                .grade(requestDto.grade())
                .startDate(java.sql.Date.valueOf(requestDto.startDate()))
                .endDate(java.sql.Date.valueOf(requestDto.endDate()))
                .ticketOpenAt(Timestamp.valueOf(requestDto.ticketOpenAt()))
                .ticketCloseAt(Timestamp.valueOf(requestDto.ticketCloseAt()))
                .isActivated(requestDto.isActivated() == null || requestDto.isActivated())
                .build();
        performance.getCastList().addAll(casts);
        Performance savedPerformance = performanceRepository.save(performance);

        // 응답 DTO로 반환
        return new CreatePerformanceResponseDto(savedPerformance.getPerformanceId());
    }
}
