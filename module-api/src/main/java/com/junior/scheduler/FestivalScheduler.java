package com.junior.scheduler;

import com.junior.domain.festival.Festival;
import com.junior.dto.festival.api.FestivalApiItem;
import com.junior.dto.festival.api.FestivalApiItems;
import com.junior.dto.festival.api.FestivalApiResponse;
import com.junior.exception.CustomException;
import com.junior.exception.StatusCode;
import com.junior.repository.festival.FestivalRepository;
import com.junior.util.CustomStringUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class FestivalScheduler {

    private final WebClient webClient;
    private final FestivalRepository festivalRepository;
    private final String festivalUrl;
    private final String festivalApiKey;

    public FestivalScheduler(WebClient webClient, FestivalRepository festivalRepository, @Value("${data-api.festival.url}") String festivalUrl, @Value("${data-api.festival.key}") String festivalApiKey) {
        this.webClient = webClient;
        this.festivalRepository = festivalRepository;
        this.festivalUrl = festivalUrl;
        this.festivalApiKey = festivalApiKey;
    }

    /**
     * 매일 자정 해당 일자부터 말일까지 방문 가능한 모든 축제 저장
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    @Transactional
    public void saveFestivalAuto() {

        String eventStartDate = LocalDate.now().toString().replaceAll("-", "");
        String eventEndDate = "";

        String[] thOne = {"01", "03", "05", "07", "08", "10", "12"};

        if (Arrays.asList(thOne).contains(eventStartDate.substring(4, 6))) {
            eventEndDate = eventStartDate.substring(0, 6) + "31";
        } else if (eventStartDate.substring(4, 6).equals("02")) {
            //윤년 확인
            int year = Integer.parseInt(eventStartDate.substring(0, 4));
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                eventEndDate = eventStartDate.substring(0, 6) + "29";
            } else {
                eventEndDate = eventStartDate.substring(0, 6) + "28";
            }
        } else {
            eventEndDate = eventStartDate.substring(0, 6) + "30";
        }

        String finalEventEndDate = eventEndDate;

        log.info("[{}] 축제 정보 가져오기 eventStartDate: {}, eventEndDate: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), eventStartDate, finalEventEndDate);

        FestivalApiResponse<FestivalApiItems> result = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(festivalUrl)
                        .path("/searchFestival1")
                        .queryParam("numOfRows", 2000)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")              //TODO: 서로 다른 환경에 대한 처리 -> 운영 계정 승인 시 고려
                        .queryParam("MobileApp", "Tripot")
                        .queryParam("_type", "json")
                        .queryParam("listYN", "Y")
                        .queryParam("eventStartDate", eventStartDate)
                        .queryParam("eventEndDate", finalEventEndDate)
                        .queryParam("serviceKey", festivalApiKey)
                        .build(true))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<FestivalApiResponse<FestivalApiItems>>() {
                })
                .block();

        if (result.getResponse() == null || !result.getResponse().getHeader().getResultCode().equals("0000")) {
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
                        .startDate(CustomStringUtil.stringToDate(festivalInfo.getEventstartdate()))
                        .endDate(CustomStringUtil.stringToDate(festivalInfo.getEventenddate()))
                        .lat(Double.valueOf(festivalInfo.getMapy()))
                        .logt(Double.valueOf(festivalInfo.getMapx()))
                        .build();

                festivalRepository.save(festival);

            }
            //존재하는 축제일 경우 값 업데이트
            else{
                Festival festival = festivalRepository.findByContentId(Long.valueOf(festivalInfo.getContentid())).get();
                festival.updateInfo(festivalInfo);

            }
        }
    }


}
