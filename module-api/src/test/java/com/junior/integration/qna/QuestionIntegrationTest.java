package com.junior.integration.qna;

import com.amazonaws.services.s3.AmazonS3Client;
import com.junior.controller.member.MemberController;
import com.junior.domain.member.Member;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomUser;
import com.junior.service.qna.CreateQuestionImgRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionIntegrationTest extends BaseIntegrationTest {


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


        given(amazonS3Client.getUrl(any(), any())).willReturn(new URL("https://aws.com/newQuestionImg"));
    }

    @Test
    @DisplayName("문의용 이미지 업로드 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void uploadQuestionImg() throws Exception {


        //given
        MockMultipartFile questionImg = createMockQuestionImg();

        String resultUrl = "https://aws.com/newQuestionImg";

        //when
        ResultActions actions = mockMvc.perform(
                multipart(HttpMethod.POST, "/api/v1/questions/imgs")
                        .file(questionImg)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(resultUrl));
    }


}
