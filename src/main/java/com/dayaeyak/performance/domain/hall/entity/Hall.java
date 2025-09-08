package com.dayaeyak.performance.domain.hall.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import com.dayaeyak.performance.domain.hall.enums.Region;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "halls")
public class Hall extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long hallId;

    @Column(length = 100, nullable = false, unique = true)
    private String hallName;

    @Column(length = 100, nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region city;

    @Column(nullable = false)
    private Integer capacity;

    @Builder
    public Hall(String hallName, String address, Region city, Integer capacity) {
        this.hallName = hallName;
        this.address = address;
        this.city = city;
        this.capacity = capacity;
    }
}
