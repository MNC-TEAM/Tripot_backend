package com.junior.dto.popUpEvent;

import com.junior.domain.popUpEvent.PopUpEvent;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponsePopUpEventDto {
    private Long id;
    private String eventName;
    private String eventUrl;
    private String city;
    private String location;
    private double latitude;
    private double longitude;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean isLiked; // 나중에 set

    @QueryProjection
    public ResponsePopUpEventDto(Long id, String eventName, String eventUrl, String city, String location, double latitude, double longitude, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.eventName = eventName;
        this.eventUrl = eventUrl;
        this.city = city;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public static ResponsePopUpEventDto from(PopUpEvent popUpEvent, boolean isLiked) {
        return ResponsePopUpEventDto.builder()
                .id(popUpEvent.getId())
                .eventName(popUpEvent.getEventName())
                .city(popUpEvent.getCity())
                .location(popUpEvent.getLocation())
                .latitude(popUpEvent.getLatitude())
                .longitude(popUpEvent.getLongitude())
                .startDate(popUpEvent.getStartDate())
                .endDate(popUpEvent.getEndDate())
                .isLiked(isLiked)
                .build();
    }
}
//public record ResponsePopUpEventDto(
//        Long id,
//        String eventName,
//
//        // 이벤트 관련 링크
//        String eventUrl,
//        String city,
//        String location,
//
//        // 마커에 사용될 위도(latitude), 경도(longitude)
//        double latitude,
//        double longitude,
//
//        // 이베트 시작 끝 날
//        LocalDateTime startDate,
//        LocalDateTime endDate
//) {
//
//    @QueryProjection
//    public ResponsePopUpEventDto(Long id, String eventName, String eventUrl, String city, String location, double latitude, double longitude, LocalDateTime startDate, LocalDateTime endDate) {
//        this.id = id;
//        this.eventName = eventName;
//        this.eventUrl = eventUrl;
//        this.city = city;
//        this.location = location;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }
//}
