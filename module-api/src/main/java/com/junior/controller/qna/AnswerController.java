package com.junior.controller.qna;

import com.junior.dto.qna.CreateAnswerRequest;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.qna.AnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnswerController {

    private final AnswerService answerService;

    @Secured("ADMIN")
    @PostMapping("/api/v1/questions/{question_id}/answers")
    public ResponseEntity<CommonResponse<Object>> save(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable(name = "question_id") Long questionId,
                                                       @RequestBody CreateAnswerRequest createAnswerRequest) {

        answerService.save(principal, questionId, createAnswerRequest);

        return ResponseEntity.status(StatusCode.ANSWER_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.ANSWER_CREATE_SUCCESS, null));
    }

}
