package com.junior.service.version;

import com.junior.domain.version.Platform;
import com.junior.domain.version.Version;
import com.junior.dto.version.VersionCheckResponseDto;
import com.junior.dto.version.VersionDto;
import com.junior.repository.version.VersionRepository;
import com.junior.service.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class VersionServiceTest extends BaseServiceTest {

    @Mock
    private VersionRepository versionRepository;

    @InjectMocks
    private VersionService versionService;

    @Test
    @DisplayName("버전 추가 - 버전이 정상적으로 저장되어야 함")
    void createVersion() {

        //given
        VersionDto versionDto = VersionDto.builder()
                .version("1.0.1")
                .forceUpdate(true)
                .build();

        Platform iosPlatform = Platform.IOS;

        //when
        versionService.createVersion(versionDto, iosPlatform);

        //then
        verify(versionRepository).save(any(Version.class));
    }

    @Test
    @DisplayName("버전 체크 - 최신 버전일 경우 업데이트를 권장하지 않아야 함")
    public void requireUpdateFalseIfUserAppVersionIsLatest() throws Exception {
        //given
        Version latestVersion = Version.builder()
                .platform(Platform.IOS)
                .forceUpdate(true)
                .version("1.3.0")
                .build();

        String version = "1.3.0";

        given(versionRepository.findFirstByPlatformOrderByCreatedDateDesc(any(Platform.class)))
                .willReturn(Optional.ofNullable(latestVersion));

        //when
        VersionCheckResponseDto versionCheckResponseDto = versionService.checkVersion(Platform.IOS, version);

        //then
        assertThat(versionCheckResponseDto.requireUpdate()).isFalse();
        assertThat(versionCheckResponseDto.forceUpdate()).isFalse();

    }

    @Test
    @DisplayName("버전 체크 - 최신 버전이 아닐 경우 업데이트를 권장해야 함")
    public void requireUpdateTrueIfUserAppVersionIsNotLatest() throws Exception {
        //given
        Version latestVersion = Version.builder()
                .platform(Platform.IOS)
                .forceUpdate(false)
                .version("1.3.0")
                .build();

        String version = "1.1.0";

        given(versionRepository.findFirstByPlatformOrderByCreatedDateDesc(any(Platform.class)))
                .willReturn(Optional.ofNullable(latestVersion));

        //when
        VersionCheckResponseDto versionCheckResponseDto = versionService.checkVersion(Platform.IOS, version);

        //then
        assertThat(versionCheckResponseDto.requireUpdate()).isTrue();
        assertThat(versionCheckResponseDto.forceUpdate()).isFalse();

    }

    @Test
    @DisplayName("버전 체크 - 최신 버전이 아니면서 강제 업데이트를 진행해야 할 경우 업데이트를 강제해야 함")
    public void requireUpdateTrueAndForceUpdateTrueIfUserAppVersionIsNotLatestAndLatestVersionRequiresForceUpdate() throws Exception {
        //given
        Version latestVersion = Version.builder()
                .platform(Platform.IOS)
                .forceUpdate(true)
                .version("1.3.0")
                .build();

        String version = "1.1.0";

        given(versionRepository.findFirstByPlatformOrderByCreatedDateDesc(any(Platform.class)))
                .willReturn(Optional.ofNullable(latestVersion));

        //when
        VersionCheckResponseDto versionCheckResponseDto = versionService.checkVersion(Platform.IOS, version);

        //then
        assertThat(versionCheckResponseDto.requireUpdate()).isTrue();
        assertThat(versionCheckResponseDto.forceUpdate()).isTrue();

    }
}