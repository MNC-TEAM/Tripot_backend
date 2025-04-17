package com.junior.service.festival;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.config.WebClientConfig;
import com.junior.domain.festival.Festival;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDetailDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.festival.api.*;
import com.junior.dto.story.GeoPointDto;
import com.junior.dto.story.GeoRect;
import com.junior.repository.festival.FestivalRepository;
import com.junior.service.BaseServiceTest;
import com.junior.util.CustomStringUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class FestivalServiceTest extends BaseServiceTest {


    @InjectMocks
    private FestivalService festivalService;

    @Mock
    private FestivalRepository festivalRepository;

    public static MockWebServer mockWebServer;

    public static ObjectMapper objectMapper;



    @BeforeEach
    void init() throws IOException {

        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.start();


        String baseUrl = String.format("localhost:%s", mockWebServer.getPort());
        String key = "sample_api_key";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .uriBuilderFactory(factory)
                .build();
        // 조작된 WebClient 주입
        festivalService = new FestivalService(webClient, festivalRepository, baseUrl, key);



    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    @DisplayName("축제 저장 - 축제 저장 로직이 정상적으로 동작해야 함")
    void saveFestival() throws JsonProcessingException {

        //given
        FestivalApiItem sampleFestivalApi = FestivalApiItem.builder()
                .addr1("서울특별시 송파구 양재대로 932 (가락동)")
                .addr2("가락몰 3층 하늘공원")
                .contentid("3113671")
                .eventstartdate("20250509")
                .eventenddate("20250511")
                .firstimage("http://tong.visitkorea.or.kr/cms/resource/91/3484791_image2_1.jpg")
                .mapx("127.1107693087")
                .mapy("37.4960786971")
                .title("가락몰 빵축제 전국빵지자랑")
                .build();

        FestivalApiItems festivalApiItems = FestivalApiItems
                .builder()
                .item(new ArrayList<>())
                .build();

        festivalApiItems.getItem().add(sampleFestivalApi);

        FestivalBody<FestivalApiItems> festivalBody = FestivalBody.<FestivalApiItems>builder()
                .items(festivalApiItems)
                .pageNo(1)
                .numOfRows(1)
                .totalCount(1)
                .build();

        FestivalApiResponse<FestivalApiItems> festivalApiResponse = FestivalApiResponse.<FestivalApiItems>builder()
                .response(
                        FestivalApiInnerResponse.<FestivalApiItems>builder()
                                .header(FestivalHeader.builder()
                                        .resultCode("0000")
                                        .resultMsg("OK")
                                        .build())
                                .body(festivalBody)
                                .build()
                ).build();


        given(festivalRepository.existsByContentId(anyLong())).willReturn(false);
        //webClient 요청 시 받게 될 가짜 응답을 미리 설정
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(
                        objectMapper.writeValueAsString(festivalApiResponse)
                ));

        //when
        festivalService.saveFestival("20250101", "");

        //then
        verify(festivalRepository).save(any(Festival.class));
    }

    @Test
    @DisplayName("축제 개최시 개수 조회 - 축제 개최 시 개수와 총합을 정상적으로 리턴해야 함")
    void findFestivalCityCount() throws Exception {
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
    void findFestival() throws Exception {
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

    @Test
    @DisplayName("축제 상세정보 조회 - 축제 상세정보 조회 로직이 정상적으로 동작해야 함")
    void findFestivalDetail() throws JsonProcessingException {

        //given
        Festival festival = Festival.builder()
                .city("서울특별시")
                .contentId(3113671L)
                .startDate(CustomStringUtil.stringToDate("20250509"))
                .endDate(CustomStringUtil.stringToDate("20250511"))
                .title("가락몰 빵축제 전국빵지자랑")
                .location("송파구 양재대로 932 (가락동) 가락몰 3층 하늘공원")
                .imgUrl("imgUrl.com")
                .lat(37.4960786971)
                .logt(127.1107693087)
                .build();



        given(festivalRepository.findById(anyLong())).willReturn(Optional.ofNullable(festival));

        String detail = "전국 각지의 농수축산물이 모이는 가락몰에서, 전국 각지의 빵 맛집들이 모여 서울 최초의 전국 빵 축제를 개최한다.";
        FestivalDetailItem sampleFestivalApi = FestivalDetailItem.builder()
                .addr1("서울특별시 송파구 양재대로 932 (가락동)")
                .addr2("가락몰 3층 하늘공원")
                .contentid("3113671")
                .eventstartdate("20250509")
                .eventenddate("20250511")
                .firstimage("http://tong.visitkorea.or.kr/cms/resource/91/3484791_image2_1.jpg")
                .mapx("127.1107693087")
                .mapy("37.4960786971")
                .title("가락몰 빵축제 전국빵지자랑")
                .overview(detail)
                .build();

        FestivalDetailItems festivalDetailItems = FestivalDetailItems
                .builder()
                .item(new ArrayList<>())
                .build();

        festivalDetailItems.getItem().add(sampleFestivalApi);

        FestivalBody<FestivalDetailItems> festivalBody = FestivalBody.<FestivalDetailItems>builder()
                .items(festivalDetailItems)
                .pageNo(1)
                .numOfRows(1)
                .totalCount(1)
                .build();

        FestivalApiResponse<FestivalDetailItems> festivalApiResponse = FestivalApiResponse.<FestivalDetailItems>builder()
                .response(
                        FestivalApiInnerResponse.<FestivalDetailItems>builder()
                                .header(FestivalHeader.builder()
                                        .resultCode("0000")
                                        .resultMsg("OK")
                                        .build())
                                .body(festivalBody)
                                .build()
                ).build();


        //webClient 요청 시 받게 될 가짜 응답을 미리 설정
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(
                        objectMapper.writeValueAsString(festivalApiResponse)
                ));

        //when
        FestivalDetailDto festivalDetail = festivalService.findFestivalDetail(1L);

        //then
        assertThat(festivalDetail.detail()).isEqualTo(detail);
        assertThat(festivalDetail.contentId()).isEqualTo(festival.getContentId());

    }
}