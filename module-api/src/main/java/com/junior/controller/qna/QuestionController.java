package com.junior.controller.qna;

import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.qna.CreateQuestionImgRequest;
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



    @PostMapping(value = "/api/v1/questions/imgs", consumes = {})
    public CommonResponse<String> uploadQuestionImg(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart(name = "questionImg") MultipartFile questionImg,
            @ModelAttribute CreateQuestionImgRequest createQuestionImgRequest) {

        String url = questionService.uploadQuestionImg(principal, questionImg, createQuestionImgRequest);

        return CommonResponse.success(StatusCode.QUESTION_IMG_UPLOAD_SUCCESS, url);
    }
}


