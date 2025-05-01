package com.junior.controller.firebase;

import com.junior.dto.firebase.FcmTokenDto;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.firebase.FcmNotificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FcmNotificationTokenController {

    private final FcmNotificationTokenService fcmNotificationTokenService;

    @PostMapping("/subscribe")
    public CommonResponse<Object> subscribe(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @RequestBody FcmTokenDto fcmTokenDto) {
        fcmNotificationTokenService.subscribe(userPrincipal, fcmTokenDto);

        return CommonResponse.success(StatusCode.FCM_CREATE_SUCCESS, null);
    }
}
