package com.junior.integration.version;

import com.junior.domain.member.Member;
import com.junior.domain.version.Platform;
import com.junior.domain.version.Version;
import com.junior.dto.version.VersionDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.version.VersionRepository;
import com.junior.security.WithMockCustomAdmin;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class VersionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private VersionRepository versionRepository;

    @BeforeEach
    void init() {
        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();
        Member activeTestMember2 = createActiveTestMember2();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);

    }

    @Test
    @DisplayName("버전 추가 - 버전 추가 기능이 정상 동작해야 함")
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

        Version newVersion = versionRepository.findById(1L).orElseThrow(RuntimeException::new);

        Assertions.assertThat(newVersion.getVersion()).isEqualTo("1.0.1");
        Assertions.assertThat(newVersion.getForceUpdate()).isTrue();

    }

    @Test
    @DisplayName("버전 체크 - 사용자 앱 버전이 최신 버전일 경우 업데이트를 요구하지 않음")
    void requireUpdateFalseIfUserAppVersionIsLatest() throws Exception {

        //given
        String version = "1.0.1";

        //ios 1.0.1 버전
        versionRepository.save(Version.builder().version("1.0.1").forceUpdate(false).platform(Platform.IOS).build());

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

    @Test
    @DisplayName("버전 체크 - 최신 버전이 아닐 경우 업데이트를 권장해야 함")
    void requireUpdateTrueIfUserAppVersionIsNotLatest() throws Exception {

        //given
        String version = "1.0.1";

        //ios 1.1.0 버전
        versionRepository.save(Version.builder().version("1.1.0").forceUpdate(false).platform(Platform.IOS).build());

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
                .andExpect(jsonPath("$.data.requireUpdate").value(true))
                .andExpect(jsonPath("$.data.forceUpdate").value(false));
    }

    @Test
    @DisplayName("버전 체크 - 최신 버전이 아니면서 강제 업데이트를 진행해야 할 경우 업데이트를 강제해야 함")
    void requireUpdateTrueAndForceUpdateTrueIfUserAppVersionIsNotLatestAndLatestVersionRequiresForceUpdate() throws Exception {

        //given
        String version = "1.0.1";

        //ios 1.0.1 버전
        versionRepository.save(Version.builder().version("1.1.0").forceUpdate(true).platform(Platform.IOS).build());

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
                .andExpect(jsonPath("$.data.requireUpdate").value(true))
                .andExpect(jsonPath("$.data.forceUpdate").value(true));
    }
}
