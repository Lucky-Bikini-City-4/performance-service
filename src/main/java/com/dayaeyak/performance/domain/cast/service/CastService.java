package com.dayaeyak.performance.domain.cast.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.cast.dto.request.CreateCastRequestDto;
import com.dayaeyak.performance.domain.cast.dto.response.CreateCastResponseDto;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.cast.exception.CastErrorCode;
import com.dayaeyak.performance.domain.cast.repository.CastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CastService {
    private final CastRepository castRepository;

    /*출연진 생성*/
    @Transactional
    public CreateCastResponseDto createCast(CreateCastRequestDto requestDto) {
        // 출연진 이름 중복 검색
        if(castRepository.existsByCastNameAndDeletedAtIsNull(requestDto.castName())){
            throw new CustomException(CastErrorCode.CAST_NAME_DUPLICATED);
        }

        // 출연진 엔티티 생성 및 저장
        Cast cast = new Cast(requestDto.castName());
        Cast savedCast = castRepository.save(cast);

        // 응답 DTO 생성 및 반환
        return new CreateCastResponseDto(savedCast.getCastId(), savedCast.getCastName());
    }

    /*출연진 수정*/
    @Transactional
    public CreateCastResponseDto updateCast(Long castId, CreateCastRequestDto requestDto) {
        // 기존 출연진 조회
        Cast existingCast = castRepository.findByCastIdAndDeletedAtIsNull(castId)
                .orElseThrow(() -> new CustomException(CastErrorCode.CAST_NOT_FOUND));

        // 출연진 이름 중복 검색 (자신 제외)
        if(castRepository.existsByCastNameAndCastIdNotAndDeletedAtIsNull(requestDto.castName(), castId)){
            throw new CustomException(CastErrorCode.CAST_NAME_DUPLICATED);
        }

        // 출연진 정보 수정
        existingCast.update(requestDto.castName());

        return new CreateCastResponseDto(existingCast.getCastId(), existingCast.getCastName());
    }
}
