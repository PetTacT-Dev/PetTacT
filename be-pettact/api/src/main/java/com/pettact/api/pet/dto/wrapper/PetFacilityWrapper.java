package com.pettact.api.pet.dto.wrapper;

import java.util.List;

import com.pettact.api.pet.dto.PetFacilityDto;

import lombok.Data;

@Data
public class PetFacilityWrapper {

    private List<PetFacilityDto> data;

    private int currentCount;
    private int matchCount;
    private int page;
    private int perPage;
    private int totalCount;
}
