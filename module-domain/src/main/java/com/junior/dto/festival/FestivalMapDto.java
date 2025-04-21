package com.junior.dto.festival;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record FestivalMapDto(
        Long id,
        Double lat,
        Double logt

) {

    @QueryProjection
    public FestivalMapDto {
    }
}
