package com.pettact.api.pet.controller;

import com.pettact.api.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PetApiController {

    private final PetService petService;

    // fetch 호출 시 시설 데이터 수집
    @GetMapping("/fetch")
    public String apiFetch() {
        petService.fetchAllApi();  
        return "반려동물 편의시설 데이터 수집 완료!";
    }
}
