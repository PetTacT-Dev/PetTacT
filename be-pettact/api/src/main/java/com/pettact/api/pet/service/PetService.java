package com.pettact.api.pet.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pettact.api.pet.dto.PetKindDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.dto.wrapper.PetFacilityWrapper;
import com.pettact.api.pet.dto.wrapper.PetKindWrapper;
import com.pettact.api.pet.dto.wrapper.PetShelterWrapper;
import com.pettact.api.pet.entity.PetFacilityEntity;
import com.pettact.api.pet.entity.PetKindEntity;
import com.pettact.api.pet.entity.PetShelterEntity;
import com.pettact.api.pet.repository.PetFacilityRepository;
import com.pettact.api.pet.repository.PetKindRepository;
import com.pettact.api.pet.repository.PetShelterRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

    private final RestTemplate restTemplate;

    private final PetFacilityRepository petFacilityRepository;
    private final PetShelterRepository petShelterRepository;
    private final PetKindRepository petKindRepository;

    // api 인증키
    @Value("${pet-api-service-key-decoded}")
    private String apiKey;

    //반려동물 동반가능 업장 api url
    @Value("${pet-facility-api-url}")
    private String petFacilityApiUrl;

    //동물 보호소 정보 api url
    @Value("${pet-shelter-api-url}")
    private String petShelterApiUrl;

    // 시군구 정보 url
    @Value("${sigungu-api-url}")
    private String sigunguApiUrl;

    // 시도 정보 url
    @Value("${sido-api-url}")
    private String sidoApiUrl;

    // 동물품종 정보 url
    @Value("${kind-api-url}")
    private String kindApiUrl;

    // 유기동물 조회  url
    @Value("${abandonment-public-api-url}")
    private String abandonmentPublicApiurl;


    // 각각 api 호출
    public void fetchAllApi() {
        // fetchPetFacility(); // 반려동물 동반가능 업장 - 중복 제거 x
        // fetchPetShelter(); // 동물 보호소 - 중복제거 x
        fetchPetKind(); // 동물품종
    }

    public void fetchPetFacility() {
        int perPage = 1000;
        int page = 1;

        // 일단 첫 페이지로 totalCount 받아오기
        String initialUrl = petFacilityApiUrl + "?page=" + page + "&perPage=" +
                perPage+ "&returnType=JSON" + "&serviceKey=" + apiKey;

        ResponseEntity<PetFacilityWrapper> initialResponse = restTemplate.getForEntity(initialUrl, PetFacilityWrapper.class);

        PetFacilityWrapper initialBody = initialResponse.getBody();

        if (initialBody == null || initialBody.getData() == null) return;

        int totalCount = initialBody.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / perPage);

        for (int i = 1; i <= totalPages; i++) {
            String url = petFacilityApiUrl
                    + "?page=" + i
                    + "&perPage=" + perPage
                    + "&returnType=JSON"
                    + "&serviceKey=" + apiKey;

            ResponseEntity<PetFacilityWrapper> response =
                    restTemplate.getForEntity(url, PetFacilityWrapper.class);

            PetFacilityWrapper body = response.getBody();

            if (body != null && body.getData() != null) {
                body.getData().forEach(dto -> {
                    PetFacilityEntity entity = PetFacilityEntity.builder()
                            .buildingNumber(dto.getBuildingNumber())
                            .longitude(dto.getLongitude())
                            .placeDescription(dto.getPlaceDescription())
                            .roadName(dto.getRoadName())
                            .roadAddress(dto.getRoadAddress())
                            .riName(dto.getRiName())
                            .petAllowed(dto.getPetAllowed())
                            .petOnly(dto.getPetOnly())
                            .petLimit(dto.getPetLimit())
                            .lotNumber(dto.getLotNumber())
                            .legalDongName(dto.getLegalDongName())
                            .sigunguName(dto.getSigunguName())
                            .sidoName(dto.getSidoName())
                            .facilityName(dto.getFacilityName())
                            .additionalFee(dto.getAdditionalFee())
                            .postalCode(dto.getPostalCode())
                            .openHours(dto.getOpenHours())
                            .latitude(dto.getLatitude())
                            .allowedPetSize(dto.getAllowedPetSize())
                            .entranceFeeInfo(dto.getEntranceFeeInfo())
                            .isIndoor(dto.getIsIndoor())
                            .isOutdoor(dto.getIsOutdoor())
                            .phone(dto.getPhone())
                            .parkingAvailable(dto.getParkingAvailable())
                            .lotAddress(dto.getLotAddress())
                            .lastUpdatedDate(dto.getLastUpdatedDate())
                            .category1(dto.getCategory1())
                            .category2(dto.getCategory2())
                            .category3(dto.getCategory3())
                            .homepage(dto.getHomepage())
                            .closedDays(dto.getClosedDays())
                            .build();
                    petFacilityRepository.save(entity);
                });
            }
        }
    }

    public void fetchPetShelter() {
        int numOfRows = 100;
        int pageNo = 1;

        String initialUrl = petShelterApiUrl
                + "?serviceKey=" + apiKey
                + "&numOfRows=" + numOfRows
                + "&pageNo=" + pageNo
                + "&_type=json";

        ResponseEntity<PetShelterWrapper> initialResponse =
                restTemplate.getForEntity(initialUrl, PetShelterWrapper.class);

        PetShelterWrapper initialWrapper = initialResponse.getBody();
        if (initialWrapper == null || initialWrapper.getResponse() == null || initialWrapper.getResponse().getBody() == null) {
            return;
        }

        int totalCount = initialWrapper.getResponse().getBody().getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

        for (int i = 1; i <= totalPages; i++) {
            String url = petShelterApiUrl
                    + "?serviceKey=" + apiKey
                    + "&numOfRows=" + numOfRows
                    + "&pageNo=" + i
                    + "&_type=json";

            try {
                ResponseEntity<PetShelterWrapper> response =
                        restTemplate.getForEntity(url, PetShelterWrapper.class);

                PetShelterWrapper wrapper = response.getBody();
                if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null) {
                    continue;
                }

                List<PetShelterDto> list = wrapper.getResponse().getBody().getItems().getItem();
                if (list == null || list.isEmpty()) {
                    continue;
                }

                for (PetShelterDto dto : list) {
                    PetShelterEntity entity = PetShelterEntity.builder()
                            .careNm(dto.getCareNm())
                            .careRegNo(dto.getCareRegNo())
                            .orgNm(dto.getOrgNm())
                            .divisionNm(dto.getDivisionNm())
                            .saveTrgtAnimal(dto.getSaveTrgtAnimal())
                            .careAddr(dto.getCareAddr())
                            .jibunAddr(dto.getJibunAddr())
                            .lat(dto.getLat())
                            .lng(dto.getLng())
                            .dsignationDate(dto.getDsignationDate())
                            .weekOprStime(dto.getWeekOprStime())
                            .weekOprEtime(dto.getWeekOprEtime())
                            .weekCellStime(dto.getWeekCellStime())
                            .weekCellEtime(dto.getWeekCellEtime())
                            .weekendOprStime(dto.getWeekendOprStime())
                            .weekendOprEtime(dto.getWeekendOprEtime())
                            .weekendCellStime(dto.getWeekendCellStime())
                            .weekendCellEtime(dto.getWeekendCellEtime())
                            .closeDay(dto.getCloseDay())
                            .vetPersonCnt(dto.getVetPersonCnt())
                            .specsPersonCnt(dto.getSpecsPersonCnt())
                            .medicalCnt(dto.getMedicalCnt())
                            .breedCnt(dto.getBreedCnt())
                            .quarabtineCnt(dto.getQuarabtineCnt())
                            .feedCnt(dto.getFeedCnt())
                            .transCarCnt(dto.getTransCarCnt())
                            .careTel(dto.getCareTel())
                            .dataStdDt(dto.getDataStdDt())
                            .build();

                    petShelterRepository.save(entity);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Transactional
    public void fetchPetKind() {
        for (String upKindCd : Arrays.asList("417000", "422400", "429900")) {
            int pageNo = 1;
            int numOfRows = 100;

            while (true) {
                String url = kindApiUrl
                        + "?serviceKey=" + apiKey
                        + "&up_kind_cd=" + upKindCd
                        + "&_type=json"
                        + "&numOfRows=" + numOfRows
                        + "&pageNo=" + pageNo;

                try {
                    ResponseEntity<PetKindWrapper> response =
                            restTemplate.getForEntity(url, PetKindWrapper.class);

                    PetKindWrapper wrapper = response.getBody();
                    if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
                            || wrapper.getResponse().getBody().getItems() == null) {
                        break;
                    }


                    List<PetKindDto> kindList = wrapper.getResponse().getBody().getItems().getItem();
                    if (kindList == null || kindList.isEmpty()) {
                        break;
                    }

                    for (PetKindDto dto : kindList) {
                        boolean exists = petKindRepository.existsByKindCd(dto.getKindCd());

                        if (!exists) {
                            PetKindEntity entity = PetKindEntity.builder()
                                    .kindCd(dto.getKindCd())
                                    .kindNm(dto.getKindNm())
                                    .upKindCd(upKindCd)
                                    .build();
                            petKindRepository.save(entity);
                            log.info("저장 : {}", dto.getKindCd());
                        } else {
                            log.info("중복 : {}", dto.getKindCd());
                        }
                    }
                    pageNo++;

                } catch (Exception e) {
                    log.error("upKindCd={} 페이지 {} 실패: {}", upKindCd, pageNo, e.getMessage());
                    break;
                }
            }
        }
    }

}
