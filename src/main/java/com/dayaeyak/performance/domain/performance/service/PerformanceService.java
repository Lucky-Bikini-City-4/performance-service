package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.dto.PageInfoDto;
import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.common.exception.GlobalErrorCode;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.cast.exception.CastErrorCode;
import com.dayaeyak.performance.domain.cast.repository.CastRepository;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.performance.dto.request.ChangePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.CreatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.UpdatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreatePerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformancePageResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.enums.Type;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /* 공연 수정 (요청 DTO의 필드를 하나씩 확인해서 들어온 값만 수정) */
    @Transactional
    public CreatePerformanceResponseDto updatePerformance(Long performanceId, UpdatePerformanceRequestDto requestDto){
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 시간이 이미 지났다면 수정 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.PERFORMANCE_ALREADY_OPENED);
        }

        // 공연명 수정
        if (requestDto.performanceName() != null && !requestDto.performanceName().isBlank()) {
            performance.setPerformanceName(requestDto.performanceName());
        }

        // 공연장 수정
        if (requestDto.hallId() != null) {
            Hall hall = hallRepository.findById(requestDto.hallId())
                    .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));
            performance.setHall(hall);
        }

        // 출연진 수정
        if (requestDto.castList() != null && !requestDto.castList().isBlank()) {
            // 출연진 목록 List로 변환
            List<String> castNames = Arrays.stream(requestDto.castList().split(",")).toList();

            // 출연진 이름으로 출연진 찾아서 리스트화
            List<Cast> casts = castNames.stream()
                    .map(name -> castRepository.findByCastNameAndDeletedAtIsNull(name)
                            .orElseThrow(()->new CustomException(CastErrorCode.CAST_NOT_FOUND)))
                    .toList();

            performance.getCastList().clear();
            performance.getCastList().addAll(casts);
        }

        // 설명 수정
        if (requestDto.description() != null && !requestDto.description().isBlank()) {
            performance.setDescription(requestDto.description());
        }

        // 타입 수정
        if (requestDto.type() != null) {
            performance.setType(requestDto.type());
        }

        // 관람 등급 수정
        if (requestDto.grade() != null) {
            performance.setGrade(requestDto.grade());
        }

        // 시작일/마감일 수정
        if (requestDto.startDate() != null) {
            performance.setStartDate(java.sql.Date.valueOf(requestDto.startDate()));
        }
        if (requestDto.endDate() != null) {
            performance.setEndDate(java.sql.Date.valueOf(requestDto.endDate()));
        }

        // 티켓 오픈/마감 수정
        if (requestDto.ticketOpenAt() != null) {
            performance.setTicketOpenAt(Timestamp.valueOf(requestDto.ticketOpenAt()));
        }
        if (requestDto.ticketCloseAt() != null) {
            performance.setTicketCloseAt(Timestamp.valueOf(requestDto.ticketCloseAt()));
        }

        return new CreatePerformanceResponseDto(performance.getPerformanceId());
    }

    /* 공연 활성화 상태 변경 */
    @Transactional
    public Boolean changeIsActivated(Long performanceId, ChangePerformanceRequestDto requestDto) {
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 일시 이후, 마감 일시 이전이면 상태 변경 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))
            && performance.getTicketCloseAt().after(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.CANNOT_CHANGE_ACTIVATION);
        }

        performance.setIsActivated(requestDto.isActivated());
        return performance.getIsActivated();
    }

    /* 공연 단건 조회 */
    public ReadPerformanceResponseDto readPerformance(Long performanceId) {
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNullAndIsActivatedIsTrue(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        return ReadPerformanceResponseDto.from(performance);
    }

    /* 공연 페이징 조회 */
    public ReadPerformancePageResponseDto readPerformanceList(int page, int size, Type type) {
        if(page < 0 || size < 0){
            throw new CustomException(GlobalErrorCode.INVALID_PAGE_OR_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<Performance> performances;

        // 공연 타입별 검색, 타입이 없을 경우 전체 타입에서 검색
        if (type == null) {
            performances = performanceRepository.findByDeletedAtIsNullAndIsActivatedIsTrue(pageable);
        } else {
            performances = performanceRepository.findByDeletedAtIsNullAndTypeAndIsActivatedIsTrue(pageable, type);
        }

        // PageInfo 생성
        PageInfoDto pageInfo = new PageInfoDto(
                performances.getNumber() + 1, // 0-base -> 1-base
                performances.getSize(),
                performances.getTotalElements(),
                performances.getTotalPages(),
                performances.isLast()
        );

        // List<DTO> 형태로 변환
        List<ReadPerformanceResponseDto> data = performances.getContent()
                .stream()
                .map(ReadPerformanceResponseDto::from)
                .toList();

        return new ReadPerformancePageResponseDto(pageInfo, data);
    }

    /* 공연 삭제 */
    @Transactional
    public Void deletePerformance(Long performanceId) {
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 일시가 이미 지났다면 삭제 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.PERFORMANCE_ALREADY_OPENED);
        }

        // 공연 삭제
        performance.delete();
        return null;
    }
}
