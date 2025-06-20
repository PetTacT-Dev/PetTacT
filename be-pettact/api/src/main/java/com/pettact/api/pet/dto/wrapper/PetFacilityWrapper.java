package com.pettact.api.pet.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pettact.api.pet.dto.PetFacilityDto;

import lombok.Data;

import java.util.List;

@Data
public class PetFacilityWrapper {

    private List<PetFacilityDto> data;

    private int currentCount;
    private int matchCount;
    private int page;
    private int perPage;
    private int totalCount;
}
