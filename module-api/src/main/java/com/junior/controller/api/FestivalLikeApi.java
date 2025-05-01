package com.junior.controller.api;


import com.junior.dto.festival.like.CreateFestivalLikeDto;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

public interface FestivalLikeApi {

    ResponseEntity<CommonResponse<Object>> save(@AuthenticationPrincipal UserPrincipal principal,
                                                       Long festivalId);

    ResponseEntity<CommonResponse<Object>> delete(@AuthenticationPrincipal UserPrincipal principal, Long festivalId);
}
