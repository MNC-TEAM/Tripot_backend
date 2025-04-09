package com.junior.controller.report;

import com.junior.controller.api.ReportApi;
import com.junior.dto.report.CreateReportDto;
import com.junior.dto.report.ReportDto;
import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @PostMapping("/api/v1/reports")
    public ResponseEntity<CommonResponse<Object>> saveReport(@AuthenticationPrincipal UserPrincipal principal,
                                                             @RequestBody CreateReportDto createReportDto) {

        reportService.save(createReportDto, principal);

        return ResponseEntity.status(StatusCode.REPORT_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.REPORT_CREATE_SUCCESS, null));

    }

    @GetMapping("/api/v1/admin/reports")
    public <T extends ReportDto> ResponseEntity<CommonResponse<PageCustom<T>>> findReport(@PageableDefault(size = 15, page = 1) Pageable pageable,
                                                                                          @RequestParam(name = "report_status", defaultValue = "ALL") String reportStatus) {

        return ResponseEntity.status(StatusCode.REPORT_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.REPORT_FIND_SUCCESS, reportService.findReport(reportStatus, pageable)));
    }

    /**
     * 신고 내역 대상 스토리 상세 조회
     * @param reportId 스토리 이름
     * @return 스토리 세부내역 조회
     * 해당 신고내역 확인 처리도 수행
     */
    @GetMapping("/api/v1/admin/reports/{report_id}/stories")
    public <T extends ReportDto> ResponseEntity<CommonResponse<AdminStoryDetailDto>> findReportTargetStoryDetail(@PathVariable(value = "report_id") Long reportId) {
        return ResponseEntity.status(StatusCode.REPORT_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.REPORT_FIND_SUCCESS, reportService.findReportDetail(reportId)));
    }

    @PatchMapping("/api/v1/admin/reports/{report_id}/confirm")
    public ResponseEntity<CommonResponse<Object>> confirmReport(@PathVariable("report_id") Long reportId) {
        reportService.confirmReport(reportId);

        return ResponseEntity.status(StatusCode.REPORT_CONFIRM_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.REPORT_CONFIRM_SUCCESS, null));
    }

    @PatchMapping("/api/v1/admin/reports/{report_id}/delete-target")
    public ResponseEntity<CommonResponse<Object>> deleteReportTarget(@PathVariable("report_id") Long reportId) {
        reportService.deleteReportTarget(reportId);
        return ResponseEntity.status(StatusCode.REPORT_DELETE_TARGET_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.REPORT_DELETE_TARGET_SUCCESS, null));
    }
}
