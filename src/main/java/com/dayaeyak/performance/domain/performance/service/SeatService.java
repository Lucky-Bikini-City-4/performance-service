package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.performance.dto.request.UpdateSeatSoldOutRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.SeatResponseDto;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSeat;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSeatRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSectionRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSessionRepository;
import com.dayaeyak.performance.utils.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final PerformanceSectionRepository performanceSectionRepository;
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

    /* 공연 회차 구역 좌석 품절 여부 변경 (분산락 적용)*/
    @DistributedLock(key = "'seat:' + #seatId")
    @Transactional
    public SeatResponseDto changeIsSoldOut(
            Long performanceId,
            Long sessionId,
            Long sectionId,
            Long seatId,
            UpdateSeatSoldOutRequestDto requestDto) {

        log.info("좌석 품절 상태 변경 시작 - seatId: {}, soldOut: {}", seatId, requestDto.isSoldOut());

        // 좌석 조회
        PerformanceSeat seat = performanceSeatRepository.findByPerformanceSeatIdAndDeletedAtIsNull(seatId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SEAT_NOT_FOUND));

        // 구역 조회
        PerformanceSection section = performanceSectionRepository.findByPerformanceSectionIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SECTION_NOT_FOUND));

        // 좌석이 요청한 구역에 속하는지 검증
        if (!Objects.equals(seat.getPerformanceSection().getPerformanceSectionId(), sectionId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SECTION_AND_SEAT);
        }

        // 구역이 요청한 회차에 속하는지 검증
        if (!Objects.equals(seat.getPerformanceSection().getPerformanceSession().getPerformanceSessionId(), sessionId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SESSION_AND_SECTION);
        }

        // 회차가 요청한 공연에 속하는지 검증
        if (!Objects.equals(seat.getPerformanceSection().getPerformanceSession().getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        // 품절 상태 변경 및 구역 잔여좌석수 업데이트
        if (requestDto.isSoldOut()) {
            // true로 요청한 경우 (품절 처리)
            if (seat.getIsSoldOut()) {
                throw new CustomException(PerformanceErrorCode.SEAT_ALREADY_SOLD_OUT);
            }
            seat.sellOut();
            section.decreaseRemainingSeats();   // 해당 구역의 잔여좌석수 -1
            log.info("좌석 품절 처리 완료 - seatId: {}", seatId);
        } else {
            // false로 요청한 경우 (품절 해제)
            if (!seat.getIsSoldOut()) {
                throw new CustomException(PerformanceErrorCode.SEAT_ALREADY_OPEN);
            }
            seat.reopen();
            section.increaseRemainingSeats();   // 해당 구역의 잔여좌석수 +1
            log.info("좌석 품절 해제 완료 - seatId: {}", seatId);
        }

        // 6. 응답 DTO 생성 및 반환
        return SeatResponseDto.from(seat);
    }

    /* 공연 회차 구역 좌석 조회 */

    /* 공연 회차 구역 좌석 전체 조회 */

}
