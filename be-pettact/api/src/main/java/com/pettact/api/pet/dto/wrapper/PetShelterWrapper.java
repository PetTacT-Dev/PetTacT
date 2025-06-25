package com.pettact.api.pet.dto.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.pettact.api.pet.dto.PetShelterDto;

@Getter
@Setter
public class PetShelterWrapper {
    private Response response;

    @Getter 
    @Setter
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter 
    @Setter
    public static class Header {
        private long reqNo;
        private String resultCode;
        private String resultMsg;
    }

    @Getter 
    @Setter
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Getter 
    @Setter
    public static class Items {
        private List<PetShelterDto> item;
    }
}
