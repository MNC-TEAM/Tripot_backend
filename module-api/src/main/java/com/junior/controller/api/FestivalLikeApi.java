package com.junior.controller.api;


import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
