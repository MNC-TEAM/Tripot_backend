package com.junior.controller.festival.like;

import com.junior.controller.api.FestivalLikeApi;
import com.junior.dto.festival.FestivalDto;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.festival.like.FestivalLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/v1/festivals/likes")
    public ResponseEntity<CommonResponse<Slice<FestivalDto>>> findFestivalLike(@AuthenticationPrincipal UserPrincipal principal,
                                                                               Long cursorId, int size) {
        return ResponseEntity.status(StatusCode.FESTIVAL_FIND_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_FIND_SUCCESS, festivalLikeService.findFestivalLike(cursorId, size, principal)));
    }


    @DeleteMapping("/api/v1/festivals/{festival_id}/likes")
    public ResponseEntity<CommonResponse<Object>> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable(name = "festival_id") Long festivalId) {
        festivalLikeService.delete(principal, festivalId);

        return ResponseEntity.status(StatusCode.FESTIVAL_LIKE_DELETE_SUCCESS.getHttpCode()).body(CommonResponse.success(StatusCode.FESTIVAL_LIKE_DELETE_SUCCESS, null));
    }
}
