package com.junior.dto.festival;

import lombok.Builder;

@Builder
public record FestivalDetailAdminDto(
        Long id,
        Long contentId,
        String title,
        String location,
        String duration,
        String detail

) {
}
