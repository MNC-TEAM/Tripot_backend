package com.junior.response;


import com.junior.exception.StatusCode;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommonResponse<T> {
    private String customCode;
    private String customMessage;
    private boolean status;
    private T data;


    public static <T> CommonResponse<T> success(StatusCode statusCode, T data) {
        return new CommonResponse<>(statusCode.getCustomCode(), statusCode.getCustomMessage(), true, data);
    }

    public static <T> CommonResponse<T> fail(StatusCode statusCode) {
        return new CommonResponse<>(statusCode.getCustomCode(), statusCode.getCustomMessage(), false, null);
    }
}
