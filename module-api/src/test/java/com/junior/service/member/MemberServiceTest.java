package com.junior.service.member;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.dto.member.*;
import com.junior.dto.qna.QnaAdminDto;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import com.junior.service.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest extends BaseServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    S3Service s3Service;

    @InjectMocks
    MemberService memberService;


    @Test
    @DisplayName("회원 활성화 시 회원 정보 업데이트 및 ACTIVE 상태가 되어야 함")
    void memberIsActivate() {

        //given
        ActivateMemberDto activateMemberDto = new ActivateMemberDto("updatenick", "강원");

        //비활성화된 회원 생성 및 저장
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));


        //when
        memberService.activateMember(principal, activateMemberDto);

        //then

        Member modifiedMember = memberRepository.findById(1L).get();

        //닉네임이 updatenick으로 변경되어야 함
        assertThat(modifiedMember.getNickname()).isEqualTo("updatenick");
        //recommendLocation이 강원이어야 함
        assertThat(modifiedMember.getRecommendLocation()).isEqualTo("강원");
        //testMember의 status가 ACTIVE여야 함
        assertThat(modifiedMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("이미 ACTIVE 상태인 회원은 활성화를 할 수 없음")
    void failToActivateMemberIfAlreadyActivated() {


        //given
        ActivateMemberDto activateMemberDto = new ActivateMemberDto("updatenick", "강원");

        //비활성화된 회원 생성 및 저장
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> memberService.activateMember(principal, activateMemberDto)).isInstanceOf(NotValidMemberException.class);


    }

    @Test
    @DisplayName("회원 활성화 - 회원을 찾지 못했을 시의 예외 처리가 되어야 함")
    void failToActivateMemberIfNotFound() {

        //given
        ActivateMemberDto activateMemberDto = new ActivateMemberDto("updatenick", "강원");

        //비활성화된 회원 생성 및 저장
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);


        //when, then
        assertThatThrownBy(() -> memberService.activateMember(principal, activateMemberDto))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());

    }

    @Test
    @DisplayName("회원 활성화 상태 확인 - 비활성화 회원의 활성화 여부는 False를 반환해야 함")
    void falseIfCheckPreactivateMemberStatus() throws Exception {
        //given

        Member testPreactiveMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testPreactiveMember);
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testPreactiveMember));

        //when
        CheckActiveMemberDto checkActiveMemberDto = memberService.checkActiveMember(principal);

        //then
        assertThat(checkActiveMemberDto.nickname()).isEqualTo("테스트비활성화닉네임");
        assertThat(checkActiveMemberDto.isActivate()).isFalse();

    }

    @Test
    @DisplayName("회원 활성화 상태 확인 - 활성화 회원의 활성화 여부는 True를 반환해야 함")
    void trueIfCheckActivateMemberStatus() throws Exception {
        //given

        Member testActiveMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testActiveMember);
        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testActiveMember));

        //when
        CheckActiveMemberDto checkActiveMemberDto = memberService.checkActiveMember(principal);

        //then
        assertThat(checkActiveMemberDto.nickname()).isEqualTo("테스트사용자닉네임");
        assertThat(checkActiveMemberDto.isActivate()).isTrue();

    }

    @Test
    @DisplayName("회원 활성화 상태 확인 - 회원을 찾지 못했을 시의 예외 처리가 되어야 함")
    void failToCheckMemberStatusIfMemberNotFound() {

        //given
        ActivateMemberDto activateMemberDto = new ActivateMemberDto("updatenick", "강원");

        //비활성화된 회원 생성 및 저장
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);


        //when, then
        assertThatThrownBy(() -> memberService.checkActiveMember(principal)).isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());

    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복된 닉네임이라면 True, 아니면 False를 반환")
    void trueIfNicknameAlreadyExistsAndFalseIfNicknameCanBeUsed() {

        given(memberRepository.existsByNickname("테스트닉")).willReturn(true);          //테스트닉이라는 닉네임을 가진 사용자가 존재할 때

        assertThat(memberService.checkDuplicateNickname("테스트닉")).isTrue();
        assertThat(memberService.checkDuplicateNickname("테스트")).isFalse();

    }

    @Test
    @DisplayName("프로필 사진 변경 - 프로필 사진 변경이 정상적으로 이루어져야 함")
    void updateProfileImage() {

        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        MultipartFile profileImage = createMockMultipartFile();

        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testMember));
        given(s3Service.saveProfileImage(profileImage)).willReturn("s3.com/newProfile");

        //when
        memberService.updateProfileImage(principal, profileImage);

        //then
        Member updatedMember = memberRepository.findById(2L).get();
        assertThat(updatedMember.getProfileImage()).isEqualTo("s3.com/newProfile");
    }

    @Test
    @DisplayName("프로필 사진 변경 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToUpdateImageIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        MultipartFile profileImage = createMockMultipartFile();


        //when, then
        assertThatThrownBy(() -> memberService.updateProfileImage(principal, profileImage))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("프로필 사진 변경 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToUpdateImageIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));
        MultipartFile profileImage = createMockMultipartFile();


        //when, then
        assertThatThrownBy(() -> memberService.updateProfileImage(principal, profileImage)).isInstanceOf(NotValidMemberException.class);


    }

    @Test
    @DisplayName("회원정보 조회 - 해당 회원의 닉네임과 프로필 사진 url을 정상적으로 불러올 것")
    void getMemberInfo() {

        //given
        Member testMember = createActiveTestMember();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));
        UserPrincipal principal = new UserPrincipal(testMember);

        //when
        MemberInfoDto memberInfo = memberService.getMemberInfo(principal);

        //then
        assertThat(memberInfo.nickname()).isEqualTo("테스트사용자닉네임");
        assertThat(memberInfo.profileImageUrl()).isEqualTo("s3.com/testProfile");


    }

    @Test
    @DisplayName("회원 정보 조회 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToGetMemberInfoIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);


        //when, then
        assertThatThrownBy(() -> memberService.getMemberInfo(principal)).isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("회원 정보 조회 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToGetMemberInfoIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> memberService.getMemberInfo(principal))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER_STATUS.getCustomMessage());


    }

    @Test
    @DisplayName("닉네임 수정 - 회원의 닉네임 수정이 정상적으로 이루어져야 함")
    void updateNickname() {

        //given
        Member testMember = createActiveTestMember();

        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testMember));
        UserPrincipal principal = new UserPrincipal(testMember);

        UpdateNicknameDto updateNicknameDto = new UpdateNicknameDto("updatenick");

        //when
        memberService.updateNickname(principal, updateNicknameDto);

        //then
        Member updatedMember = memberRepository.findById(2L).get();

        assertThat(updatedMember.getNickname()).isEqualTo("updatenick");

    }

    @Test
    @DisplayName("닉네임 수정 - 회원을 찾을 수 없을 경우 예외 처리를 해야 함")
    void failToUpdateNicknameIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);


        UpdateNicknameDto updateNicknameDto = new UpdateNicknameDto("updatenick");


        //when, then
        assertThatThrownBy(() -> memberService.updateNickname(principal, updateNicknameDto))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("닉네임 수정 - ACTIVE 상태가 아닌 회원은 닉네임 수정을 할 수 없음")
    void failToUpdateNicknameIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));

        UpdateNicknameDto updateNicknameDto = new UpdateNicknameDto("updatenick");


        //when, then
        assertThatThrownBy(() -> memberService.updateNickname(principal, updateNicknameDto))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER_STATUS.getCustomMessage());
        ;


    }

    @Test
    @DisplayName("회원 탈퇴 - 삭제된 회원은 DELETE 상태가 되어야 함")
    void deleteMember() {

        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testMember));

        //when
        memberService.deleteMember(principal);

        //then

        Member deletedMember = memberRepository.findById(2L).get();

        assertThat(deletedMember.getStatus()).isEqualTo(MemberStatus.DELETE);

    }

    @Test
    @DisplayName("회원 탈퇴 - 회원을 찾지 못했을 시의 예외 처리가 되어야 함")
    void failToDeleteMemberIfMemberNotFound() {


        //given

        //비활성화된 회원 생성 및 저장
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);


        //when, then
        assertThatThrownBy(() -> memberService.deleteMember(principal)).isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("회원 탈퇴 - 활성화되지 않은 회원에 대한 탈퇴는 예외를 발생시킴")
    void failToDeleteMemberIfMemberIsNotActivate() {


        //given

        //비활성화된 회원 생성 및 저장
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> memberService.deleteMember(principal)).isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER_STATUS.getCustomMessage());


    }
    
    @Test
    @DisplayName("회원 리스트 조회 - 회원 리스트를 정상적으로 조회할 수 있어야 함")        
    public void findMembers() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1, 20);
        PageRequest pageableAfterFind = PageRequest.of(0, 20);
        String q = "";

        List<MemberListResponseDto> dtoList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            dtoList.add(
                    MemberListResponseDto.builder()
                            .id((long) i)
                            .signUpType(SignUpType.KAKAO)
                            .status(MemberStatus.ACTIVE)
                            .nickname("사용자 " + i)
                            .build()

            );
        }

        PageImpl<MemberListResponseDto> pageList = new PageImpl<>(dtoList, pageableAfterFind, 10);

        given(memberRepository.findMember(any(Pageable.class), anyString())).willReturn(pageList);

        //when
        PageCustom<MemberListResponseDto> result = memberService.findMembers(pageRequest, q);

        //then
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getPageable().getNumber()).isEqualTo(1);
        
    }


}