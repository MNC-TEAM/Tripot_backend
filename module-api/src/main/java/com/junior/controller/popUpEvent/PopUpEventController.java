package com.junior.controller.popUpEvent;

import com.junior.controller.api.PopUpEventApi;
import com.junior.dto.popUpEvent.CreateNewPopUpEventDto;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.dto.popUpEvent.UpdatePopUpEventDto;
import com.junior.dto.story.GeoRect;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.popUpEvent.PopUpEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pop-up-events")
@RequiredArgsConstructor
public class PopUpEventController implements PopUpEventApi {

    private final PopUpEventService popUpEventService;

    //    @Operation(summary = "팝업 이벤트 생성 (ADMIN만 가능)")
    @PostMapping
    public CommonResponse<Object> createEvent(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @RequestBody CreateNewPopUpEventDto createNewPopUpEventDto) {
        popUpEventService.createEvent(userPrincipal, createNewPopUpEventDto);

        return CommonResponse.success(StatusCode.POPUPEVENT_CREATE_SUCCESS, null);
    }

    //    @Operation(summary = "팝업 이벤트 수정 (ADMIN만 가능)")
    @PatchMapping("/{id}")
    public CommonResponse<Object> editEvent(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable("id") Long popUpEventId,
                                            @RequestBody UpdatePopUpEventDto updatePopUpEventDto) {

        popUpEventService.editEvent(userPrincipal, updatePopUpEventDto, popUpEventId);

        return CommonResponse.success(StatusCode.POPUPEVENT_UPDATE_SUCCESS, null);
    }

    //    @Operation(summary = "팝업 이벤트 삭제 (ADMIN만 가능)")
    @DeleteMapping("/{id}")
    public CommonResponse<Object> deleteEvent(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @PathVariable("id") Long popUpEventId) {

        popUpEventService.deletePopUpEvent(userPrincipal, popUpEventId);

        return CommonResponse.success(StatusCode.POPUPEVENT_DELETE_SUCCESS, null);
    }


    //    @Operation(summary = "지도 영역 내 팝업 이벤트 조회")
    @PostMapping("/map")
    public CommonResponse<List<ResponsePopUpEventDto>> getEventsByPos(
            @RequestBody GeoRect geoRect) {

        List<ResponsePopUpEventDto> popUpEventsByPos = popUpEventService.getPopUpEventsByPos(geoRect.geoPointLt(), geoRect.geoPointRb());

        return CommonResponse.success(StatusCode.POPUPEVENT_READ_SUCCESS, popUpEventsByPos);
    }

    //    @Operation(summary = "스크롤 기반 팝업 이벤트 조회")
    @GetMapping("/scroll")
    public CommonResponse<Slice<ResponsePopUpEventDto>> scrollEvents(
            @RequestParam(required = false, name = "cursorId") Long cursorId,
            @RequestParam(defaultValue = "10", name = "size") int size) {
        Slice<ResponsePopUpEventDto> responsePopUpEventDtos = popUpEventService.loadPopUpEventsOnScroll(cursorId, size);

        return CommonResponse.success(StatusCode.POPUPEVENT_READ_SUCCESS, responsePopUpEventDtos);
    }

    @GetMapping("/list")
    public CommonResponse<Page<ResponsePopUpEventDto>> getPopUpEventByPage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) {
        Page<ResponsePopUpEventDto> responsePopUpEventDtos = popUpEventService.getPopUpEventsByPage(userPrincipal, page, size);

        return CommonResponse.success(StatusCode.POPUPEVENT_READ_SUCCESS, responsePopUpEventDtos);
    }

    @GetMapping("/{popUpEventId}")
    public CommonResponse<ResponsePopUpEventDto> getPopUpEventById(
            @PathVariable("popUpEventId") Long popUpEventId
    ) {
        ResponsePopUpEventDto responsePupUpEventDto = popUpEventService.getPopUpEventsById(popUpEventId);

        return CommonResponse.success(StatusCode.POPUPEVENT_READ_SUCCESS, responsePupUpEventDto);
    }
}
