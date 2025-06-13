package com.junior.controller.qna;

import com.junior.dto.qna.CreateQuestionRequest;
import com.junior.dto.qna.QuestionDetailResponse;
import com.junior.dto.qna.UpdateQuestionRequest;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.dto.qna.CreateQuestionImgRequest;
import com.junior.service.qna.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;


    @PostMapping("/api/v1/questions")
    public ResponseEntity<CommonResponse<Void>> save(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateQuestionRequest createQuestionRequest
    ) {
        questionService.save(principal, createQuestionRequest);
        return ResponseEntity.status(StatusCode.QUESTION_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.QUESTION_CREATE_SUCCESS, null));
    }

    @PostMapping(value = "/api/v1/questions/imgs")
    public ResponseEntity<CommonResponse<String>> uploadOrUpdateQuestionImg(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart(name = "questionImg") MultipartFile questionImg,
            @ModelAttribute CreateQuestionImgRequest createQuestionImgRequest) {

        String url = questionService.uploadQuestionImg(principal, questionImg, createQuestionImgRequest);

        return ResponseEntity.status(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS, url));
    }

    @GetMapping("/api/v1/questions/{question_id}")
    public ResponseEntity<CommonResponse<QuestionDetailResponse>> findDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable(name = "question_id") Long questionId
    ) {

        return ResponseEntity.status(StatusCode.QUESTION_DETAIL_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.QUESTION_DETAIL_FIND_SUCCESS, questionService.findDetail(principal, questionId)));
    }

    @PatchMapping("/api/v1/questions/{question_id}")
    public ResponseEntity<CommonResponse<Void>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable(name = "question_id") Long questionId,
            @RequestBody UpdateQuestionRequest updateQuestionRequest
            ) {
        questionService.update(principal, questionId, updateQuestionRequest);

        return ResponseEntity.status(StatusCode.QUESTION_UPDATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.QUESTION_UPDATE_SUCCESS, null));
    }

    @DeleteMapping("/api/v1/questions/{question_id}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable(name = "question_id") Long questionId
    ) {
        questionService.delete(principal, questionId);

        return ResponseEntity.status(StatusCode.QUESTION_DELETE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.QUESTION_DELETE_SUCCESS, null));
    }

}


