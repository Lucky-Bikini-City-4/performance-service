package com.dayaeyak.performance.domain.cast.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "casts")
public class Cast extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long castId;

    @Column(length = 30, nullable = false)
    private String castName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "castList")
    private List<Performance> performanceList = new ArrayList<>();

    public Cast(String castName){
        this.castName = castName;
    }
    public void update(String castName) {
        this.castName = castName;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();

        // 각 공연의 출연진 목록에서 출연진(자기 자신) 삭제
        for (Performance performance : new ArrayList<>(performanceList)) {
            performance.getCastList().remove(this);
        }
        // 공연 리스트도 비워서 혼란 방지
        performanceList.clear();
    }
}