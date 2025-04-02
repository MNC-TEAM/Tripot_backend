package com.junior.controller.api;

import com.junior.dto.jwt.RefreshTokenDto;
import com.junior.dto.member.CheckActiveMemberDto;
import com.junior.dto.oauth2.OAuth2LoginDto;
import com.junior.dto.oauth2.OAuth2Provider;
import com.junior.response.CommonResponse;
import com.junior.validator.EnumValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "OAuth2")
public interface OAuth2Api {

    @Operation(summary = "소셜 로그인", description = "소셜 로그인을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "소셜 로그인 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "JWT-SUCCESS-001",
                                                        "customMessage": "JWT 재발급 완료",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    )))
            })
    public CommonResponse<CheckActiveMemberDto> oauth2Login(HttpServletResponse response, @Valid @RequestBody OAuth2LoginDto oAuth2LoginDto, @EnumValue(enumClass = OAuth2Provider.class, ignoreCase = true) @PathVariable("provider") String provider);


    @Operation(summary = "로그 아웃", description = "로그 아웃을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 완료",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "JWT-SUCCESS-002",
                                                        "customMessage": "로그아웃 완료",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    )))
            })
    public CommonResponse<Boolean> logout(@Valid @RequestBody RefreshTokenDto refreshTokenDto);
}
