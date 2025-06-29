package com.junior.domain.popUpEvent;

import com.junior.domain.base.BaseEntity;
import com.junior.dto.popUpEvent.CreateNewPopUpEventDto;
import com.junior.dto.popUpEvent.UpdatePopUpEventDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopUpEvent extends BaseEntity {

    @Builder.Default
    boolean isDeleted = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 이벤트 이름
    @Column(nullable = false)
    private String eventName;
    @Column(length = 255)
    private String city;
    @Column(length = 255)
    private String location;
    // 이벤트 관련 링크
    @Column(nullable = false)
    private String eventUrl;
    // 마커에 사용될 위도(latitude), 경도(longitude)
    private double latitude;
    private double longitude;
    // 이베트 시작 끝 날
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;

    public static PopUpEvent from(CreateNewPopUpEventDto createNewPopUpEventDto) {
        return PopUpEvent.builder()
                .eventName(createNewPopUpEventDto.eventName())
                .eventUrl(createNewPopUpEventDto.eventUrl())
                .latitude(createNewPopUpEventDto.latitude())
                .longitude(createNewPopUpEventDto.longitude())
                .startDate(createNewPopUpEventDto.startDate())
                .endDate(createNewPopUpEventDto.endDate())
                .city(createNewPopUpEventDto.city())
                .location(createNewPopUpEventDto.location())
                .build();
    }

    public void update(UpdatePopUpEventDto updatePopUpEventDto) {
        this.eventName = updatePopUpEventDto.eventName();
        this.eventUrl = updatePopUpEventDto.eventUrl();
        this.latitude = updatePopUpEventDto.latitude();
        this.longitude = updatePopUpEventDto.longitude();
        this.startDate = updatePopUpEventDto.startDate();
        this.endDate = updatePopUpEventDto.endDate();
        this.city = updatePopUpEventDto.city();
        this.location = updatePopUpEventDto.location();
    }

    public void delete() {
        this.isDeleted = true;
    }
}
