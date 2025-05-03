package com.junior.dto.festival.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
public class FestivalApiItem {

    private String addr1;
    private String addr2;
    private String booktour;
    private String cat1;
    private String cat2;
    private String cat3;

    //공공데이터 지정 축제 고유번호
    private String contentid;

    //축제: 15
    private String contenttypeid;
    private String createdtime;

    //축제 시작일 및 종료일
    private String eventstartdate;
    private String eventenddate;

    //원본 이미지 (500*333)
    private String firstimage;

    //썸네일 이미지(150*100)
    private String firstimage2;

    //저작권 유형  Type1:제1유형(출처표시-권장) / Type3:제3유형(제1유형 + 변경금지)
    private String cpyrhtDivCd;

    //(x, y): (경도(logt), 위도(lat))
    private String mapx;
    private String mapy;


    private String mlevel;
    private String modifiedtime;
    private String areacode;
    private String sigungucode;
    private String tel;

    //콘텐츠 제목
    private String title;



}
