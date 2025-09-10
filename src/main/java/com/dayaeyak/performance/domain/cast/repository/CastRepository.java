package com.dayaeyak.performance.domain.cast.repository;

import com.dayaeyak.performance.domain.cast.entity.Cast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastRepository extends JpaRepository<Cast,Long> {
    Boolean existsByCastNameAndDeletedAtIsNull(String castName);
}
