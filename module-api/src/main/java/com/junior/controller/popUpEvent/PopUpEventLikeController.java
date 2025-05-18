package com.junior.controller.popUpEvent;

import com.junior.controller.api.PopUpEventLikeApi;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.popUpEvent.PopUpEventLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PopUpEventLikeController implements PopUpEventLikeApi {
    private final PopUpEventLikeService popUpEventLikeService;

    @PostMapping("/{popUpEventId}/like")
    public CommonResponse<Object> likePopUpEvent(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable Long popUpEventId) {
        popUpEventLikeService.save(userPrincipal, popUpEventId);

        return CommonResponse.success(StatusCode.POPUPEVENT_LIKE_CREATE_SUCCESS, null);
    }

    @GetMapping("pop-up-event/likes")
    public CommonResponse<Slice<ResponsePopUpEventDto>> getLikedPopUpEvents(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        Slice<ResponsePopUpEventDto> popUpEventsByLike = popUpEventLikeService.findPopUpEventsByLike(userPrincipal, cursorId, size);

        return CommonResponse.success(StatusCode.POPUPEVENT_READ_SUCCESS, popUpEventsByLike);
    }

    @DeleteMapping("/{popUpEventId}/like")
    public CommonResponse<Slice<Object>> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable(name = "festival_id") Long festivalId) {
        popUpEventLikeService.delete(principal, festivalId);

        return CommonResponse.success(StatusCode.POPUPEVENT_LIKE_DELETE_SUCCESS, null);
    }

}
