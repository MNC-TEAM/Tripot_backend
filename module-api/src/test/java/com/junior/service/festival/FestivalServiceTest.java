package com.junior.service.festival;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.domain.festival.Festival;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.api.*;
import com.junior.repository.festival.FestivalRepository;
import com.junior.service.BaseServiceTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
}