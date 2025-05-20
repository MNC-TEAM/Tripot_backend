package com.junior.controller.api;

import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

//POPUPEVENT_LIKE_CREATE_SUCCESS(200, "POPUPEVENT-LIKE-SUCCESS-001", "팝업이벤트 좋아요 성공"),
//INVALID_MEMBER(401, "MEMBER-ERR-001", "없는 회원 입니다."),
//POPUPEVENT_READ_FAIL(400, "POPUPEVENT-ERR-001", "팝업이벤트 조회 실패"),
//POPUPEVENT_LIKE_NOT_FOUND(400, "POPUPEVENT-LIKE-FAIL-002", "북마크 되지 않은 팝업 이벤트"),
@Tag(name = "팝업스토어 북마크")
public interface PopUpEventLikeApi {
    @Operation(summary = "팝업스토어 북마크", description = "팝업스토어 북마크 전환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팝업이벤트 생성 성공",
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

//    @Operation(summary = "팝업스토어 북마크 조회", description = "북마크 리스트 조회",
//    responses = {
//            //POPUPEVENT_READ_SUCCESS(200, "POPUPEVENT-SUCCESS-002", "팝업이벤트 조회 성공"),
//            @ApiResponse(responseCode = "200", description = "북마크한 팝업 리스트 조회",
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
//                            examples = {
//                                    @ExampleObject(
//                                            value = """
//                                                            {
//                                                                "customCode": "POPUPEVENT-SUCCESS-002",
//                                                                "customMessage": "팝업이벤트 조회 성공",
//                                                                "status": true,
//                                                                "data": null
//                                                            }
//                                                            """
//                                    )
//                            }
//                    )
//            ),
//            @ApiResponse(responseCode = "401", description = "회원이 없는 경우",
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
//                            examples = {
//                                    @ExampleObject(
//                                            value = """
//                                                            {
//                                                                "customCode": "MEMBER-ERR-001",
//                                                                "customMessage": "없는 회원 입니다.",
//                                                                "status": true,
//                                                                "data": null
//                                                            }
//                                                            """
//                                    )
//                            }
//                    )
//            )
//    })
//    public CommonResponse<Slice<ResponsePopUpEventDto>> getLikedPopUpEvents(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestParam(required = false) Long cursorId,
//            @RequestParam(defaultValue = "10") int size
//    );


//    @Operation(summary = "팝업스토어 북마크 삭제", description = "북마크 삭제")
//    public CommonResponse<Slice<Object>> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable(name = "festival_id") Long festivalId);
}
