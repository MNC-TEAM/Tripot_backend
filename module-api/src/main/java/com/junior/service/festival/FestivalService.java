package com.junior.service.festival;

import com.junior.domain.festival.Festival;
import com.junior.domain.member.Member;
import com.junior.dto.festival.*;
import com.junior.dto.festival.api.*;
import com.junior.exception.CustomException;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.festival.like.FestivalLikeRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.util.CustomStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class FestivalService {

    private final WebClient webClient;

    private final FestivalRepository festivalRepository;
    private final MemberRepository memberRepository;

    private final String festivalUrl;

    private final String festivalApiKey;
    private final FestivalLikeRepository festivalLikeRepository;


    public FestivalService(WebClient webClient,
                           FestivalRepository festivalRepository,
                           MemberRepository memberRepository,
                           FestivalLikeRepository festivalLikeRepository,
                           @Value("${data-api.festival.url}") String festivalUrl,
                           @Value("${data-api.festival.key}") String festivalApiKey
    ) {

        this.webClient = webClient;
        this.festivalRepository = festivalRepository;
        this.memberRepository = memberRepository;
        this.festivalUrl = festivalUrl;
        this.festivalApiKey = festivalApiKey;
        this.festivalLikeRepository = festivalLikeRepository;
    }

    /**
     * 축제 데이터를 가져와 저장하는 기능
     * 관리자 권한으로만 수행 가능
     */

    @Transactional
    public void saveFestival(String eventStartDate, String eventEndDate) {

        String[] thOne = {"01", "03", "05", "07", "08", "10", "12"};

        if (eventEndDate.isEmpty()) {
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

            //존재하지 않는 축제일 경우 저장
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

    public List<FestivalMapDto> findFestivalByMap(Double geoPointLtY,
                                                  Double geoPointLtX,
                                                  Double geoPointRbY,
                                                  Double geoPointRbX) {

        log.info("[{}] 지도 내 확인 가능한 축제 내역 조회", Thread.currentThread().getStackTrace()[1].getMethodName());

        return festivalRepository.findFestivalByMap(geoPointLtY, geoPointLtX, geoPointRbY, geoPointRbX);
    }

    public List<FestivalCityCountDto> findFestivalCityCount() {
        List<FestivalCityCountDto> festivalCityCount = festivalRepository.findFestivalCityCount();

        long sum = festivalCityCount.stream()
                .mapToLong(FestivalCityCountDto::count)
                .sum();

        festivalCityCount.add(FestivalCityCountDto.builder()
                .city("all").count(sum).build());

        return festivalCityCount;
    }

    public Slice<FestivalDto> findFestival(Long cursorId, int size, String city, String q) {

        PageRequest pageRequest = PageRequest.of(0, size);

        log.info("[{}] 축제 리스트 조회", Thread.currentThread().getStackTrace()[1].getMethodName());

        return festivalRepository.findFestival(cursorId, pageRequest, city, q);
    }

    public FestivalDetailDto findFestivalDetail(Long id, UserPrincipal principal) {

        Festival targetFestival = festivalRepository.findById(id)
                .orElseThrow(() -> new CustomException(StatusCode.FESTIVAL_NOT_FOUND));

        Member member = principal != null ? memberRepository.findById(principal.getMember().getId())
                .orElseThrow(() -> new NotValidMemberException(StatusCode.MEMBER_NOT_FOUND)) : null;

        log.info("[{}] 축제 상세정보 조회 title: {}, contentId: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), targetFestival.getTitle(), targetFestival.getContentId());
        FestivalApiResponse<FestivalDetailItems> result = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(festivalUrl)
                        .path("/detailCommon1")
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "Tripot")
                        .queryParam("_type", "json")
                        .queryParam("contentId", targetFestival.getContentId())
                        .queryParam("defaultYN", "Y")
                        .queryParam("firstImageYN", "Y")
                        .queryParam("addrinfoYN", "Y")
                        .queryParam("mapinfoYN", "Y")
                        .queryParam("overviewYN", "Y")
                        .queryParam("serviceKey", festivalApiKey)
                        .build(true))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<FestivalApiResponse<FestivalDetailItems>>() {
                })
                .block();

        if (result.getResponse() == null || !result.getResponse().getHeader().getResultCode().equals("0000") ||
                result.getResponse().getBody().getItems().getItem().isEmpty()) {
            throw new CustomException(StatusCode.FESTIVAL_DETAIL_FOUND_FAIL);
        }

        FestivalDetailItem item = result.getResponse().getBody().getItems().getItem().get(0);

        boolean isLiked = member != null && festivalLikeRepository.existsByMemberAndFestival(member, targetFestival);

        return FestivalDetailDto.builder()
                .id(id)
                .contentId(Long.valueOf(item.getContentid()))
                .city(targetFestival.getCity())
                .title(targetFestival.getTitle())
                .location(targetFestival.getCity() + " " + targetFestival.getLocation())
                .duration(CustomStringUtil.durationToString(targetFestival.getStartDate(), targetFestival.getEndDate()))
                .imgUrl(targetFestival.getImgUrl())
                .detail(item.getOverview())
                .isLiked(isLiked)
                .build();
    }

    public PageCustom<FestivalAdminDto> findFestivalAdmin(Pageable pageable, String q) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());

        Page<FestivalAdminDto> result = festivalRepository.findFestivalAdmin(pageRequest, q);

        return new PageCustom<>(result.getContent(), result.getPageable(), result.getTotalElements());
    }

    public FestivalDetailAdminDto findFestivalAdminDetail(Long id) {

        Festival targetFestival = festivalRepository.findById(id)
                .orElseThrow(() -> new CustomException(StatusCode.FESTIVAL_NOT_FOUND));

        log.info("[{}] 관리자 축제 상세정보 조회 title: {}, contentId: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), targetFestival.getTitle(), targetFestival.getContentId());
        FestivalApiResponse<FestivalDetailItems> result = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(festivalUrl)
                        .path("/detailCommon1")
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "Tripot")
                        .queryParam("_type", "json")
                        .queryParam("contentId", targetFestival.getContentId())
                        .queryParam("defaultYN", "Y")
                        .queryParam("firstImageYN", "Y")
                        .queryParam("addrinfoYN", "Y")
                        .queryParam("mapinfoYN", "Y")
                        .queryParam("overviewYN", "Y")
                        .queryParam("serviceKey", festivalApiKey)
                        .build(true))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<FestivalApiResponse<FestivalDetailItems>>() {
                })
                .block();

        if (result.getResponse() == null || !result.getResponse().getHeader().getResultCode().equals("0000") ||
                result.getResponse().getBody().getItems().getItem().isEmpty()) {
            throw new CustomException(StatusCode.FESTIVAL_DETAIL_FOUND_FAIL);
        }

        FestivalDetailItem item = result.getResponse().getBody().getItems().getItem().get(0);


        return FestivalDetailAdminDto.builder()
                .id(id)
                .contentId(Long.valueOf(item.getContentid()))
                .title(targetFestival.getTitle())
                .location(targetFestival.getCity() + " " + targetFestival.getLocation())
                .duration(CustomStringUtil.durationToString(targetFestival.getStartDate(), targetFestival.getEndDate()))
                .detail(item.getOverview())
                .build();
    }


}
