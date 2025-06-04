package com.junior.controller.qna;

import com.junior.controller.BaseControllerTest;
import com.junior.domain.member.Member;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.CreateQuestionRequest;
import com.junior.dto.qna.UpdateQuestionRequest;
import com.junior.exception.StatusCode;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomUser;
import com.junior.dto.qna.CreateQuestionImgRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest extends BaseControllerTest {

    @MockBean
    QuestionService questionService;


    @Test
    @DisplayName("문의용 이미지 업로드 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void uploadQuestionImg() throws Exception {


        //given
        MockMultipartFile questionImg = createMockMultipartFile("questionImg");

        String resultUrl = "s3.com/newQuestionImg";
        given(questionService.uploadQuestionImg(any(UserPrincipal.class), any(MultipartFile.class), any(CreateQuestionImgRequest.class))).willReturn(resultUrl);

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
    @DisplayName("문의글 등록 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void saveQuestion() throws Exception {


        //given
        CreateQuestionRequest createQuestionRequest = CreateQuestionRequest.builder()
                .title("title")
                .content("question")
                .imgUrl("s3.com/question-img")
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
    }

    MockMultipartFile createMockMultipartFile(String name) {
        MockMultipartFile questionImg = new MockMultipartFile(
                name,
                "question_img.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );

        return questionImg;

    }

    @Test
    @DisplayName("문의글 수정 - 응답이 정상적으로 반환되어야 함")
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
    }

    @Test
    @DisplayName("문의글 삭제 - 응답이 정상적으로 반환되어야 함")
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
    }


}