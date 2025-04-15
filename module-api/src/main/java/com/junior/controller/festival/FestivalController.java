package com.junior.controller.festival;

import com.junior.controller.api.FestivalApi;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDetailDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.story.GeoRect;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.service.festival.FestivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FestivalController implements FestivalApi {

    private final FestivalService festivalService;


    /**
     * 축제 정보를 API로 받아와서 저장하는 기능
     * ADMIN 권한에서만 동작
     */
    @PostMapping("/api/v1/festivals")
    public ResponseEntity<CommonResponse<Object>> saveFestival(@RequestParam(value = "eventStartDate") String eventStartDate,
                                                               @RequestParam(value = "eventEndDate", required = false, defaultValue = "") String eventEndDate) {
        festivalService.saveFestival(eventStartDate, eventEndDate);

        return ResponseEntity.status(StatusCode.FESTIVAL_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_CREATE_SUCCESS, null));

    }

    @GetMapping("/api/v1/festivals/cities/count")
    public ResponseEntity<CommonResponse<List<FestivalCityCountDto>>> findFestivalCityCount() {
        return ResponseEntity.status(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS, festivalService.findFestivalCityCount()));
    }

    @GetMapping("/api/v1/festivals/map")
    public ResponseEntity<CommonResponse<List<FestivalMapDto>>> findFestivalByMap(GeoRect geoRect) {
        return ResponseEntity.status(StatusCode.FESTIVAL_FIND_MAP_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_FIND_MAP_SUCCESS, festivalService.findFestivalByMap(geoRect)));
    }

    @GetMapping("/api/v1/festivals")
    public ResponseEntity<CommonResponse<Slice<FestivalDto>>> findFestival(@RequestParam(name = "cursorId", required = false) Long cursorId,
                                                                           @RequestParam(name = "size") int size,
                                                                           @RequestParam(name = "city", required = false, defaultValue = "") String city,
                                                                           @RequestParam(name = "q", required = false, defaultValue = "") String q) {

        return ResponseEntity.status(StatusCode.FESTIVAL_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_FIND_SUCCESS, festivalService.findFestival(cursorId, size, city, q)));
    }

    @GetMapping("/api/v1/festivals/{festival_id}")
    public ResponseEntity<CommonResponse<FestivalDetailDto>> findFestivalDetail(@PathVariable("festival_id") Long festivalId) {
        return ResponseEntity.status(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS, festivalService.findFestivalDetail(festivalId)));
    }


}
