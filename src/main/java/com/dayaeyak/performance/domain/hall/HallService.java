package com.dayaeyak.performance.domain.hall;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.common.exception.GlobalErrorCode;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallSectionDto;
import com.dayaeyak.performance.domain.hall.dto.request.UpdateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.response.CreateHallResponseDto;
import com.dayaeyak.performance.domain.hall.dto.response.ReadHallResponseDto;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import com.dayaeyak.performance.domain.hall.enums.Region;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.hall.repository.HallSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HallService {
    private final HallRepository hallRepository;
    private final HallSectionRepository hallSectionRepository;

    @Transactional
    public CreateHallResponseDto createHall(CreateHallRequestDto requestDto) {
        // 공연장 이름 중복 검색
        if (hallRepository.existsByHallNameAndDeletedAtIsNull(requestDto.hallName())) {
            throw new CustomException(HallErrorCode.HALL_NAME_DUPLICATED);
        }

        // 공연장 엔티티 생성 및 저장
        Hall hall = Hall.builder()
                .hallName(requestDto.hallName())
                .address(requestDto.address())
                .city(requestDto.city())
                .capacity(requestDto.capacity())
                .build();
        Hall savedHall = hallRepository.save(hall);

        // 공연장 구역 엔티티 리스트 생성 및 저장
        List<HallSection> hallSections = createHallSections(requestDto.sections(), hall);

        List<HallSection> savedHallSections = hallSectionRepository.saveAll(hallSections);

        // 응답 DTO 생성 및 반환
        return new CreateHallResponseDto(savedHall.getHallId());
    }

    @Transactional
    public CreateHallResponseDto updateHall(Long hallId, UpdateHallRequestDto requestDto) {
        // 기존 공연장 조회
        Hall existingHall = hallRepository.findById(hallId)
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        // 공연장 이름 중복 검색 (자기 자신 제외)
        if (hallRepository.existsByHallNameAndHallIdNotAndDeletedAtIsNull(requestDto.hallName(), hallId)) {
            throw new CustomException(HallErrorCode.HALL_NAME_DUPLICATED);
        }

        // 공연장 정보 수정
        existingHall.update(requestDto.hallName(), requestDto.address(), requestDto.city(), requestDto.capacity());

        if(requestDto.sections() != null && !requestDto.sections().isEmpty()) {
            hallSectionRepository.deleteByHall(existingHall);

            // 공연장 구역 엔티티 리스트 생성 및 저장
            List<HallSection> hallSections = createHallSections(requestDto.sections(), existingHall);
            hallSectionRepository.saveAll(hallSections);
        }

        return new CreateHallResponseDto(existingHall.getHallId());
    }

    public ReadHallResponseDto readHall(Long hallId) {
        Hall hall = hallRepository.findByHallIdAndDeletedAtIsNull(hallId)
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        return ReadHallResponseDto.from(hall);
    }

    public List<ReadHallResponseDto> readHallList(int page, int size, Region city) {
        if(page < 0 || size < 0){
            throw new CustomException(GlobalErrorCode.INVALID_PAGE_OR_SIZE);
        }

        List<Hall> halls;
        if(size == 0){
            if(city != null){       // 지역별 전체 공연장 목록 조회
                halls = hallRepository.findByCityAndDeletedAtIsNullOrderByCreatedAtDesc(city);
            } else{                 // 전체 공연장 목록 조회
                halls = hallRepository.findByDeletedAtIsNullOrderByCreatedAtDesc();
            }
        } else{
            Pageable pageable = PageRequest.of(page, size);
            Page<Hall> hallPage;
            if (city != null) {     // 지역별 공연장 페이징 조회
                hallPage = hallRepository.findByCityAndDeletedAtIsNullOrderByCreatedAtDesc(city, pageable);
            } else {                // 공연장 페이징 조회
                hallPage = hallRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
            }
            halls = hallPage.getContent();
        }

        return halls.stream()
                .map(hall -> new ReadHallResponseDto(
                        hall.getHallId(),
                        hall.getHallName(),
                        hall.getAddress(),
                        hall.getCity(),
                        hall.getCapacity()))
                .toList();
    }

    /* 공연장 구역 엔티티 리스트 생성 및 반환 메서드 */
    private List<HallSection> createHallSections(List<CreateHallSectionDto> requestDto, Hall hall) {
        return requestDto.stream()
                .map(sectionDto -> HallSection.builder()
                        .hall(hall)
                        .sectionName(sectionDto.sectionName())
                        .seats(sectionDto.seats())
                        .seatPrice(sectionDto.seatPrice())
                        .build())
                .toList();
    }
}
