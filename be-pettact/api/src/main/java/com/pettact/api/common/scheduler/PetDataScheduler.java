package com.pettact.api.common.scheduler;

import com.pettact.api.pet.service.PetDataMergeService;
import com.pettact.api.pet.service.PetTmpDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class PetDataScheduler {

    private final PetTmpDataService petTmpDataService;
    private final PetDataMergeService petDataMergeService;
 
    // 초 분 시 순서로 쓰세요
    @Scheduled(cron = "0 0 3 * * *")
    public void collectAndMergePetData() {
        log.info("유기동물 API 수집 시작");
        petTmpDataService.fetchAllTmpApi();
        log.info("임시 테이블 데이터 수집 완료, 병합 시작");
        petDataMergeService.mergeAll();
        log.info("병합 완료");
    }
}

