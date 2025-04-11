package com.junior.dto.popUpEvent;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResponsePopUpEventDto(
        Long id,
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

    @QueryProjection

    public ResponsePopUpEventDto(Long id, String eventName, String eventUrl, String city, double latitude, double longitude, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.eventName = eventName;
        this.eventUrl = eventUrl;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
