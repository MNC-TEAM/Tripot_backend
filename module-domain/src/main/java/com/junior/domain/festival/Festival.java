package com.junior.domain.festival;

import com.junior.dto.festival.api.FestivalApiItem;
import com.junior.util.CustomStringUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(indexes = {@Index(name = "idx_festival_start_date_end_date_lat_logt", columnList = "start_date, end_date, lat, logt")})
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //상세정보 조회용 ID
    private Long contentId;

    //축제명
    private String title;

    //개최 지역
    private String city;

    //개최 장소
    private String location;

    //이미지
    private String imgUrl;


    //축제시작일자
    private LocalDate startDate;
    //축제종료일자
    private LocalDate endDate;


    //개최 장소 좌표(위도, 경도)
    private Double lat;
    private Double logt;


    public void updateInfo(FestivalApiItem festivalInfo) {
        String fullLocation = festivalInfo.getAddr1() + " " + festivalInfo.getAddr2();


        String[] split = fullLocation.split(" ");
        String city = split.length != 0 ? split[0] : "";
        String location = split.length != 0 ? fullLocation.substring(city.length()).trim() : "";

        this.contentId = Long.valueOf(festivalInfo.getContentid());
        this.title = festivalInfo.getTitle();
        this.city = city;
        this.location = location;
        this.imgUrl = festivalInfo.getFirstimage();
        this.startDate = CustomStringUtil.stringToDate(festivalInfo.getEventstartdate());
        this.endDate = CustomStringUtil.stringToDate(festivalInfo.getEventenddate());
        this.lat = Double.valueOf(festivalInfo.getMapy());
        this.logt = Double.valueOf(festivalInfo.getMapx());
    }
}
