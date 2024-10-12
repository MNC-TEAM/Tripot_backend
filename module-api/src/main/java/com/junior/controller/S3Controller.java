package com.junior.controller;


import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/s3UploadImage")
    public CommonResponse<String> fileUpload(
            @RequestPart MultipartFile img) {

        String url = s3Service.saveFile(img);

        return CommonResponse.<String>builder()
                .returnCode(StatusCode.S3_UPLOAD_SUCCESS.getCode())
                .returnMessage(StatusCode.S3_UPLOAD_SUCCESS.getMessage())
                .info(url)
                .build();
    }
}