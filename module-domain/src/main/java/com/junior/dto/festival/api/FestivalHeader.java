package com.junior.dto.festival.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalHeader {

    private String resultCode;
    private String resultMsg;
}
