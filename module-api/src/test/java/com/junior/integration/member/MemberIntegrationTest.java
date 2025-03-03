package com.junior.integration.member;

import com.amazonaws.services.s3.AmazonS3Client;
import com.junior.controller.member.MemberController;
import com.junior.domain.member.Member;
import com.junior.domain.member.MemberStatus;
import com.junior.dto.member.ActivateMemberDto;
import com.junior.dto.member.UpdateNicknameDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomPreactiveUser;
import com.junior.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MemberIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MemberController memberController;

    @Autowired
    private MemberRepository memberRepository;


    //aws 테스트는 요금이 발생할 수 있으므로 해당 객체를 mock 처리
    @MockBean
    private AmazonS3Client amazonS3Client;


    @BeforeEach
    void init() throws MalformedURLException {
        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);


        given(amazonS3Client.getUrl(any(), any())).willReturn(new URL("https://aws.com/new-url"));
    }

    @Test
    @DisplayName("회원 활성화가 - 기능이 정상적으로 동작해야 함")
    @WithMockCustomPreactiveUser
    public void activeMember() throws Exception {
        //given
        ActivateMemberDto activateMemberDto = new ActivateMemberDto("updatenick", "강원");
        String content = objectMapper.writeValueAsString(activateMemberDto);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/members/activate")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.ACTIVATE_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ACTIVATE_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //회원 활성화가 정상 작동해야 함
        Member targetMember = memberRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(targetMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(targetMember.getNickname()).isEqualTo("updatenick");
        assertThat(targetMember.getRecommendLocation()).isEqualTo("강원");


    }

    @Test
    @DisplayName("닉네임 사용가능 여부 확인 - 결과를 정상적으로 반환해야 함")
    void checkNicknameValid() throws Exception {

        //given
        String trueNickname = "nickname";
        String falseNickname = "테스트사용자닉네임";


        //when
        ResultActions actionsTrue = mockMvc.perform(
                get("/api/v1/members/nicknames/check-valid")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", trueNickname)
        );

        //when
        ResultActions actionsFalse = mockMvc.perform(
                get("/api/v1/members/nicknames/check-valid")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", falseNickname)
        );

        //then
        actionsTrue
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.CHECK_NICKNAME_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.CHECK_NICKNAME_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(true));


        //then
        actionsFalse
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.CHECK_NICKNAME_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.CHECK_NICKNAME_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(false));

    }

    @Test
    @DisplayName("회원 활성화 여부 확인 - 비활성화 회원의 활성화 여부는 false가 반환되어야 함")
    @WithMockCustomPreactiveUser
    public void checkPreactiveMemberIsActivate() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/members/check-activate")
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBER_ACTIVATE.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBER_ACTIVATE.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("테스트비활성화닉네임"))
                .andExpect(jsonPath("$.data.isActivate").value(false));

    }

    @Test
    @DisplayName("회원 활성화 여부 확인 - 활성화 회원의 활성화 여부는 true가 반환되어야 함")
    @WithMockCustomUser
    public void checkActiveMemberIsActivate() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/members/check-activate")
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBER_ACTIVATE.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBER_ACTIVATE.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("테스트사용자닉네임"))
                .andExpect(jsonPath("$.data.isActivate").value(true));

    }

    @Test
    @DisplayName("회원 정보 조회 - 응답에 조회한 회원 정보가 정상적으로 들어가야 함")
    @WithMockCustomUser
    void getMemberInfo() throws Exception {

        //given


        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBER_INFO.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBER_INFO.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("테스트사용자닉네임"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("s3.com/testProfile"));


    }

    @Test
    @DisplayName("닉네임 변경 - 정상적으로 진행되어야 함")
    @WithMockCustomUser
    void changeNickname() throws Exception {
        //given
        UpdateNicknameDto updateNicknameDto = new UpdateNicknameDto("테스트수정닉네임");
        String content = objectMapper.writeValueAsString(updateNicknameDto);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/members/nicknames")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.UPDATE_NICKNAME_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.UPDATE_NICKNAME_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Member member = memberRepository.findById(2L).get();
        assertThat(member.getNickname()).isEqualTo("테스트수정닉네임");


    }

    @Test
    @WithMockCustomUser
    @DisplayName("프로필 사진 수정 - 응답이 정상적으로 반환되어야 함")
    void changeProfileImage() throws Exception {

        //given
        MockMultipartFile profileImg = createMockMultipartFile();

        //s3에 저장하는 과정이 생략되어야 함 -> AmazonS3Client mock 처리


        //when
        ResultActions actions = mockMvc.perform(
                multipart(HttpMethod.PATCH, "/api/v1/members/profile-images")
                        .file(profileImg)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.UPDATE_PROFILE_IMAGE_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.UPDATE_PROFILE_IMAGE_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //회원 정보에 변경된 url이 저장되어야 함
        Member member = memberRepository.findById(2L).get();
        assertThat(member.getProfileImage()).isEqualTo("https://aws.com/new-url");

    }

    @Test
    @DisplayName("회원 리스트 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    public void findMembers() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/members")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBERS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBERS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].nickname").value("테스트사용자닉네임"));

    }


}
