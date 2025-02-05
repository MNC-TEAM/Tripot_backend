package com.junior.controller.report;

import com.junior.controller.BaseControllerTest;
import com.junior.domain.report.ReportReason;
import com.junior.domain.report.ReportStatus;
import com.junior.domain.report.ReportType;
import com.junior.dto.report.CreateReportDto;
import com.junior.dto.report.ReportDto;
import com.junior.dto.report.StoryReportDto;
import com.junior.page.PageCustom;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import com.junior.service.report.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
public class ReportControllerTest extends BaseControllerTest {

    @MockBean
    ReportService reportService;


    @Test
    @DisplayName("신고 - 응답이 반환되어야 함")
    @WithMockCustomUser
    void saveReport() throws Exception {

        //given
        CreateReportDto createReportDto = new CreateReportDto(1L, "STORY", "스팸홍보");

        String content = objectMapper.writeValueAsString(createReportDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/reports")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customCode").value("REPORT-SUCCESS-001"))
                .andExpect(jsonPath("$.customMessage").value("신고 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("신고 조회 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void findReport() throws Exception {

        //given
        Pageable resultPageable = PageRequest.of(0, 15);
        String q = "CONFIRMED";

        List<ReportDto> result = new ArrayList<>();

        result.add(StoryReportDto.builder()
                .id(1L)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET.getName())
                .reporterUsername("username")
                .reportStatus(ReportStatus.CONFIRMED)
                .build());


        given(reportService.findReport(anyString(), any(Pageable.class))).willReturn(new PageCustom<>(result, resultPageable, result.size()));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/reports")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("REPORT-SUCCESS-004"))
                .andExpect(jsonPath("$.customMessage").value("신고 조회 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].reportReason").value("스팸홍보"));


    }

    @Test
    @DisplayName("신고 확인 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void confirmReport() throws Exception {

        //given
        Long reportId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/confirm", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("REPORT-SUCCESS-002"))
                .andExpect(jsonPath("$.customMessage").value("신고 처리(미삭제) 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("신고 대상 삭제 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void deleteReportTarget() throws Exception {

        //given
        Long reportId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/delete-target", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("REPORT-SUCCESS-003"))
                .andExpect(jsonPath("$.customMessage").value("신고 처리(삭제) 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
