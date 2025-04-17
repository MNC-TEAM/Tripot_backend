package com.junior.controller.api;

import com.junior.dto.qna.CreateQnaDto;
import com.junior.dto.qna.QnaAdminDto;
import com.junior.dto.qna.QnaDetailDto;
import com.junior.dto.qna.UpdateQnaDto;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Q&A Admin")
@Validated
public interface QnaAdminApi {

    @Operation(summary = "Q&A 업로드", description = "Q&A을 업로드합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Q&A 업로드 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "Q&A-SUCCESS-001",
                                                        "customMessage": "Q&A 업로드 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    )))
            })
    public CommonResponse<Object> saveQna(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CreateQnaDto createQnaDto);


    @Operation(summary = "Q&A 조회", description = "Q&A를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Q&A 정보",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "customCode": "Q&A-SUCCESS-004",
                                                         "customMessage": "Q&A 조회 성공",
                                                         "status": true,
                                                         "data": {
                                                             "content": [
                                                                 {
                                                                     "id": 3,
                                                                     "question": "질문"
                                                                 },
                                                                 {
                                                                     "id": 2,
                                                                     "question": "질문"
                                                                 },
                                                                 {
                                                                     "id": 1,
                                                                     "question": "질문"
                                                                 }
                                                             ],
                                                             "pageable": {
                                                                 "number": 3,
                                                                 "size": 15,
                                                                 "sort": {
                                                                     "empty": true,
                                                                     "unsorted": true,
                                                                     "sorted": false
                                                                 },
                                                                 "first": false,
                                                                 "last": true,
                                                                 "hasNext": false,
                                                                 "totalPages": 3,
                                                                 "totalElements": 33,
                                                                 "numberOfElements": 3,
                                                                 "empty": false
                                                             }
                                                         }
                                                     }
                                                    """
                                    )))
            })
    public CommonResponse<PageCustom<QnaAdminDto>> findQna(@PageableDefault(size = 15, page = 1) Pageable pageable,
                                                           @RequestParam(required = false, value = "q") String q);


    @Operation(summary = "Q&A 세부 조회", description = "Q&A 세부 내용을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Q&A 세부 조회",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "Q&A-SUCCESS-005",
                                                        "customMessage": "Q&A 세부정보 조회 성공",
                                                        "status": true,
                                                        "data": {
                                                            "id": 1
                                                            "question": "질문",
                                                            "answer": "답변",
                                                        }
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 Q&A을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "Q&A-ERR-001",
                                                                "customMessage": "해당 Q&A을 찾을 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    })),
                    @ApiResponse(responseCode = "404", description = "해당 회원을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-003",
                                                                "customMessage": "해당 회원을 찾을 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<QnaDetailDto> findQnaDetail(@PathVariable("qna_id") Long qnaId);


    @Operation(summary = "Q&A 수정", description = "Q&A을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Q&A 수정 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "Q&A-SUCCESS-003",
                                                        "customMessage": "Q&A 수정 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 Q&A을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "Q&A-ERR-001",
                                                                "customMessage": "해당 Q&A을 찾을 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<Object> updateQna(@PathVariable(name = "qna_id") Long qnaId, @Valid @RequestBody UpdateQnaDto updateQnaDto);

    @Operation(summary = "Q&A 삭제", description = "Q&A을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Q&A 삭제 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "Q&A-SUCCESS-002",
                                                        "customMessage": "Q&A 삭제 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 Q&A을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "Q&A-ERR-001",
                                                                "customMessage": "해당 Q&A을 찾을 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<Object> deleteQna(@PathVariable(name = "qna_id") Long qnaId);
}
