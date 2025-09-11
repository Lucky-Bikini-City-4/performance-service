package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<Performance,Long> {
    Optional<Performance> findByPerformanceIdAndDeletedAtIsNull(Long performanceId);
    Page<Performance> findByDeletedAtIsNull(Pageable pageable);
    Page<Performance> findByDeletedAtIsNullAndType(Pageable pageable, Type type);
}
