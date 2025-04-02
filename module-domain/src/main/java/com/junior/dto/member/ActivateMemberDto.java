package com.junior.dto.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ActivateMemberDto(

        @Size(min = 0, max = 25, message = "닉네임은 25자까지 가능합니다.")
        @NotNull(message = "닉네임은 필수 값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]+$", message = "잘못된 닉네임 형식입니다.")
        String nickname,
/*        // 서비스 이용 약관 동의 여부
        Boolean isAgreeTermsUse,
        // 개인정보 수집 및 이용 동의
        Boolean isAgreeCollectingUsingPersonalInformation,
        // 마케팅 수신 동의
        Boolean isAgreeMarketing,*/


        String recommendLocation

) {

}



