package com.junior.service.festival;

import com.junior.domain.festival.Festival;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.festival.api.FestivalApiItem;
import com.junior.dto.festival.api.FestivalApiResponse;
import com.junior.dto.story.GeoPointDto;
import com.junior.dto.story.GeoRect;
import com.junior.exception.CustomException;
import com.junior.exception.StatusCode;
import com.junior.repository.festival.FestivalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FestivalService {

    private final FestivalRepository festivalRepository;

    @Value("${data-api.festival.url}")
    private String festivalUrl;

    @Value("${data-api.festival.key}")
    private String festivalApiKey;

    /**
     * 축제 데이터를 가져와 저장하는 기능
     * 관리자 권한으로만 수행 가능
     */

    //TODO: 이거 매월 초 자동실행시켜도 무방한지 고민해보기
    @Transactional
    public void saveFestival(String eventStartDate, String eventEndDate){

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(festivalUrl);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient.builder().uriBuilderFactory(uriBuilderFactory).build();

        FestivalApiResponse result = WebClient.builder()
                .uriBuilderFactory(uriBuilderFactory)
                .baseUrl(festivalUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024))     //DataBufferLimitException 해결
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/searchFestival1")
                        .queryParam("numOfRows", 2000)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "IOS")              //TODO: 서로 다른 환경에 대한 처리 -> 운영 계정 승인 시 고려
                        .queryParam("MobileApp", "Tripot")
                        .queryParam("_type", "json")
                        .queryParam("listYN", "Y")
                        .queryParam("eventStartDate", eventStartDate)
                        .queryParam("eventEndDate", eventEndDate)
                        .queryParam("serviceKey", festivalApiKey)
                        .build(true))
                .retrieve()
                .bodyToMono(FestivalApiResponse.class)
                .block();

        if (result.getResponse()==null || !result.getResponse().getHeader().getResultCode().equals("0000")) {
            throw new CustomException(StatusCode.FESTIVAL_CREATE_FAIL);
        }

        List<FestivalApiItem> item = result.getResponse().getBody().getItems().getItem();

        for (FestivalApiItem festivalInfo : item) {


            if (!festivalRepository.existsByContentId(Long.valueOf(festivalInfo.getContentid()))) {
                String fullLocation = festivalInfo.getAddr1() + " " + festivalInfo.getAddr2();


                String[] split = fullLocation.split(" ");
                String city = split.length != 0 ? split[0] : "";
                String location = split.length != 0 ? fullLocation.substring(city.length()).trim() : "";


                Festival festival = Festival.builder()
                        .contentId(Long.valueOf(festivalInfo.getContentid()))
                        .title(festivalInfo.getTitle())
                        .city(city)
                        .location(location)
                        .imgUrl(festivalInfo.getFirstimage())
                        .startDate(stringToDate(festivalInfo.getEventstartdate()))
                        .endDate(stringToDate(festivalInfo.getEventenddate()))
                        .lat(Double.valueOf(festivalInfo.getMapy()))
                        .logt(Double.valueOf(festivalInfo.getMapx()))
                        .build();

                festivalRepository.save(festival);

            }
        }
    }

    public List<FestivalMapDto> findFestivalByMap(GeoRect geoRect){

        log.info("[{}] 지도 내 확인 가능한 축제 내역 조회", Thread.currentThread().getStackTrace()[1].getMethodName());

        return festivalRepository.findFestivalByMap(geoRect.geoPointLt(), geoRect.geoPointRb());
    }

    public List<FestivalCityCountDto> findFestivalCityCount(){
        List<FestivalCityCountDto> festivalCityCount = festivalRepository.findFestivalCityCount();

        long sum = festivalCityCount.stream()
                .mapToLong(FestivalCityCountDto::count)
                .sum();

        festivalCityCount.add(FestivalCityCountDto.builder()
                .city("all").count(sum).build());

        return festivalCityCount;
    }


    /**
     * @param date yyyyMMdd 형태의 문자열
     * @return 해당 일자에 맞는 LocalDate 객체
     */
    private static LocalDate stringToDate(String date) {
        return LocalDate.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8)));
    }


    /**
     * @param startDate
     * @param endDate
     * @return 시작일과 종료일을 ****년 **월 **일 - ****년 **월 **일 형태로 리턴
     */
    private static String durationToString(String startDate, String endDate) {

        //시작일 및 종료일이 8자리 숫자가 아닐 경우 빈 문자열 리턴
        if (!Pattern.matches("^[0-9]{8}", startDate) || !Pattern.matches("^[0-9]{8}", startDate)) {
            return "";
        }

        return String.format("%d년 %d월 %d일 - %d년 %d월 %d일", Integer.parseInt(startDate.substring(0, 4)), Integer.parseInt(startDate.substring(4, 6)), Integer.parseInt(startDate.substring(6, 8)),
                Integer.parseInt(endDate.substring(0, 4)), Integer.parseInt(endDate.substring(4, 6)), Integer.parseInt(endDate.substring(6, 8)));

    }

}
