package com.junior.controller.api;

import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.story.GeoRect;
import com.junior.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "축제 관련")
@Validated
public interface FestivalApi {

    @Operation(summary = "축제 데이터 저장", description = "TourAPI 4.0에서 축제 데이터를 받아와 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "축제 데이터 저장 성공"),
            @ApiResponse(responseCode = "500", description = "축제 데이터 저장 실패")
    })
    public ResponseEntity<CommonResponse<Object>> saveFestival(@Pattern(regexp = "^[0-9]{8}", message = "잘못 된 날짜 형식입니다.") @RequestParam(value = "eventStartDate") String eventStartDate,
                                                               @Pattern(regexp = "^[0-9]{8}|^$", message = "잘못 된 날짜 형식입니다.") @RequestParam(value = "eventEndDate", required = false, defaultValue = "") String eventEndDate);


    @Operation(summary = "축제 도시 개수", description = "시/도 별 축제의 개수를 응답합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "축제 개수 응답 성공"),
    })
    public ResponseEntity<CommonResponse<List<FestivalCityCountDto>>> findFestivalCityCount();


    @Operation(summary = "지도 기반 축제 리스트 조회", description = "지도의 좌측 최상단, 우측 최하단 좌표를 받아 이 사이에 해당하는 축제의 위치를 리턴합니다.")
    @ApiResponse(responseCode = "200", description = "지도 기반 축제 리스트 조회 성공")
    ResponseEntity<CommonResponse<List<FestivalMapDto>>> findFestivalByMap(@RequestBody GeoRect geoRect);

}
