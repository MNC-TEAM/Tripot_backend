package com.junior.dto.festival;

import lombok.Builder;

@Builder
public record FestivalDetailDto(
        Long id,
        Long contentId,
        String city,
        String title,
        String location,
        String duration,
        String imgUrl,
        String detail,
        boolean isLiked

) {
}
