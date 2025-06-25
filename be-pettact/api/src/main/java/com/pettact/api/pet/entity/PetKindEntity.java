package com.pettact.api.pet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_kind")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetKindEntity {

    @Id
    private String kindCd;
    private String kindNm;
    private String upKindCd;// 개, 고양이, 기타 축종 구분용
}
