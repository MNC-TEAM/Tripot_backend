package com.junior.service.firebase;

import com.junior.domain.firebase.FcmNotificationToken;
import com.junior.domain.member.Member;
import com.junior.dto.firebase.FcmTokenDto;
import com.junior.repository.firebase.FcmNotificationTokenRepository;
import com.junior.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmNotificationTokenService {

    private final FcmNotificationTokenRepository fcmNotificationTokenRepository;

    @Transactional
    public void subscribe(UserPrincipal userPrincipal, FcmTokenDto fcmTokenDto) {
        Member member = userPrincipal.getMember();
        String token = fcmTokenDto.fcmToken();

        if (fcmNotificationTokenRepository.existsByToken(token)) {
            FcmNotificationToken findFcm = fcmNotificationTokenRepository.findByToken(token).orElseThrow();

            LocalDateTime lastUsedDateTime = LocalDateTime.now();
            findFcm.updateDate(lastUsedDateTime);

        } else {
            FcmNotificationToken fcmNotificationToken = FcmNotificationToken.builder()
                    .member(member)
                    .token(fcmTokenDto.fcmToken())
                    .build();

            fcmNotificationTokenRepository.save(fcmNotificationToken);
        }
    }
}
