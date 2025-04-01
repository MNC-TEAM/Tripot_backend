package com.junior.controller.api;

import com.junior.dto.member.ActivateMemberDto;
import com.junior.dto.member.MemberInfoDto;
import com.junior.dto.member.UpdateNicknameDto;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member")
public interface MemberApi {

    @Operation(summary = "회원 활성화", description = "회원의 추가정보를 입력받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 활성화 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "MEMBER-SUCCESS-001",
                                                        "customMessage": "회원 활성화 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "유효하지 않은 회원",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "해당 회원 status로 실행할 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-002",
                                                                "customMessage": "해당 회원 status로 실행할 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<String> activeMember(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody ActivateMemberDto activateMemberDto);

    @Operation(summary = "유효 닉네임 확인", description = "닉네임의 사용가능여부를 받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "닉네임 사용가능 여부",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "customCode": "MEMBER-SUCCESS-002",
                                                      "customMessage": "닉네임 사용가능 여부",
                                                      "status": true,
                                                      "data": true
                                                    }
                                                    """
                                    )))
            })
    public CommonResponse<Boolean> checkNicknameValid(@Size(max = 25, message = "닉네임은 25자까지 가능합니다.")
                                                          @NotNull(message = "닉네임은 필수 값입니다.")
                                                          @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]+$", message = "잘못된 닉네임 형식입니다.") String nickname);

    @Operation(summary = "회원 조회", description = "회원의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 조회",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "MEMBER-SUCCESS-007",
                                                        "customMessage": "회원 정보 조회 성공",
                                                        "status": true,
                                                        "data": {
                                                            "nickname": "테스트닉",
                                                            "profileImageUrl": "https://tripot.s3.eu-north-1.amazonaws.com/user/profile/bdbf3002-ee4f-454c-9925-ab9e025f0cbe.png"
                                                        }
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "유효하지 않은 회원",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "해당 회원 status로 실행할 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-002",
                                                                "customMessage": "해당 회원 status로 실행할 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<MemberInfoDto> getMemberInfo(@AuthenticationPrincipal UserPrincipal principal);

    @Operation(summary = "회원 닉네임 수정", description = "회원의 닉네임을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 닉네임 변경 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "MEMBER-SUCCESS-005",
                                                        "customMessage": "회원 닉네임 변경 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "유효하지 않은 회원",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "해당 회원 status로 실행할 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-002",
                                                                "customMessage": "해당 회원 status로 실행할 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<String> changeNickname(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UpdateNicknameDto updateNicknameDto);

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 삭제 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "returnCode": "MEMBER-SUCCESS-003",
                                                        "returnMessage": "회원 삭제 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "유효하지 않은 회원",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "해당 회원 status로 실행할 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-002",
                                                                "customMessage": "해당 회원 status로 실행할 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<String> deleteMember(@AuthenticationPrincipal UserPrincipal principal);

    @Operation(summary = "회원 프로필 수정", description = "회원 프로필 사진을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 프로필 사진 변경 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "customCode": "MEMBER-SUCCESS-006",
                                                         "customMessage": "회원 프로필 사진 변경 성공",
                                                         "status": true,
                                                         "data": null
                                                     }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "유효하지 않은 회원",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "해당 회원 status로 실행할 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-002",
                                                                "customMessage": "해당 회원 status로 실행할 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<String> changeProfileImage(@AuthenticationPrincipal UserPrincipal principal, @RequestPart(value = "profileimg") MultipartFile profileImage);
}
