package com.junior.controller.api;

import com.junior.dto.popUpEvent.CreateNewPopUpEventDto;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PopUpEvent")
public interface PopUpEventApi {

    @Operation(summary = "팝업스토어", description = "팝업스토어 생성",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팝업이벤트 생성 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "POPUPEVENT-SUCCESS-001",
                                                                "customMessage": "팝업이벤트 생성 성공",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "권한이 없는 계정입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "PERMISSION-ERROR-001",
                                                                "customMessage": "권한이 없는 계정입니다.",
                                                                "status": true,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            })

    public CommonResponse<Object> createEvent(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @RequestBody CreateNewPopUpEventDto createNewPopUpEventDto);
}
