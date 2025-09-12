package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceSectionRepository extends JpaRepository<PerformanceSection, Long> {
    List<PerformanceSection> findByPerformanceSessionAndDeletedAtIsNull(PerformanceSession session);
}
