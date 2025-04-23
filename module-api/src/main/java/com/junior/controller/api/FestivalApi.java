package com.junior.controller.api;

import com.junior.dto.festival.*;
import com.junior.dto.story.GeoRect;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "축제 관련")
@Validated
public interface FestivalApi {

    @Operation(summary = "축제 데이터 저장", description = "TourAPI 4.0에서 축제 데이터를 받아와 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "축제 데이터 저장 성공",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-SUCCESS-003",
                                                              "customMessage": "",
                                                              "status": true,
                                                              "data": null
                                                            }
                                                            """
                                    ))
                    }),
            @ApiResponse(responseCode = "500", description = "축제 데이터 저장 실패",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-ERR-001",
                                                              "customMessage": "축제 정보 불러오기에 실패했습니다.",
                                                              "status": false,
                                                              "data": null
                                                            }
                                                            """
                                    ))
                    })
    })
    ResponseEntity<CommonResponse<Object>> saveFestival(@Pattern(regexp = "^[0-9]{8}", message = "잘못 된 날짜 형식입니다.") @RequestParam(value = "eventStartDate") String eventStartDate,
                                                        @Pattern(regexp = "^[0-9]{8}|^$", message = "잘못 된 날짜 형식입니다.") @RequestParam(value = "eventEndDate", required = false, defaultValue = "") String eventEndDate);


    @Operation(summary = "축제 도시 개수", description = "시/도 별 축제의 개수를 응답합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 개수 응답 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "customCode": "FESTIVAL-SUCCESS-002",
                                              "customMessage": "",
                                              "status": true,
                                              "data": [
                                                {
                                                  "city": "강원특별자치도",
                                                  "count": 4
                                                },
                                                {
                                                  "city": "서울특별시",
                                                  "count": 5
                                                },
                                                {
                                                  "city": "all",
                                                  "count": 9
                                                }
                                              ]
                                            }
                                            """
                            ))),
    })
    ResponseEntity<CommonResponse<List<FestivalCityCountDto>>> findFestivalCityCount();


    @Operation(summary = "지도 기반 축제 위치 조회", description = "지도의 좌측 최상단, 우측 최하단 좌표를 받아 이 사이에 해당하는 축제의 위치를 리턴합니다.")
    @ApiResponse(responseCode = "200", description = "지도 기반 축제 리스트 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "customCode": "FESTIVAL-SUCCESS-004",
                              "customMessage": "",
                              "status": true,
                              "data": [
                                {
                                  "id": 1,
                                  "lat": 37,
                                  "logt": 125
                                },
                                {
                                  "id": 3,
                                  "lat": 37,
                                  "logt": 125
                                },
                                {
                                  "id": 5,
                                  "lat": 37,
                                  "logt": 125
                                },
                                {
                                  "id": 7,
                                  "lat": 37,
                                  "logt": 125
                                },
                                {
                                  "id": 9,
                                  "lat": 37,
                                  "logt": 125
                                }
                              ]
                            }
                            """)))
    ResponseEntity<CommonResponse<List<FestivalMapDto>>> findFestivalByMap(@RequestParam(name = "geoPointLtY") Double geoPointLtY,
                                                                           @RequestParam(name = "geoPointLtX") Double geoPointLtX,
                                                                           @RequestParam(name = "geoPointRbY") Double geoPointRbY,
                                                                           @RequestParam(name = "geoPointRbX") Double geoPointRbX);


    @Operation(summary = "축제 리스트 조회", description = "조건에 부합하는 축제 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "축제 리스트 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "customCode": "FESTIVAL-SUCCESS-001",
                                      "customMessage": "",
                                      "status": true,
                                      "data": {
                                        "content": [
                                          {
                                            "id": 9,
                                            "contentId": 890,
                                            "imgUrl": "url.com",
                                            "title": "축제 9",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "서울특별시",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 8,
                                            "contentId": 285,
                                            "imgUrl": "url.com",
                                            "title": "축제 8",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "강원특별자치도",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 7,
                                            "contentId": 571,
                                            "imgUrl": "url.com",
                                            "title": "축제 7",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "서울특별시",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 6,
                                            "contentId": 171,
                                            "imgUrl": "url.com",
                                            "title": "축제 6",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "강원특별자치도",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 5,
                                            "contentId": 365,
                                            "imgUrl": "url.com",
                                            "title": "축제 5",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "서울특별시",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 4,
                                            "contentId": 178,
                                            "imgUrl": "url.com",
                                            "title": "축제 4",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "강원특별자치도",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 3,
                                            "contentId": 827,
                                            "imgUrl": "url.com",
                                            "title": "축제 3",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "서울특별시",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 2,
                                            "contentId": 885,
                                            "imgUrl": "url.com",
                                            "title": "축제 2",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "강원특별자치도",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 1,
                                            "contentId": 3113671,
                                            "imgUrl": "url.com",
                                            "title": "축제 1",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "city": "서울특별시",
                                            "location": "서울특별시 location"
                                          }
                                        ],
                                        "pageable": {
                                          "pageNumber": 0,
                                          "pageSize": 10,
                                          "sort": {
                                            "empty": true,
                                            "unsorted": true,
                                            "sorted": false
                                          },
                                          "offset": 0,
                                          "unpaged": false,
                                          "paged": true
                                        },
                                        "size": 10,
                                        "number": 0,
                                        "sort": {
                                          "empty": true,
                                          "unsorted": true,
                                          "sorted": false
                                        },
                                        "first": true,
                                        "last": true,
                                        "numberOfElements": 9,
                                        "empty": false
                                      }
                                    }
                                    """
                    )))
    ResponseEntity<CommonResponse<Slice<FestivalDto>>> findFestival(@RequestParam(name = "cursorId", required = false) Long cursorId,
                                                                    @RequestParam(name = "size") int size,
                                                                    @RequestParam(name = "city", required = false, defaultValue = "") String city,
                                                                    @RequestParam(name = "q", required = false, defaultValue = "") String q);

    @GetMapping("/api/v1/festivals/{festival_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 상세정보 불러오기 성공",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-SUCCESS-005",
                                                              "customMessage": "",
                                                              "status": true,
                                                              "data": {
                                                                "id": 1,
                                                                "contentId": 3113671,
                                                                "city": "서울특별시",
                                                                "title": "축제 1",
                                                                "location": "서울특별시 location",
                                                                "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                                                "imgUrl": "url.com",
                                                                "detail": "전국 각지의 농수축산물이 모이는 가락몰에서, 전국 각지의 빵 맛집들이 모여 서울 최초의 전국 빵 축제를 개최한다. 축제는 5월 9일(금)부터 5월 11일(일)까지 3일간, 가락몰 하늘공원에서 진행된다. 20개의 전국 유명 빵집이 참여하며, 총 100종 이상의 다양한 빵을 선보인다. 축제 기간 동안 버블&매직쇼, 풍선 빵 오마카세와 같은 다양한 볼거리와 가락몰 및 행사장 구매고객대상 꽝없는 빵쿠폰 뽑기와 신라호텔 망고쇼트케이크, 나폴레옹과자점 쿠키세트 추첨 이벤트도 진행된다.선착순 400명의 사전 예약자에게는 1만원 빵쿠폰도 제공된다. 사진맛집 가락몰 하늘공원에서, 향긋한 빵 향기와 함께 가족, 친구, 연인과 멋진 추억을 남길 수 있다."
                                                              }
                                                            }
                                                            """
                                    ))
                    }),
            @ApiResponse(responseCode = "500", description = "축제 데이터 불러오기 실패",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-ERR-001",
                                                              "customMessage": "축제 정보 불러오기에 실패했습니다.",
                                                              "status": false,
                                                              "data": null
                                                            }
                                                            """
                                    ))
                    })
    })
    ResponseEntity<CommonResponse<FestivalDetailDto>> findFestivalDetail(@PathVariable("festival_id") Long festivalId);

    @GetMapping("/api/v1/admin/festivals")
    @ApiResponse(responseCode = "200", description = "축제 리스트 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "customCode": "FESTIVAL-SUCCESS-001",
                                      "customMessage": "",
                                      "status": true,
                                      "data": {
                                        "content": [
                                          {
                                            "id": 9,
                                            "title": "축제 9",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 8,
                                            "title": "축제 8",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 7,
                                            "title": "축제 7",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 6,
                                            "title": "축제 6",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 5,
                                            "title": "축제 5",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 4,
                                            "title": "축제 4",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 3,
                                            "title": "축제 3",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "서울특별시 location"
                                          },
                                          {
                                            "id": 2,
                                            "title": "축제 2",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "강원특별자치도 location"
                                          },
                                          {
                                            "id": 1,
                                            "title": "축제 1",
                                            "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                            "location": "서울특별시 location"
                                          }
                                        ],
                                        "pageable": {
                                          "number": 1,
                                          "size": 10,
                                          "sort": {
                                            "empty": true,
                                            "unsorted": true,
                                            "sorted": false
                                          },
                                          "first": true,
                                          "last": true,
                                          "hasNext": false,
                                          "totalPages": 1,
                                          "totalElements": 9,
                                          "numberOfElements": 9,
                                          "empty": false
                                        }
                                      }
                                    }
                                    """
                    )))
    ResponseEntity<CommonResponse<PageCustom<FestivalAdminDto>>> findFestivalAdmin(@PageableDefault(size = 15, page = 1) Pageable pageable, @RequestParam(name = "q") String q);

    @GetMapping("/api/v1/admin/festivals/{festival_id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 상세정보 불러오기 성공",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-SUCCESS-005",
                                                              "customMessage": "",
                                                              "status": true,
                                                              "data": {
                                                                "id": 1,
                                                                "contentId": 3113671,
                                                                "title": "축제 1",
                                                                "location": "서울특별시 location",
                                                                "duration": "2025년 1월 1일 - 2025년 1월 2일",
                                                                "detail": "전국 각지의 농수축산물이 모이는 가락몰에서, 전국 각지의 빵 맛집들이 모여 서울 최초의 전국 빵 축제를 개최한다. 축제는 5월 9일(금)부터 5월 11일(일)까지 3일간, 가락몰 하늘공원에서 진행된다. 20개의 전국 유명 빵집이 참여하며, 총 100종 이상의 다양한 빵을 선보인다. 축제 기간 동안 버블&매직쇼, 풍선 빵 오마카세와 같은 다양한 볼거리와 가락몰 및 행사장 구매고객대상 꽝없는 빵쿠폰 뽑기와 신라호텔 망고쇼트케이크, 나폴레옹과자점 쿠키세트 추첨 이벤트도 진행된다.선착순 400명의 사전 예약자에게는 1만원 빵쿠폰도 제공된다. 사진맛집 가락몰 하늘공원에서, 향긋한 빵 향기와 함께 가족, 친구, 연인과 멋진 추억을 남길 수 있다."
                                                              }
                                                            }
                                                            """
                                    ))
                    }),
            @ApiResponse(responseCode = "500", description = "축제 데이터 불러오기 실패",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-ERR-001",
                                                              "customMessage": "축제 정보 불러오기에 실패했습니다.",
                                                              "status": false,
                                                              "data": null
                                                            }
                                                            """
                                    ))
                    })
    })
    ResponseEntity<CommonResponse<FestivalDetailAdminDto>> findFestivalAdminDetail(@PathVariable("festival_id") Long festivalId);

}
