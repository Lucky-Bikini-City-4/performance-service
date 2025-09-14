package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceSectionRepository extends JpaRepository<PerformanceSection, Long> {
    List<PerformanceSection> findByPerformanceSessionAndDeletedAtIsNull(PerformanceSession session);
    Optional<PerformanceSection> findByPerformanceSectionIdAndDeletedAtIsNull(Long performanceSectionId);
}
