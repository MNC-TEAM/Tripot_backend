package com.junior.controller.api;

import com.junior.dto.notice.CreateNoticeDto;
import com.junior.dto.notice.NoticeAdminDto;
import com.junior.dto.notice.NoticeDetailDto;
import com.junior.dto.notice.UpdateNoticeDto;
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

@Tag(name = "Notice Admin")
@Validated
public interface NoticeAdminApi {

    @Operation(summary = "공지사항 업로드", description = "공지사항을 업로드합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "공지사항 업로드 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "NOTICE-SUCCESS-001",
                                                        "customMessage": "공지사항 업로드 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    )))
            })
    public CommonResponse<Object> saveNotice(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CreateNoticeDto createNoticeDto);


    @Operation(summary = "공지사항 조회", description = "공지사항을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 정보",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "customCode": "NOTICE-SUCCESS-004",
                                                         "customMessage": "공지사항 조회 성공",
                                                         "status": true,
                                                         "data": {
                                                             "content": [
                                                                 {
                                                                     "id": 3,
                                                                     "title": "타이틀"
                                                                 },
                                                                 {
                                                                     "id": 2,
                                                                     "title": "타이틀"
                                                                 },
                                                                 {
                                                                     "id": 1,
                                                                     "title": "타이틀"
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
    public CommonResponse<PageCustom<NoticeAdminDto>> findNotice(@PageableDefault(size = 15) Pageable pageable,
                                                                 @RequestParam(required = false, value = "q") String q);


    @Operation(summary = "공지사항 세부 조회", description = "공지사항 세부 내용을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 세부 조회",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "NOTICE-SUCCESS-005",
                                                        "customMessage": "공지사항 세부정보 조회 성공",
                                                        "status": true,
                                                        "data": {
                                                            "id": 1
                                                            "title": "타이틀",
                                                            "content": "내용",
                                                        }
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 공지사항을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "NOTICE-ERR-001",
                                                                "customMessage": "해당 공지사항을 찾을 수 없음",
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
    public CommonResponse<NoticeDetailDto> findNoticeDetail(@PathVariable("notice_id") Long noticeId);


    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 수정 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "NOTICE-SUCCESS-003",
                                                        "customMessage": "공지사항 수정 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 공지사항을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "NOTICE-ERR-001",
                                                                "customMessage": "해당 공지사항을 찾을 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<Object> updateNotice(@PathVariable(name = "notice_id") Long noticeId, @Valid @RequestBody UpdateNoticeDto updateNoticeDto);

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 삭제 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "NOTICE-SUCCESS-002",
                                                        "customMessage": "공지사항 삭제 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 공지사항을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "customCode": "NOTICE-ERR-001",
                                                                "customMessage": "해당 공지사항을 찾을 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public CommonResponse<Object> deleteNotice(@PathVariable(name = "notice_id") Long noticeId);
}
