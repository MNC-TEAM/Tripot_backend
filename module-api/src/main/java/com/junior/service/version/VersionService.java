package com.junior.service.version;

import com.junior.domain.version.Platform;
import com.junior.domain.version.Version;
import com.junior.dto.version.VersionCheckResponseDto;
import com.junior.dto.version.VersionDto;
import com.junior.exception.CustomException;
import com.junior.exception.StatusCode;
import com.junior.repository.version.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VersionService {

    private final VersionRepository versionRepository;

    @Transactional
    public void createVersion(VersionDto versionDto, Platform platform) {
        Version version = Version.builder()
                .platform(platform)
                .version(versionDto.version())
                .forceUpdate(versionDto.forceUpdate())
                .build();

        log.info("[{}] 버전 저장 version: {}, 플랫폼: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), version.getVersion(), version.getPlatform());
        versionRepository.save(version);
    }

    public VersionCheckResponseDto checkVersion(Platform platform, String version) {

        Version latestVersion = versionRepository.findFirstByPlatformOrderByCreatedDateDesc(platform)
                .orElseThrow(() -> new CustomException(StatusCode.VERSION_NOT_FOUND));

        boolean forceUpdate = false;
        boolean requireUpdate = false;

        log.debug("[{}] 버전 저장 최신 버전: {}, 사용자 버전: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), latestVersion.getVersion(), version);
        if (version.compareTo(latestVersion.getVersion()) < 0) {
            requireUpdate = true;
            if (latestVersion.getForceUpdate()) {
                forceUpdate = true;
            }
        }

        return VersionCheckResponseDto.builder()
                .requireUpdate(requireUpdate)
                .forceUpdate(forceUpdate)
                .build();


    }
}
