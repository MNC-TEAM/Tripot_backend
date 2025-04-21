package com.junior.dto.festival;

import com.junior.util.CustomStringUtil;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FestivalDto(
        Long id,
        Long contentId,
        String imgUrl,
        String title,
        String duration,
        String city,
        String location
) {


    @QueryProjection
    public FestivalDto(Long id, Long contentId, String imgUrl, String title, LocalDate startDate, LocalDate endDate, String city, String location) {
        this(id, contentId, imgUrl, title, CustomStringUtil.durationToString(startDate.toString().replaceAll("-", ""),
                endDate.toString().replaceAll("-", "")), city, city + " " + location);
    }
}
