package com.junior.controller.qna;

import com.junior.dto.qna.AnswerResponse;
import com.junior.dto.qna.CreateAnswerRequest;
import com.junior.dto.qna.UpdateAnswerRequest;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.qna.AnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnswerController {

    private final AnswerService answerService;

    @Secured("ADMIN")
    @PostMapping("/api/v1/questions/{question_id}/answers")
    public ResponseEntity<CommonResponse<Void>> save(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable(name = "question_id") Long questionId,
                                                       @RequestBody CreateAnswerRequest createAnswerRequest) {

        answerService.save(principal, questionId, createAnswerRequest);

        return ResponseEntity.status(StatusCode.ANSWER_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.ANSWER_CREATE_SUCCESS, null));
    }

    @GetMapping("/api/v1/questions/{question_id}/answers")
    public ResponseEntity<CommonResponse<AnswerResponse>> find(@AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable(name = "question_id") Long questionId) {

        return ResponseEntity.status(StatusCode.ANSWER_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.ANSWER_FIND_SUCCESS, answerService.find(principal, questionId)));
    }

    @Secured("ADMIN")
    @PatchMapping("/api/v1/answers/{answer_id}")
    public ResponseEntity<CommonResponse<Void>> update(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable(name = "answer_id") Long answerId,
                                                       @RequestBody UpdateAnswerRequest updateAnswerRequest) {

        answerService.update(principal, answerId, updateAnswerRequest);

        return ResponseEntity.status(StatusCode.ANSWER_UPDATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.ANSWER_UPDATE_SUCCESS, null));
    }

    @Secured("ADMIN")
    @DeleteMapping("/api/v1/questions/{question_id}/answers")
    public ResponseEntity<CommonResponse<Void>> delete(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable(name = "question_id") Long questionId) {

        answerService.delete(principal, questionId);

        return ResponseEntity.status(StatusCode.ANSWER_DELETE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.ANSWER_DELETE_SUCCESS, null));
    }

}
