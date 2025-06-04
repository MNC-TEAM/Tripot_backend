package com.junior.integration.qna;

import com.amazonaws.services.s3.AmazonS3Client;
import com.junior.domain.member.Member;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.CreateQuestionRequest;
import com.junior.dto.qna.UpdateQuestionRequest;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.qna.QuestionRepository;
import com.junior.security.WithMockCustomUser;
import org.assertj.core.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;


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

        for (int i = 1; i <= 20; i++) {
            Question question = Question.builder()
                    .title("title")
                    .content("question")
                    .imgUrl("https://aws.com/newQuestionImg")
                    .member(i % 2 == 1 ? activeTestMember : testAdmin)
                    .build();

            questionRepository.save(question);
        }
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

    @Test
    @DisplayName("문의글 등록 - 문의글이 정상적으로 저장되어야 함")
    @WithMockCustomUser
    void saveQuestion() throws Exception {


        //given
        CreateQuestionRequest createQuestionRequest = CreateQuestionRequest.builder()
                .title("new title")
                .content("new question")
                .imgUrl("https://aws.com/newQuestionImg")
                .build();

        String content = objectMapper.writeValueAsString(createQuestionRequest);



        //when
        ResultActions actions = mockMvc.perform(
                multipart(HttpMethod.POST, "/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //문의글이 정상적으로 저장되어야 함
        Question question = questionRepository.findById(21L).orElseThrow(RuntimeException::new);

        assertThat(question.getTitle()).isEqualTo("new title");
        assertThat(question.getContent()).isEqualTo("new question");
        assertThat(question.getImgUrl()).isEqualTo("https://aws.com/newQuestionImg");
        assertThat(question.getIsDeleted()).isFalse();

    }

    @Test
    @DisplayName("문의글 수정 - 문의글이 정상적으로 수정되어야 함")
    @WithMockCustomUser
    void updateQuestion() throws Exception {


        //given
        Long questionId = 1L;
        UpdateQuestionRequest updateQuestionRequest = UpdateQuestionRequest.builder()
                .title("new title")
                .content("new question")
                .imgUrl("s3.com/question-img")
                .build();

        String content = objectMapper.writeValueAsString(updateQuestionRequest);

        //when
        ResultActions actions = mockMvc.perform(
                multipart(HttpMethod.PATCH, "/api/v1/questions/{question_id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_UPDATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_UPDATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //문의글이 정상적으로 수정되어야 함
        Question question = questionRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(question.getTitle()).isEqualTo("new title");
        assertThat(question.getContent()).isEqualTo("new question");
        assertThat(question.getImgUrl()).isEqualTo("s3.com/question-img");
    }

    @Test
    @DisplayName("문의글 삭제 - 문의글이 정상적으로 삭제되어야 함")
    @WithMockCustomUser
    void deleteQuestion() throws Exception {


        //given
        Long questionId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                multipart(HttpMethod.DELETE, "/api/v1/questions/{question_id}", questionId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //문의글이 정상적으로 삭제되어야 함
        Question question = questionRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(question.getIsDeleted()).isTrue();

    }


}
