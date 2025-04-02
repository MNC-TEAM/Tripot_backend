package com.junior.controller.api;

import com.junior.dto.version.VersionCheckResponseDto;
import com.junior.dto.version.VersionDto;
import com.junior.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "앱 버전 관리")
@Validated
public interface VersionApi {

    @Operation(summary = "버전 추가", description = "앱의 최신 버전을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "버전 추가 성공"),
            @ApiResponse(responseCode = "400", description = "플랫폼 오입력")
    })
    public ResponseEntity<CommonResponse<Object>> createVersion(@RequestBody VersionDto versionDto, @PathVariable("platform") String platform);

    @Operation(summary = "버전 비교", description = "현재 앱 버전이 최신 버전인지 비교합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "버전 비교 성공"),
            @ApiResponse(responseCode = "400", description = "플랫폼 오입력", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<CommonResponse<VersionCheckResponseDto>> checkVersion(@RequestParam("version") String version, @PathVariable("platform") String platform);
}
