package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.booking.dto.BulkSeatSoldOutRequestDto;
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

import java.util.List;
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
    public SeatResponseDto changeIsSoldOut(Long performanceId, Long sessionId,
            Long sectionId, Long seatId, UpdateSeatSoldOutRequestDto requestDto) {
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
    public SeatResponseDto readPerformanceSeat(Long performanceId, Long sessionId, Long sectionId, Long seatId){
        PerformanceSeat seat = performanceSeatRepository.findByPerformanceSeatIdAndDeletedAtIsNull(seatId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SEAT_NOT_FOUND));

        if (!Objects.equals(seat.getPerformanceSection().getPerformanceSectionId(), sectionId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SECTION_AND_SEAT);
        }

        if (!Objects.equals(seat.getPerformanceSection().getPerformanceSession().getPerformanceSessionId(), sessionId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SESSION_AND_SECTION);
        }

        if (!Objects.equals(seat.getPerformanceSection().getPerformanceSession().getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        return SeatResponseDto.from(seat);
    }

    /* 한 구역의 좌석 전체 조회 (순서대로 좌석의 품절 여부만 응답) */
    public List<Boolean> readPerformanceSeats(Long performanceId, Long sessionId, Long sectionId){
        // 요청 구역 조회
        PerformanceSection section = performanceSectionRepository.findByPerformanceSectionIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SECTION_NOT_FOUND));

        // 회차 구역이 요청한 공연 회차에 속하는지 검증
        if (!Objects.equals(section.getPerformanceSession().getPerformanceSessionId(), sessionId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SESSION_AND_SECTION);
        }

        // 공연 회차가 요청한 공연에 속하는지 검증
        if (!Objects.equals(section.getPerformanceSession().getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        // 해당 구역 전체 좌석의 품절여부만 조회
        return performanceSeatRepository.findIsSoldOutBySectionIdNative(sectionId);
    }

    /**
     * 여러 좌석을 한 번에 품절 처리 (분산락 적용)
     */
    @DistributedLock(key = "'section:' + #requestDto.sectionId()")
    @Transactional
    public void bulkChangeToSoldOut(BulkSeatSoldOutRequestDto requestDto) {
        log.info("벌크 좌석 품절 처리 시작 - sectionId: {}, seatIds: {}",
                requestDto.sectionId(), requestDto.seatIds());

        // 구역 조회
        PerformanceSection section = performanceSectionRepository.findByPerformanceSectionIdAndDeletedAtIsNull(requestDto.sectionId())
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SECTION_NOT_FOUND));

        // 구역이 요청한 회차에 속하는지 검증
        if (!Objects.equals(section.getPerformanceSession().getPerformanceSessionId(), requestDto.sessionId())) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SESSION_AND_SECTION);
        }

        // 회차가 요청한 공연에 속하는지 검증
        if (!Objects.equals(section.getPerformanceSession().getPerformance().getPerformanceId(), requestDto.performanceId())) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        // 좌석들 조회 및 검증
        List<PerformanceSeat> seats = performanceSeatRepository.findAllByPerformanceSeatIdInAndDeletedAtIsNull(requestDto.seatIds());

        if (seats.size() != requestDto.seatIds().size()) {
            throw new CustomException(PerformanceErrorCode.SEAT_NOT_FOUND);
        }

        int soldOutCount = 0;

        for (PerformanceSeat seat : seats) {
            // 좌석이 요청한 구역에 속하는지 검증
            if (!Objects.equals(seat.getPerformanceSection().getPerformanceSectionId(), requestDto.sectionId())) {
                throw new CustomException(PerformanceErrorCode.MISMATCHED_SECTION_AND_SEAT);
            }

            // 이미 품절된 좌석이 아닌 경우만 품절 처리
            if (!seat.getIsSoldOut()) {
                seat.sellOut();
                soldOutCount++;
                log.info("좌석 품절 처리 - seatId: {}", seat.getPerformanceSeatId());
            } else {
                log.warn("이미 품절된 좌석 스킵 - seatId: {}", seat.getPerformanceSeatId());
            }
        }

        // 실제로 품절 처리된 좌석 수만큼 구역의 잔여좌석수 감소
        if (soldOutCount > 0) {
            section.decreaseRemainingSeats(soldOutCount);
            log.info("구역 잔여좌석수 감소 - sectionId: {}, 감소된 좌석수: {}", requestDto.sectionId(), soldOutCount);
        }

        log.info("벌크 좌석 품절 처리 완료 - sectionId: {}, 처리된 좌석수: {}/{}",
                requestDto.sectionId(), soldOutCount, requestDto.seatIds().size());
    }


}
