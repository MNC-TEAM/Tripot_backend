package com.junior.dto.festival;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record FestivalCityCountDto(
        String city,
        long count
) {

    @QueryProjection
    public FestivalCityCountDto {
    }
}
