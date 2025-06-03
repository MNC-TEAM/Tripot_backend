package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import com.junior.service.s3.S3Service;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class QuestionServiceTest extends BaseServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    S3Service s3Service;

    @InjectMocks
    QuestionService questionService;



    @Test
    @DisplayName("문의용 이미지 업로드 - 이미지 업로드 성공 후 해당 주소를 리턴해야 함")
    void uploadQuestionImage() {

        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        MultipartFile newQuestionImg = createMockMultipartFile();
        CreateQuestionImgRequest createQuestionImgRequest = new CreateQuestionImgRequest("");

        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testMember));
        given(s3Service.saveQuestionImage(newQuestionImg)).willReturn("s3.com/newQuestionImg");

        //when
        String newUrl = questionService.uploadQuestionImg(principal, newQuestionImg, createQuestionImgRequest);

        //then
        assertThat(newUrl).isEqualTo("s3.com/newQuestionImg");

    }

    @Test
    @DisplayName("문의용 이미지 업로드 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToUploadImageIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        MultipartFile profileImage = createMockMultipartFile();
        CreateQuestionImgRequest createQuestionImgRequest = new CreateQuestionImgRequest("");


        //when, then
        assertThatThrownBy(() -> questionService.uploadQuestionImg(principal, profileImage, createQuestionImgRequest))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("문의용 이미지 업로드 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToUploadImageIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        CreateQuestionImgRequest createQuestionImgRequest = new CreateQuestionImgRequest("");
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));
        MultipartFile profileImage = createMockMultipartFile();


        //when, then
        assertThatThrownBy(() -> questionService.uploadQuestionImg(principal, profileImage, createQuestionImgRequest)).isInstanceOf(NotValidMemberException.class);


    }
}