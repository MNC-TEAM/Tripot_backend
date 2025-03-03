package com.junior.controller.member;

import com.junior.controller.api.MemberApi;
import com.junior.dto.member.*;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.junior.exception.StatusCode.*;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberApi {

    private final MemberService memberService;

    /**
     * PREACTIVE 상태의 회원을 활성화시키는 기능
     * @param userPrincipal
     * @param activateMemberDto
     * @return 회원 활성화 완료
     */
    @PatchMapping("/api/v1/members/activate")
    public CommonResponse<String> activeMember(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ActivateMemberDto activateMemberDto) {
        memberService.activateMember(userPrincipal, activateMemberDto);

//        return CommonResponse.of(ACTIVATE_MEMBER.getCustomCode(), ACTIVATE_MEMBER.getCustomMessage(), null);
        return CommonResponse.success(ACTIVATE_MEMBER, null);
    }

    /**
     * 사용가능한 닉네임인지 확인하는 기능
     * @param nickname
     * @return true: valid한 닉네임
     * @return false: valid하지 않은 닉네임(중복 닉네임이 존재함)
     */
    @GetMapping("/api/v1/members/nicknames/check-valid")
    public CommonResponse<Boolean> checkNicknameValid(@RequestParam("nickname") String nickname) {

//        return CommonResponse.of(CHECK_NICKNAME_MEMBER.getCustomCode(), CHECK_NICKNAME_MEMBER.getCustomMessage(), !memberService.checkDuplicateNickname(nickname));
        return CommonResponse.success(CHECK_NICKNAME_MEMBER, !memberService.checkDuplicateNickname(nickname));
    }

    /**
     * 회원의 활성화 여부를 반환하는 기능
     * @param principal
     * @return nickname: 사용자의 닉네임
     *         isActivate: 해당 회원의 활성화 여부
     */
    @GetMapping("/api/v1/members/check-activate")
    public CommonResponse<CheckActiveMemberDto> checkActivateMember(@AuthenticationPrincipal UserPrincipal principal) {

        return CommonResponse.success(GET_MEMBER_ACTIVATE, memberService.checkActiveMember(principal));
    }

    /**
     *
     * @param principal
     * @return nickname: 사용자의 닉네임
     *         profileImageUrl: 사용자의 프로필 사진 s3 url 경로
     */
    @GetMapping("/api/v1/members")
    public CommonResponse<MemberInfoDto> getMemberInfo(@AuthenticationPrincipal UserPrincipal principal) {

        return CommonResponse.success(GET_MEMBER_INFO, memberService.getMemberInfo(principal));
    }

    /**
     * 회원 닉네임 변경 기능
     * @param principal
     * @param updateNicknameDto: 사용자의 닉네임
     * @return 회원 닉네임 변경 성공
     */
    @PatchMapping("/api/v1/members/nicknames")
    public CommonResponse<String> changeNickname(@AuthenticationPrincipal UserPrincipal principal, @RequestBody UpdateNicknameDto updateNicknameDto) {

        memberService.updateNickname(principal, updateNicknameDto);


        return CommonResponse.success(UPDATE_NICKNAME_MEMBER, null);
    }

    /**
     * 회원 탈퇴 기능
     * @param principal
     * @return 회원 탈퇴 완료
     */
    @DeleteMapping("/api/v1/members")
    public CommonResponse<String> deleteMember(@AuthenticationPrincipal UserPrincipal principal) {
        memberService.deleteMember(principal);

//        return CommonResponse.of(DELETE_MEMBER.getCustomCode(), DELETE_MEMBER.getCustomMessage(), null);
        return CommonResponse.success(DELETE_MEMBER, null);
    }

    /**
     * 회원 프로필 사진 변경 기능
     * @param principal
     * @param profileImage
     * @return 회원 프로필 사진 변경 성공
     */
    @PatchMapping("/api/v1/members/profile-images")
    public CommonResponse<String> changeProfileImage(@AuthenticationPrincipal UserPrincipal principal, @RequestPart(value = "profileimg") MultipartFile profileImage) {

        memberService.updateProfileImage(principal, profileImage);


        return CommonResponse.success(UPDATE_PROFILE_IMAGE_MEMBER, null);
    }

    /**
     * 관리자용 회원 정보 조회 기능
     * @param pageable
     * @param q
     * @return 회원 리스트
     */
    @GetMapping("/api/v1/admin/members")
    public CommonResponse<PageCustom<MemberListResponseDto>> findMembers(@PageableDefault(size = 20, page = 1) Pageable pageable, @RequestParam(value = "q", defaultValue = "") String q) {
        return CommonResponse.success(GET_MEMBERS, memberService.findMembers(pageable, q));
    }

}
