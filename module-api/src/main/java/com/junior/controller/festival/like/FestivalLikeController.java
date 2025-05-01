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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FestivalLikeController implements FestivalLikeApi {

    private final FestivalLikeService festivalLikeService;

    @PostMapping("/api/v1/festivals/{festival_id}/likes")
    public ResponseEntity<CommonResponse<Object>> save(@AuthenticationPrincipal UserPrincipal principal, @PathVariable(name = "festival_id") Long festivalId) {
        festivalLikeService.save(principal, festivalId);

        return ResponseEntity.status(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS, null));
    }


        return ResponseEntity.status(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS, null));
    }
}
