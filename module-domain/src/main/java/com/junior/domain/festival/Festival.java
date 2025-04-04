package com.junior.domain.festival;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //상세정보 조회용 ID
    private Long contentId;

    //축제명
    private String title;

    //개최 장소
    private String location;

    //이미지
    private String imgUrl;


    //축제시작일자
    private LocalDate startDate;
    //축제종료일자
    private LocalDate endDate;

    //축제 내용
    @Builder.Default
    private String detail = "";


    //개최 장소 좌표(위도, 경도)
    private Double lat;
    private Double logt;


}
