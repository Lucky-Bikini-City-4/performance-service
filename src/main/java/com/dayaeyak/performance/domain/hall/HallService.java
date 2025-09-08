package com.dayaeyak.performance.domain.hall;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.response.CreateHallResponseDto;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.hall.repository.HallSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HallService {
    private final HallRepository hallRepository;
    private final HallSectionRepository hallSectionRepository;

    @Transactional
    public CreateHallResponseDto createHall(CreateHallRequestDto requestDto) {
        // 공연장 엔티티 생성 및 저장
        Hall hall = Hall.builder()
                .hallName(requestDto.hallName())
                .address(requestDto.address())
                .city(requestDto.city())
                .capacity(requestDto.capacity())
                .build();
        Hall savedHall;
        try{
            savedHall = hallRepository.save(hall);
        } catch (Exception e){
            throw new CustomException(HallErrorCode.HALL_NAME_DUPLICATED);
        }

        // 공연장 구역 엔티티 리스트 생성 및 저장
        List<HallSection> hallSections = requestDto.sections().stream()
                .map(sectionDto -> HallSection.builder()
                        .hall(savedHall)
                        .sectionName(sectionDto.sectionName())
                        .seats(sectionDto.seats())
                        .seatPrice(sectionDto.seatPrice())
                        .build())
                .collect(Collectors.toList());

        List<HallSection> savedHallSections = hallSectionRepository.saveAll(hallSections);

        // 응답 DTO 생성 및 반환
        return new CreateHallResponseDto(savedHall.getHallId());
    }
}
