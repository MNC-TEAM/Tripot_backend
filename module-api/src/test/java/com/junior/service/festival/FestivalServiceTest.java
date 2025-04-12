package com.junior.service.festival;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.festival.api.*;
import com.junior.dto.story.GeoPointDto;
import com.junior.dto.story.GeoRect;
import com.junior.repository.festival.FestivalRepository;
import com.junior.service.BaseServiceTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(initializers = {ConfigDataApplicationContextInitializer.class}, classes = {FestivalService.class})           //@Value를 사용하는 로직에서 환경변수를 가져오는 코드
class FestivalServiceTest extends BaseServiceTest {


    @InjectMocks
    private FestivalService festivalService;

    @Mock
    private FestivalRepository festivalRepository;

    @Mock
    public static MockWebServer mockWebServer;

    public static ObjectMapper objectMapper;


    @Test
    @DisplayName("축제 개최시 개수 조회 - 축제 개최 시 개수와 총합을 정상적으로 리턴해야 함")
    public void findFestivalCityCount() throws Exception {
        //given
        List<FestivalCityCountDto> festivalCityCountDto = new ArrayList<>();
        festivalCityCountDto.add(FestivalCityCountDto.builder().city("강원특별자치도").count(4).build());
        festivalCityCountDto.add(FestivalCityCountDto.builder().city("서울특별시").count(5).build());

        given(festivalRepository.findFestivalCityCount()).willReturn(festivalCityCountDto);

        //when
        List<FestivalCityCountDto> result = festivalService.findFestivalCityCount();

        //then
        assertThat(result.get(2).count()).isEqualTo(9);

    }

    @Test
    @DisplayName("지도 좌표 기반 축제 리스트 출력 - 해당 조건에 맞는 축제의 ID와 좌표를 정상적으로 리턴해야 함")
    void findFestivalByMap() throws Exception {
        //given
        GeoRect geoRect = GeoRect.builder()
                .geoPointLt(GeoPointDto.builder()
                        .latitude(35.0)
                        .longitude(125.0).build())
                .geoPointRb(GeoPointDto.builder()
                        .latitude(39.0)
                        .longitude(129.0).build())
                .build();

        List<FestivalMapDto> festivalMapDtoList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            festivalMapDtoList.add(FestivalMapDto.builder()
                    .id((long) i)
                    .lat(37.0)
                    .logt(127.0)
                    .build());
        }

        given(festivalRepository.findFestivalByMap(geoRect.geoPointLt(), geoRect.geoPointRb()))
                .willReturn(festivalMapDtoList);

        //when
        List<FestivalMapDto> result = festivalService.findFestivalByMap(geoRect);

        //then
        assertThat(result.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("축제 리스트 출력 - 축제 리스트를 출력해야 함")
    public void findFestival() throws Exception {
        //given
        Long cursorId = 5L;
        int size = 10;
        String city = "";
        String q = "";

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<FestivalDto> resultList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            resultList.add(FestivalDto.builder()
                    .contentId((long) i)
                    .duration("duration")
                    .imgUrl("imgurl")
                    .location("location")
                    .title("title")
                    .id((long) i)
                    .build());
        }

        Slice<FestivalDto> result = new SliceImpl<>(resultList, pageRequest, false);

        given(festivalRepository.findFestival(anyLong(), any(Pageable.class), anyString(), anyString())).willReturn(result);

        //when
        Slice<FestivalDto> res = festivalService.findFestival(cursorId, size, city, q);

        //then
        assertThat(res.getContent().size()).isEqualTo(4);

    }
}