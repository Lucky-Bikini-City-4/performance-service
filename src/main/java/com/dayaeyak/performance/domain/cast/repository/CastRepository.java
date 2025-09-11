package com.dayaeyak.performance.domain.cast.repository;

import com.dayaeyak.performance.domain.cast.entity.Cast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CastRepository extends JpaRepository<Cast,Long> {
    Boolean existsByCastNameAndDeletedAtIsNull(String castName);
    Optional<Cast> findByCastIdAndDeletedAtIsNull(Long castId);
    Boolean existsByCastNameAndCastIdNotAndDeletedAtIsNull(String castName, Long castId);
    Page<Cast> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    List<Cast> findByDeletedAtIsNullOrderByCreatedAtDesc();
    Optional<Cast> findByCastNameAndDeletedAtIsNull(String castName);
}
