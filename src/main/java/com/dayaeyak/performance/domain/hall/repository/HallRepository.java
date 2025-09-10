package com.dayaeyak.performance.domain.hall.repository;

import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HallRepository extends JpaRepository<Hall,Long> {
    Boolean existsByHallNameAndDeletedAtIsNull(String hallName);
    Boolean existsByHallNameAndHallIdNotAndDeletedAtIsNull(String hallName, Long hallId);
    Optional<Hall> findByHallIdAndDeletedAtIsNull(Long hallId);
    Page<Hall> findByRegionAndDeletedAtIsNullOrderByCreatedAtDesc(Region region, Pageable pageable);
    List<Hall> findByRegionAndDeletedAtIsNullOrderByCreatedAtDesc(Region region);
    Page<Hall> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    List<Hall> findByDeletedAtIsNullOrderByCreatedAtDesc();
}
