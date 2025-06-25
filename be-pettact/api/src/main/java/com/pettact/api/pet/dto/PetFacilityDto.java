package com.pettact.api.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetFacilityDto {

    @JsonProperty("건물 번호")
    private String buildingNumber;

    @JsonProperty("경도")
    private String longitude;

    @JsonProperty("소재지 도로명")
    private String placeDescription;

    @JsonProperty("도로명")
    private String roadName;

    @JsonProperty("소재지 도로명 주소")
    private String roadAddress;

    @JsonProperty("리 명")
    private String riName;

    @JsonProperty("반려동물 동반 가능 여부")
    private String petAllowed;

    @JsonProperty("반려동물 전용 여부")
    private String petOnly;

    @JsonProperty("반려동물 제한사항")
    private String petLimit;

    @JsonProperty("지번 번호")
    private String lotNumber;

    @JsonProperty("법정동 명")
    private String legalDongName;

    @JsonProperty("시군구명")
    private String sigunguName;

    @JsonProperty("시도 명")
    private String sidoName; 

    @JsonProperty("시설명")
    private String facilityName;

    @JsonProperty("추가 요금")
    private String additionalFee;

    @JsonProperty("우편번호")
    private Integer postalCode;

    @JsonProperty("운영시간")
    private String openHours;

    @JsonProperty("위도")
    private String latitude;

    @JsonProperty("반려동물 크기")
    private String allowedPetSize;

    @JsonProperty("입장료 정보")
    private String entranceFeeInfo;

    @JsonProperty("실내 여부")
    private String isIndoor;

    @JsonProperty("실외 여부")
    private String isOutdoor;

    @JsonProperty("전화번호")
    private String phone;

    @JsonProperty("주차 가능 여부")
    private String parkingAvailable;

    @JsonProperty("소재지 지번 주소")
    private String lotAddress;

    @JsonProperty("정보 최종 갱신일")
    private String lastUpdatedDate;

    @JsonProperty("카테고리1")
    private String category1;

    @JsonProperty("카테고리2")
    private String category2;

    @JsonProperty("카테고리3")
    private String category3;

    @JsonProperty("홈페이지")
    private String homepage;

    @JsonProperty("휴무일")
    private String closedDays;
}
