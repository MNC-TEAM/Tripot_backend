package com.junior.controller.version;

import com.junior.controller.BaseControllerTest;
import com.junior.domain.version.Platform;
import com.junior.dto.version.VersionCheckResponseDto;
import com.junior.dto.version.VersionDto;
import com.junior.exception.StatusCode;
import com.junior.security.WithMockCustomAdmin;
import com.junior.service.version.VersionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VersionController.class)
class VersionControllerTest extends BaseControllerTest {

    @MockBean
    private VersionService versionService;

    @Test
    @DisplayName("버전 추가 - 버전 추가 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void createVersion() throws Exception {

        //given
        VersionDto versionDto = VersionDto.builder()
                .version("1.0.1")
                .forceUpdate(true)
                .build();

        String iosPlatform = "ios";

        String content = objectMapper.writeValueAsString(versionDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/versions/{platform}", iosPlatform)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customCode").value(StatusCode.VERSION_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.VERSION_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("버전 체크 - 버전 확인 응답이 정상적으로 반환되어야 함")
    void checkVersion() throws Exception {

        //given
        String version = "1.0.1";

        VersionCheckResponseDto versionCheckResponseDto = VersionCheckResponseDto.builder()
                .requireUpdate(false)
                .forceUpdate(false)
                .build();

        given(versionService.checkVersion(any(Platform.class), anyString())).willReturn(versionCheckResponseDto);

        String iosPlatform = "ios";

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/versions/{platform}/check", iosPlatform)
                        .queryParam("version", version)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.VERSION_CHECK_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.VERSION_CHECK_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.requireUpdate").value(false))
                .andExpect(jsonPath("$.data.forceUpdate").value(false));
    }
}