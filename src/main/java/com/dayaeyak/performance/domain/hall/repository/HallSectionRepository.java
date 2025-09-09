package com.dayaeyak.performance.domain.hall.repository;

import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallSectionRepository extends JpaRepository<HallSection, Long> {
    void deleteByHall(Hall hall);
    List<HallSection> findByHallAndDeletedAtIsNull(Hall hall);
}
