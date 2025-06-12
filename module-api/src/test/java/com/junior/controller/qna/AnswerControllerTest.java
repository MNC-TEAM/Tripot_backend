package com.junior.controller.qna;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.qna.*;
import com.junior.exception.StatusCode;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import com.junior.service.qna.AnswerService;
import com.junior.service.qna.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnswerController.class)
class AnswerControllerTest extends BaseControllerTest {

    @MockBean
    AnswerService answerService;



    @Test
    @DisplayName("문의 답글 등록 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void saveAnswer() throws Exception {


        //given
        CreateAnswerRequest createAnswerRequest = CreateAnswerRequest.builder()
                .title("title")
                .content("answer")
                .build();

        String content = objectMapper.writeValueAsString(createAnswerRequest);

        Long questionId = 1L;



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
    }

    @Test
    @DisplayName("문의 답글 수정 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void updateAnswer() throws Exception {


        //given
        UpdateAnswerRequest updateAnswerRequest = UpdateAnswerRequest.builder()
                .title("title")
                .content("answer")
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
    }

    @Test
    @DisplayName("문의 답글 삭제 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void deleteAnswer() throws Exception {


        //given
        Long questionId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
               delete("/api/v1/questions/{question_id}/answers", questionId)
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
    }



}