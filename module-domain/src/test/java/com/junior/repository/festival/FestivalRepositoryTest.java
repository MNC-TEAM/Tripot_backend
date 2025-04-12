package com.junior.repository.festival;


import com.junior.domain.festival.Festival;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.story.GeoPointDto;
import com.junior.dto.story.GeoRect;
import com.junior.repository.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class FestivalRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private FestivalRepository festivalRepository;

    @BeforeEach
    void init() {

        for (int i = 1; i <= 9; i++) {
            Festival festival = createFestival("축제 " + i, i % 2 == 1 ? "서울특별시" : "강원특별자치도", i % 2 == 1 ? 37.0 : 40.0, i % 2 == 1 ? 125.0 : 130.0);

            festivalRepository.save(festival);
        }
    }

    @Test
    @DisplayName("축제 개최시 개수 조회 - 응답을 정상적으로 반환해야 함")
    public void findFestivalCityCount() throws Exception {
        //given

        //when
        List<FestivalCityCountDto> festivalCityCount = festivalRepository.findFestivalCityCount();

        //then
        assertThat(festivalCityCount.get(0).count()).isEqualTo(4);          //강원에서 개최하는 축제는 5개
        assertThat(festivalCityCount.get(1).count()).isEqualTo(5);          //서울에서 개최하는 축제는 4개

    }

    @Test
    @DisplayName("지도 좌표 기반 축제 조회 - 응답을 정상적으로 반환해야 함")
    public void findFestivalByMap() throws Exception {
        //given
        GeoRect geoRect = GeoRect.builder()
                .geoPointLt(GeoPointDto.builder()
                        .latitude(35.0)
                        .longitude(125.0).build())
                .geoPointRb(GeoPointDto.builder()
                        .latitude(39.0)
                        .longitude(129.0).build())
                .build();

        GeoRect geoRect2 = GeoRect.builder()
                .geoPointLt(GeoPointDto.builder()
                        .latitude(39.0)
                        .longitude(129.0).build())
                .geoPointRb(GeoPointDto.builder()
                        .latitude(42.0)
                        .longitude(132.0).build())
                .build();

        //when
        List<FestivalMapDto> result = festivalRepository.findFestivalByMap(geoRect.geoPointLt(), geoRect.geoPointRb());
        List<FestivalMapDto> result2 = festivalRepository.findFestivalByMap(geoRect2.geoPointLt(), geoRect2.geoPointRb());

        //then
        //(37, 125) 5개, 40, 130 4개 존재
        assertThat(result.size()).isEqualTo(5);
        assertThat(result2.size()).isEqualTo(4);


    }

    @Test
    @DisplayName("축제 리스트 조회 - 응답을 정상적으로 반환해야 함")
    public void findFestival() throws Exception {
        //given
        Long cursorId = null;
        PageRequest pageRequest = PageRequest.of(0, 10);
        String city = "";
        String q = "";

        //when
        Slice<FestivalDto> result = festivalRepository.findFestival(cursorId, pageRequest, q, city);
        List<FestivalDto> resultList = result.getContent();

        //then
        assertThat(resultList.size()).isEqualTo(9);


    }

    @Test
    @DisplayName("축제 리스트 조회 - 슬라이싱이 정상적으로 동작해야 함")
    public void findFestivalSlice() throws Exception {
        //given
        Long cursorId = 4L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        String city = "";
        String q = "";

        //when
        Slice<FestivalDto> result = festivalRepository.findFestival(cursorId, pageRequest, q, city);
        List<FestivalDto> resultList = result.getContent();

        //then
        assertThat(resultList.size()).isEqualTo(3);


    }



    @Test
    @DisplayName("축제 리스트 조회 - 도시 기반 검색 기능이 정상적으로 동작해야 함")
    public void findFestivalByCity() throws Exception {
        //given
        Long cursorId = null;
        PageRequest pageRequest = PageRequest.of(0, 10);
        String city = "서울특별시";
        String q = "";

        //when
        Slice<FestivalDto> result = festivalRepository.findFestival(cursorId, pageRequest, city, q);
        List<FestivalDto> resultList = result.getContent();

        //then
        assertThat(resultList.size()).isEqualTo(5);

    }

    @Test
    @DisplayName("축제 리스트 조회 - 검색 기능이 정상적으로 동작해야 함")
    public void findFestivalByKeyword() throws Exception {
        //given
        Long cursorId = null;
        PageRequest pageRequest = PageRequest.of(0, 10);
        String city = "";
        String q = "3";

        //when
        Slice<FestivalDto> result = festivalRepository.findFestival(cursorId, pageRequest, city, q);
        List<FestivalDto> resultList = result.getContent();

        //then
        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0).title()).isEqualTo("축제 3");

    }
}
