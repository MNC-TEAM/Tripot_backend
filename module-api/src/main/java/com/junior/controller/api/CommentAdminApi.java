package com.junior.controller.api;

import com.junior.dto.comment.CommentAdminDto;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;

public interface CommentAdminApi {

    @Operation(summary = "관리자용 댓글 조회", description = "관리자 페이지에서 댓글 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                            "customCode": "COMMENT-SUCCESS-002",
                                                            "customMessage": "",
                                                            "status": true,
                                                            "data": {
                                                              "content": [
                                                                {
                                                                  "id": 19,
                                                                  "content": "content",
                                                                  "createdNickname": "탈퇴회원",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 18,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 17,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 16,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 15,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 14,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 13,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 12,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 11,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 10,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 9,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 8,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 7,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 6,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                },
                                                                {
                                                                  "id": 5,
                                                                  "content": "content",
                                                                  "createdNickname": "테스트사용자닉네임",
                                                                  "isDeleted": false
                                                                }
                                                              ],
                                                              "pageable": {
                                                                "number": 1,
                                                                "size": 15,
                                                                "sort": {
                                                                  "empty": true,
                                                                  "unsorted": true,
                                                                  "sorted": false
                                                                },
                                                                "first": true,
                                                                "last": false,
                                                                "hasNext": true,
                                                                "totalPages": 2,
                                                                "totalElements": 19,
                                                                "numberOfElements": 15,
                                                                "empty": false
                                                              }
                                                            }
                                                          }
                                                    """
                                    )))
            })
    public CommonResponse<PageCustom<CommentAdminDto>> findComment(@PageableDefault(size = 15, page = 1) Pageable pageable);

    @Operation(summary = "관리자용 댓글 삭제", description = "관리자 페이지에서 댓글을 삭제 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "customCode": "COMMENT-SUCCESS-0004",
                                                      "customMessage": "댓글 삭제 성공",
                                                      "status": true,
                                                      "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "댓글 찾기 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "댓글 불러오기 실패",
                                                    value = """
                                                            {
                                                              "customCode": "COMMENT-ERR-0001",
                                                              "customMessage": "댓글 불러오기 실패",
                                                              "status": false,
                                                              "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<PageCustom<CommentAdminDto>> deleteComment(@PathVariable(name = "comment_id") Long commentId);
}
