package com.pettact.api.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetShelterDto {
    private String careNm;
    private String careRegNo;
    private String orgNm;
    private String divisionNm;
    private String saveTrgtAnimal;
    private String careAddr;
    private String jibunAddr;
    private String lat;
    private String lng;
    private String dsignationDate;
    private String weekOprStime;
    private String weekOprEtime;
    private String weekCellStime;
    private String weekCellEtime;
    private String weekendOprStime;
    private String weekendOprEtime;
    private String weekendCellStime;
    private String weekendCellEtime;
    private String closeDay;
    private Integer vetPersonCnt;
    private Integer specsPersonCnt;
    private Integer medicalCnt;
    private Integer breedCnt;
    private Integer quarabtineCnt;
    private Integer feedCnt;
    private Integer transCarCnt;
    private String careTel;
    private String dataStdDt;  
}
