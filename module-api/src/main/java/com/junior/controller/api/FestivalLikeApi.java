package com.junior.controller.api;


import com.junior.dto.festival.FestivalDto;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

public interface FestivalLikeApi {

    @Operation(summary = "축제 좋아요", description = "해당 축제에 좋아요 표시를 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "축제 좋아요 성공",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-LIKE-SUCCESS-001",
                                                              "customMessage": "",
                                                              "status": true,
                                                              "data": null
                                                            }
                                                            """
                                    ))
                    }),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원",
                                            value = """
                                                    {
                                                        "customCode": "MEMBER-ERR-001",
                                                        "customMessage": "없는 회원입니다.",
                                                        "status": false,
                                                        "data": null
                                                    }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "404", description = "축제 찾기 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                            examples = {
                                    @ExampleObject(name = "존재하지 않는 축제",
                                            value = """
                                                    {
                                                        "customCode": "FESTIVAL-LIKE-FAIL-002",
                                                        "customMessage": "해당 축제가 존재하지 않습니다.",
                                                        "status": false,
                                                        "data": null
                                                    }
                                                    """
                                    )
                            }))
    })
    ResponseEntity<CommonResponse<Object>> save(@AuthenticationPrincipal UserPrincipal principal,
                                                Long festivalId);

    @Operation(summary = "좋아요한 축제 조회", description = "좋아요한 축제 리스트를 조회합니다.")
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
    ResponseEntity<CommonResponse<Slice<FestivalDto>>> findFestivalLike(@AuthenticationPrincipal UserPrincipal principal,
                                                                        @RequestParam(name = "cursorId", required = false) Long cursorId, @RequestParam(name = "size") int size);

    @Operation(summary = "축제 좋아요", description = "해당 축제에 좋아요를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 좋아요 삭제 성공",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value =
                                                    """
                                                            {
                                                              "customCode": "FESTIVAL-LIKE-SUCCESS-002",
                                                              "customMessage": "",
                                                              "status": true,
                                                              "data": null
                                                            }
                                                            """
                                    ))
                    }),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 회원",
                                            value = """
                                                    {
                                                        "customCode": "MEMBER-ERR-001",
                                                        "customMessage": "없는 회원입니다.",
                                                        "status": false,
                                                        "data": null
                                                    }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "404", description = "축제 찾기 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                            examples = {
                                    @ExampleObject(name = "존재하지 않는 축제",
                                            value = """
                                                    {
                                                        "customCode": "FESTIVAL-LIKE-FAIL-002",
                                                        "customMessage": "해당 축제가 존재하지 않습니다.",
                                                        "status": false,
                                                        "data": null
                                                    }
                                                    """
                                    )
                            }))
    })
    ResponseEntity<CommonResponse<Object>> delete(@AuthenticationPrincipal UserPrincipal principal, Long festivalId);
}
