package com.junior.controller;


import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.security.UserPrincipal;
import com.junior.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/new")
    public CommonResponse<Object> fileUpload(
            @RequestPart(name = "img") MultipartFile img) {

        String url = s3Service.saveFile(img);

        return CommonResponse.success(StatusCode.S3_UPLOAD_SUCCESS, url);
    }

    @PostMapping("/news")
    public CommonResponse<Object> filesUpload(
            @RequestParam(name = "imgs") List<MultipartFile> images
    ) {
        List<String> urls = s3Service.saveFiles(images);

        return CommonResponse.success(StatusCode.S3_UPLOAD_SUCCESS, urls);
    }

    @DeleteMapping
    public CommonResponse<Object> deleteImg(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "imgPath") String imgPath
    ) {
        s3Service.deleteStoryImage(imgPath);

        return CommonResponse.success(StatusCode.S3_DELETE_SUCCESS, null);
    }
}