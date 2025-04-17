package com.junior.dto.popUpEvent;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdatePopUpEventDto(
        String eventName,

        // 이벤트 관련 링크
        String eventUrl,
        String city,

        // 마커에 사용될 위도(latitude), 경도(longitude)
        double latitude,
        double longitude,

        // 이베트 시작 끝 날
        LocalDateTime startDate,
        LocalDateTime endDate
) {

}
