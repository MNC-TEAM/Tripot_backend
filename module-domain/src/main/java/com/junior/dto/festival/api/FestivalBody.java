package com.junior.dto.festival.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
public class FestivalBody {

    private FestivalApiItems items;

    private long numOfRows;
    private int pageNo;
    private long totalCount;

}
