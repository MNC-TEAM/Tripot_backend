package com.junior.controller.qna;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.qna.*;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import com.junior.service.qna.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest extends BaseControllerTest {

    @MockBean
    QuestionService questionService;


    @Test
    @DisplayName("문의용 이미지 업로드 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void uploadOrUpdateQuestionImg() throws Exception {


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
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS.getHttpCode()))
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
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_CREATE_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("문의글 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void find() throws Exception {
        //given
        int size = 5;

        PageRequest pageRequest = PageRequest.of(0, size);

        List<QuestionResponse> responses = new ArrayList<>();

        QuestionResponse response = QuestionResponse.builder()
                .id(1L)
                .title("title")
                .content("question")
                .isAnswered(false)
                .build();

        responses.add(response);

        given(questionService.find(any(UserPrincipal.class), anyLong(), anyInt())).willReturn(new SliceImpl<>(responses, pageRequest, false));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/questions")
                        .queryParam("size", "5")
                        .queryParam("cursorId", "5")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_FIND_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("title"))
                .andExpect(jsonPath("$.data.content[0].content").value("question"));

    }

    @Test
    @DisplayName("문의글 관리자 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findQuestionAdmin() throws Exception {
        //given

        int pageNumber = 1;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(pageNumber, size);

        List<QuestionAdminResponse> responses = new ArrayList<>();

        QuestionAdminResponse response = QuestionAdminResponse.builder()
                .id(1L)
                .title("title")
                .content("question")
                .isAnswered(false)
                .isDeleted(false)
                .build();

        responses.add(response);

        given(questionService.findQuestionAdmin(any(Pageable.class))).willReturn(new PageCustom<>(responses, pageRequest, 1));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/questions")
                        .queryParam("page", "1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_FIND_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("title"))
                .andExpect(jsonPath("$.data.content[0].content").value("question"));

    }

    @Test
    @DisplayName("문의글 상세조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findQuestionDetail() throws Exception {
        //given
        Long questionId = 1L;

        QuestionDetailResponse response = QuestionDetailResponse.builder()
                .id(questionId)
                .title("title")
                .content("question")
                .imgUrl("imgurl.com")
                .build();

        given(questionService.findDetail(any(UserPrincipal.class), anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/questions/{question_id}", questionId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_DETAIL_FIND_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_DETAIL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_DETAIL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.content").value("question"))
                .andExpect(jsonPath("$.data.imgUrl").value("imgurl.com"));

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
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_UPDATE_SUCCESS.getHttpCode()))
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
                .andDo(print())
                .andExpect(status().is(StatusCode.QUESTION_DELETE_SUCCESS.getHttpCode()))
                .andExpect(jsonPath("$.customCode").value(StatusCode.QUESTION_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.QUESTION_DELETE_SUCCESS.getCustomMessage()))
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


}