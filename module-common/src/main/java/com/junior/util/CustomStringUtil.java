package com.junior.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CustomStringUtil {


    /**
     * @param date yyyyMMdd 형태의 문자열
     * @return 해당 일자에 맞는 LocalDate 객체
     */
    public static LocalDate stringToDate(String date) {
        return LocalDate.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8)));
    }

    /**
     * @param startDate
     * @param endDate
     * @return 시작일과 종료일을 ****년 **월 **일 - ****년 **월 **일 형태로 리턴
     */
    public static String durationToString(String startDate, String endDate) {

        //시작일 및 종료일이 8자리 숫자가 아닐 경우 빈 문자열 리턴
        if (!Pattern.matches("^[0-9]{8}", startDate) || !Pattern.matches("^[0-9]{8}", startDate)) {
            return "";
        }

        return String.format("%d년 %d월 %d일 - %d년 %d월 %d일", Integer.parseInt(startDate.substring(0, 4)), Integer.parseInt(startDate.substring(4, 6)), Integer.parseInt(startDate.substring(6, 8)),
                Integer.parseInt(endDate.substring(0, 4)), Integer.parseInt(endDate.substring(4, 6)), Integer.parseInt(endDate.substring(6, 8)));

    }

    public static String durationToString(LocalDate startDate, LocalDate endDate) {

        String stStartDate = startDate.toString().replaceAll("-", "");
        String stEndDate = endDate.toString().replaceAll("-", "");

        return String.format("%d년 %d월 %d일 - %d년 %d월 %d일", Integer.parseInt(stStartDate.substring(0, 4)), Integer.parseInt(stStartDate.substring(4, 6)), Integer.parseInt(stStartDate.substring(6, 8)),
                Integer.parseInt(stEndDate.substring(0, 4)), Integer.parseInt(stEndDate.substring(4, 6)), Integer.parseInt(stEndDate.substring(6, 8)));

    }
}
