package com.junior.dto.festival;

import com.junior.util.CustomStringUtil;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FestivalAdminDto(
        Long id,
        String title,
        String duration,
        String location
) {


    @QueryProjection
    public FestivalAdminDto(Long id, String title, LocalDate startDate, LocalDate endDate, String city, String location) {
        this(id, title, CustomStringUtil.durationToString(startDate.toString().replaceAll("-", ""),
                endDate.toString().replaceAll("-", "")), city + " " + location);
    }
}
