package com.junior.controller.festival;

import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.service.festival.FestivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FestivalController {

    private final FestivalService festivalService;


    /**
     * 축제 정보를 API로 받아와서 저장하는 기능
     * ADMIN 권한에서만 동작
     */
    @Secured("ADMIN")
    @PostMapping("/api/v1/festivals")
    public ResponseEntity<CommonResponse<Object>> saveFestival() {
        festivalService.saveFestival();

        return ResponseEntity.status(StatusCode.FESTIVAL_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_CREATE_SUCCESS, null));

    }

    @GetMapping("/api/v1/festivals/cities/count")
    public ResponseEntity<CommonResponse<Object>> findFestivalCityCount(){
        return ResponseEntity.status(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS, festivalService.findFestivalCityCount()));
    }

}
