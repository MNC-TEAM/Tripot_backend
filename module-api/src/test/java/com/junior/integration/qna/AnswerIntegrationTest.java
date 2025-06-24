package com.junior.integration.qna;

import com.amazonaws.services.s3.AmazonS3Client;
import com.junior.domain.member.Member;
import com.junior.domain.qna.Answer;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.CreateAnswerRequest;
import com.junior.dto.qna.UpdateAnswerRequest;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.qna.AnswerRepository;
import com.junior.repository.qna.QuestionRepository;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

public class AnswerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    //aws 테스트는 요금이 발생할 수 있으므로 해당 객체를 mock 처리
    @MockBean
    private AmazonS3Client amazonS3Client;


    @BeforeEach
    void init() throws MalformedURLException {
        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();
        Member activeTestMember2 = createActiveTestMember2();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);


        given(amazonS3Client.getUrl(any(), any())).willReturn(new URL("https://aws.com/newQuestionImg"));

        /**
         * 21번 질문은 답변이 달려있지 않은 상태
         */
        for (int i = 1; i <= 21; i++) {
            Answer answer = Answer.builder()
                    .title("title " + i)
                    .content("answer")
                    .member(testAdmin)
                    .build();

            if (i != 21) {
                answerRepository.save(answer);
            }


            Question question = Question.builder()
                    .title("title" + i)
                    .content("question")
                    .imgUrl("https://aws.com/newQuestionImg")
                    .member(i % 2 == 1 ? activeTestMember : activeTestMember2)
                    .answer(i != 21 ? answer : null)
                    .build();

            questionRepository.save(question);

        }

    }


    @Test
    @DisplayName("답글 등록 - 답글이 정상적으로 저장되어야 함")
    @WithMockCustomAdmin
    void saveAnswer() throws Exception {


        //given
        CreateAnswerRequest createAnswerRequest = CreateAnswerRequest.builder()
                .title("new title")
                .content("new answer")
                .build();

        String content = objectMapper.writeValueAsString(createAnswerRequest);

        Long questionId = 21L;

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/questions/{question_id}/answers", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.ANSWER_CREATE_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.ANSWER_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ANSWER_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //문의글이 정상적으로 저장되어야 함
        Question question = questionRepository.findById(21L).orElseThrow(RuntimeException::new);
        Answer answer = answerRepository.findById(21L).orElseThrow(RuntimeException::new);

        assertThat(answer.getTitle()).isEqualTo("new title");
        assertThat(answer.getContent()).isEqualTo("new answer");
    }

    @Test
    @DisplayName("답글 조회 - 답글이 정상적으로 조회되어야 함")
    @WithMockCustomAdmin
    void findAnswer() throws Exception {


        //given

        //activeTestmember1이 주인
        Long questionId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/questions/{question_id}/answers", questionId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.ANSWER_FIND_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.ANSWER_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ANSWER_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("title 1"))
                .andExpect(jsonPath("$.data.content").value("answer"));

    }

    @Test
    @DisplayName("답글 조회 - 타인이 조회할 경우 예외를 발생시켜야 함")
    @WithMockCustomUser2
    void failToFindAnswerIfQuestionIsNotYours() throws Exception {
        //given

        //activeTestmember1이 주인
        Long questionId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/questions/{question_id}/answers", questionId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.ANSWER_FORBIDDEN.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.ANSWER_FORBIDDEN.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ANSWER_FORBIDDEN.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

    }

    @Test
    @DisplayName("문의 답글 수정 - 답글이 정상적으로 수정되어야 함")
    @WithMockCustomAdmin
    void updateAnswer() throws Exception {


        //given
        UpdateAnswerRequest updateAnswerRequest = UpdateAnswerRequest.builder()
                .title("new title")
                .content("new answer")
                .build();

        String content = objectMapper.writeValueAsString(updateAnswerRequest);

        Long answerId = 1L;


        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/answers/{answer_id}", answerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.ANSWER_UPDATE_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.ANSWER_UPDATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ANSWER_UPDATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Answer answer = answerRepository.findById(answerId).orElseThrow(RuntimeException::new);

        assertThat(answer.getTitle()).isEqualTo("new title");
        assertThat(answer.getContent()).isEqualTo("new answer");
    }

    @Test
    @DisplayName("문의 답글 삭제 - 답글이 정상적으로 삭제되어야 함")
    @WithMockCustomAdmin
    void deleteAnswer() throws Exception {


        //given

        Long questionId = 1L;


        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/questions/{question_id}/answers", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.ANSWER_DELETE_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.ANSWER_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ANSWER_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Question question = questionRepository.findById(questionId).orElseThrow(RuntimeException::new);
        Answer answer = answerRepository.findById(questionId).orElseThrow(RuntimeException::new);

        assertThat(answer.getIsDeleted()).isTrue();
        assertThat(question.getAnswer()).isNull();
    }


}
