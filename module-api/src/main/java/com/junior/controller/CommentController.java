package com.junior.controller;

import com.junior.dto.comment.CreateCommentDto;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/save")
    public CommonResponse<Object> save(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateCommentDto createCommentDto
            ) {
        commentService.saveComment(userPrincipal, createCommentDto);

        return CommonResponse.success(StatusCode.COMMENT_CREATE_SUCCESS, null);
    }
}
