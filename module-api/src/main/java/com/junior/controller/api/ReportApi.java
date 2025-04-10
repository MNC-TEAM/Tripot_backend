package com.junior.controller.api;

import com.junior.dto.report.CreateReportDto;
import com.junior.dto.report.ReportDto;
import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Report")
public interface ReportApi {


    @Operation(summary = "신고내역 업로드", description = "게시글 및 댓글을 신고합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "신고 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "REPORT-SUCCESS-001",
                                                        "customMessage": "신고 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "202", description = "본인 글은 신고할 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(name = "본인 글은 신고할 수 없음",
                                            value = """
                                                    {
                                                        "customCode": "REPORT-ERR-004",
                                                        "customMessage": "본인 글은 신고할 수 없음",
                                                        "status": false,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효한 신고 유형이 아님",
                                                    value = """
                                                            {
                                                                "customCode": "REPORT-ERR-001",
                                                                "customMessage": "유효한 신고 유형이 아님",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "중복신고할 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "REPORT-ERR-005",
                                                                "customMessage": "중복신고할 수 없음",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "스토리를 찾을 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "STORY-ERR-0001",
                                                                "customMessage": "스토리 불러오기 실패",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(name = "댓글을 찾을 수 없음",
                                                    value = """
                                                            {
                                                                "customCode": "COMMENT-ERR-0001",
                                                                "customMessage": "댓글 불러오기 실패",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            ),
                                    })),
                    @ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효하지 않은 회원",
                                                    value = """
                                                            {
                                                                "customCode": "MEMBER-ERR-001",
                                                                "customMessage": "유효하지 않은 회원",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))


            })
    public ResponseEntity<CommonResponse<Object>> saveReport(@AuthenticationPrincipal UserPrincipal principal,
                                                             @RequestBody CreateReportDto createReportDto);

    @Operation(summary = "신고 조회", description = "신고 내역을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "신고내역",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                          "customCode": "REPORT-SUCCESS-004",
                                                          "customMessage": "신고 조회 성공",
                                                          "status": true,
                                                          "data": {
                                                              "content": [
                                                                  {
                                                                      "id": 4,
                                                                      "reporterUsername": "USER",
                                                                      "reportType": "STORY",
                                                                      "reportedTime": "2024-12-27T02:52:14.545327",
                                                                      "reportStatus": "UNCONFIRMED",
                                                                      "reportReason": "스팸홍보",
                                                                      "title": "updateTitle1"
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
                                                                  "last": true,
                                                                  "hasNext": false,
                                                                  "totalPages": 1,
                                                                  "totalElements": 1,
                                                                  "numberOfElements": 1,
                                                                  "empty": false
                                                              }
                                                          }
                                                      }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = {
                                            @ExampleObject(name = "유효한 신고 타입이 아님",
                                                    value = """
                                                            {
                                                                "customCode": "REPORT-ERR-003",
                                                                "customMessage": "유효한 신고 타입이 아님",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                            )
                                    }))
            })
    public <T extends ReportDto> ResponseEntity<CommonResponse<PageCustom<T>>> findReport(@PageableDefault(size = 15, page = 1) Pageable pageable,
                                                                                          @RequestParam(name = "report_type", defaultValue = "ALL") String reportStatus);

    @Operation(summary = "신고 스토리 상세 정보 조회", description = "신고 스토리 상세 정보를 조회합니다. 해당 신고 내역을 처리(미삭제) 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 스토리 상세 정보 조회"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                            examples = {
                                    @ExampleObject(name = "유효한 신고 타입이 아님",
                                            value = """
                                                            {
                                                                "customCode": "REPORT-ERR-003",
                                                                "customMessage": "유효한 신고 타입이 아님",
                                                                "status": false,
                                                                "data": null
                                                            }
                                                            """
                                    )
                            }))
    })
    public <T extends ReportDto> ResponseEntity<CommonResponse<AdminStoryDetailDto>> findReportTargetStoryDetail(@PathVariable(value = "report_id") Long reportId);

    @Operation(summary = "신고내역 처리", description = "신고 내역을 삭제하지 않고 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "신고 처리 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "REPORT-SUCCESS-002",
                                                        "customMessage": "신고 처리(미삭제) 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 신고내역을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "REPORT-ERR-002",
                                                        "customMessage": "해당 신고내역을 찾을 수 없음",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    )))
            })
    public ResponseEntity<CommonResponse<Object>> confirmReport(@PathVariable("report_id") Long reportId);

    @Operation(summary = "신고내역 처리", description = "신고 내역 대상 글을 삭제합니다..",
            responses = {
                    @ApiResponse(responseCode = "200", description = "신고 처리(해당글 삭제) 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "REPORT-SUCCESS-003",
                                                        "customMessage": "신고 처리(삭제) 성공",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "해당 신고내역을 찾을 수 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "customCode": "REPORT-ERR-002",
                                                        "customMessage": "해당 신고내역을 찾을 수 없음",
                                                        "status": true,
                                                        "data": null
                                                    }
                                                    """
                                    )))
            })
    public ResponseEntity<CommonResponse<Object>> deleteReportTarget(@PathVariable("report_id") Long reportId);
}
