package com.junior.controller.festival.like;

import com.junior.controller.api.FestivalLikeApi;
import com.junior.dto.festival.like.CreateFestivalLikeDto;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.festival.like.FestivalLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FestivalLikeController implements FestivalLikeApi {

    private final FestivalLikeService festivalLikeService;

    @PostMapping("/api/v1/festival-likes")
    public ResponseEntity<CommonResponse<Object>> save(@AuthenticationPrincipal UserPrincipal principal, CreateFestivalLikeDto createFestivalLikeDto) {
        festivalLikeService.save(principal, createFestivalLikeDto);

        return ResponseEntity.status(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS, null));
    }
}
