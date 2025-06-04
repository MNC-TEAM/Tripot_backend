package com.junior.controller.qna;

import com.junior.dto.qna.CreateQuestionRequest;
import com.junior.dto.qna.UpdateQuestionRequest;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.dto.qna.CreateQuestionImgRequest;
import com.junior.service.qna.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;


    @PostMapping("/api/v1/questions")
    public CommonResponse<Object> save(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateQuestionRequest createQuestionRequest
    ) {
        questionService.save(principal, createQuestionRequest);
        return CommonResponse.success(StatusCode.QUESTION_CREATE_SUCCESS, null);
    }

    @PostMapping(value = "/api/v1/questions/imgs")
    public CommonResponse<String> uploadQuestionImg(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart(name = "questionImg") MultipartFile questionImg,
            @ModelAttribute CreateQuestionImgRequest createQuestionImgRequest) {

        String url = questionService.uploadQuestionImg(principal, questionImg, createQuestionImgRequest);

        return CommonResponse.success(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS, url);
    }

    @PatchMapping("/api/v1/questions/{question_id}")
    public CommonResponse<Object> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable(name = "question_id") Long questionId,
            @RequestBody UpdateQuestionRequest updateQuestionRequest
            ) {
        questionService.update(principal, questionId, updateQuestionRequest);

        return CommonResponse.success(StatusCode.QUESTION_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/api/v1/questions/{question_id}")
    public CommonResponse<Object> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable(name = "question_id") Long questionId
    ) {
        questionService.delete(principal, questionId);

        return CommonResponse.success(StatusCode.QUESTION_DELETE_SUCCESS, null);
    }
}


