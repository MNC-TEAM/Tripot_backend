package com.junior.controller.api;

import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//POPUPEVENT_LIKE_CREATE_SUCCESS(200, "POPUPEVENT-LIKE-SUCCESS-001", "팝업이벤트 좋아요 성공"),
//INVALID_MEMBER(401, "MEMBER-ERR-001", "없는 회원 입니다."),
//POPUPEVENT_READ_FAIL(400, "POPUPEVENT-ERR-001", "팝업이벤트 조회 실패"),
//POPUPEVENT_LIKE_NOT_FOUND(400, "POPUPEVENT-LIKE-FAIL-002", "북마크 되지 않은 팝업 이벤트"),
@Tag(name = "팝업스토어 북마크")
public interface PopUpEventLikeApi {
    @Operation(summary = "팝업스토어 북마크", description = "팝업스토어 상세 조회에서 하트 버튼을 눌렀을 때 북마크 성공, 삭제 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팝업이벤트 북마크 생성 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "POPUPEVENT-LIKE-SUCCESS-001",
                                                                "customMessage": "팝업이벤트 좋아요 전환 성공",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "회원이 없는 경우",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "없는 회원 입니다.",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "팝업이벤트 조회 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "POPUPEVENT-ERR-001",
                                                                "customMessage": "팝업이벤트 조회 실패",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "팝업이벤트 좋아요 정보 조회 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "POPUPEVENT-LIKE-FAIL-002",
                                                                "customMessage": "북마크 되지 않은 팝업 이벤트",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            })
    public CommonResponse<Object> likePopUpEvent(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable Long popUpEventId);

    @Operation(summary = "팝업스토어 북마크 조회", description = "북마크 리스트 조회",
            responses = {
                    //POPUPEVENT_READ_SUCCESS(200, "POPUPEVENT-SUCCESS-002", "팝업이벤트 조회 성공"),
                    @ApiResponse(responseCode = "200", description = "북마크한 팝업 리스트 조회",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                              "customCode": "POPUPEVENT-SUCCESS-002",
                                                              "customMessage": "팝업이벤트 조회 성공",
                                                              "status": true,
                                                              "data": {
                                                                "first": true,
                                                                "last": true,
                                                                "size": 0,
                                                                "content": [
                                                                  {
                                                                    "id": 0,
                                                                    "eventName": "string",
                                                                    "eventUrl": "string",
                                                                    "city": "string",
                                                                    "location": "string",
                                                                    "latitude": 0,
                                                                    "longitude": 0,
                                                                    "startDate": "2025-05-18T02:54:41.591Z",
                                                                    "endDate": "2025-05-18T02:54:41.592Z",
                                                                    "liked": true
                                                                  }
                                                                ],
                                                                "number": 0,
                                                                "sort": [
                                                                  {
                                                                    "direction": "string",
                                                                    "nullHandling": "string",
                                                                    "ascending": true,
                                                                    "property": "string",
                                                                    "ignoreCase": true
                                                                  }
                                                                ],
                                                                "numberOfElements": 0,
                                                                "pageable": {
                                                                  "offset": 0,
                                                                  "sort": [
                                                                    {
                                                                      "direction": "string",
                                                                      "nullHandling": "string",
                                                                      "ascending": true,
                                                                      "property": "string",
                                                                      "ignoreCase": true
                                                                    }
                                                                  ],
                                                                  "unpaged": true,
                                                                  "pageSize": 0,
                                                                  "paged": true,
                                                                  "pageNumber": 0
                                                                }
                                                              }
                                                            }
                                                                    """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "회원이 없는 경우",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "없는 회원 입니다.",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            })
    public CommonResponse<Slice<ResponsePopUpEventDto>> getLikedPopUpEvents(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    );


    @Operation(summary = "팝업스토어 북마크 삭제", description = "북마크 삭제")
    public CommonResponse<Slice<Object>> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable(name = "festival_id") Long festivalId);
}
