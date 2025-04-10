package com.junior.controller.login;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.jwt.RefreshTokenDto;
import com.junior.exception.StatusCode;
import com.junior.security.WithMockCustomUser;
import com.junior.service.login.ReissueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReissueController.class)
class ReissueControllerTest extends BaseControllerTest {


    @MockBean
    private ReissueService reissueService;

    @Test
    @DisplayName("reissue - 응답을 정상적으로 리턴해야 함")
    @WithMockCustomUser
    void reissue() throws Exception {

        //given

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("Bearer sample_token");

        String content = objectMapper.writeValueAsString(refreshTokenDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/reissue")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REISSUE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REISSUE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

    }
}