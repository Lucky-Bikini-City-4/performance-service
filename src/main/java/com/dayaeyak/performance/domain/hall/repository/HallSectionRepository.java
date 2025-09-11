package com.dayaeyak.performance.domain.hall.repository;

import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HallSectionRepository extends JpaRepository<HallSection, Long> {
    void deleteByHall(Hall hall);
    List<HallSection> findByHallAndDeletedAtIsNull(Hall hall);
    Optional<HallSection> findByHallSectionIdAndDeletedAtIsNull(Long hallSectionId);
    Boolean existsByHall_HallIdAndSectionNameAndHallSectionIdNotAndDeletedAtIsNull(Long hallId, String sectionName, Long hallSectionId);
}
