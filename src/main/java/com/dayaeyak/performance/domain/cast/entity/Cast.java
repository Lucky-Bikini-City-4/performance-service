package com.dayaeyak.performance.domain.cast.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "casts")
public class Cast extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long castId;

    @Column(length = 30, nullable = false, unique = true)
    private String castName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "castList")
    private List<Performance> performanceList = new ArrayList<>();

    public Cast(String castName){
        this.castName = castName;
    }
    public void update(String castName) {
        this.castName = castName;
    }
}