package com.junior.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.junior.exception.CustomException;
import com.junior.exception.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.path.profile}")
    private String profilePath;

    private final AmazonS3Client amazonS3Client;
    private Set<String> uploadedFileNames = new HashSet<>();
    private Set<Long> uploadedFileSizes = new HashSet<>();

    
    //단일 파일 업로드
    public String saveFile(MultipartFile file) {
        String randomFilename = generateRandomFilename(file);

        log.info("File upload started: " + randomFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3Client.putObject(bucket, randomFilename, file.getInputStream(), metadata);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new CustomException(StatusCode.S3_UPLOAD_FAIL);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new CustomException(StatusCode.S3_UPLOAD_FAIL);
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new CustomException(StatusCode.S3_UPLOAD_FAIL);
        }

        log.info("File upload completed: " + randomFilename);

        return amazonS3Client.getUrl(bucket, randomFilename).toString();
    }

    //프로필 사진 업로드
    public String saveProfileImage(MultipartFile file) {
        String randomFilename = profilePath + generateRandomFilename(file);

        log.info("File upload started: {}", randomFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3Client.putObject(bucket, randomFilename, file.getInputStream(), metadata);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new CustomException(StatusCode.S3_UPLOAD_FAIL);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new CustomException(StatusCode.S3_UPLOAD_FAIL);
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new CustomException(StatusCode.S3_UPLOAD_FAIL);
        }

        log.info("File upload completed: " + randomFilename);

        return amazonS3Client.getUrl(bucket, randomFilename).toString();
    }

    //여러 파일 업로드
    public List<String> saveFiles(List<MultipartFile> multipartFiles) {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            if (isDuplicate(multipartFile)) {
                throw new CustomException(StatusCode.S3_DUPLICATE_FILE);
            }

            String uploadedUrl = saveFile(multipartFile);
            uploadedUrls.add(uploadedUrl);
        }

        clear();
        return uploadedUrls;
    }

    private void clear() {
        uploadedFileNames.clear();
        uploadedFileSizes.clear();
    }

    private boolean isDuplicate(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        Long fileSize = multipartFile.getSize();

        if (uploadedFileNames.contains(fileName) && uploadedFileSizes.contains(fileSize)) {
            return true;
        }

        uploadedFileNames.add(fileName);
        uploadedFileSizes.add(fileSize);

        return false;
    }

    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        return UUID.randomUUID() + "." + fileExtension;
    }

    private String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new CustomException(StatusCode.S3_NOT_ALLOWED_EXTENSION);
        }
        return fileExtension;
    }

    public void deleteProfileImage(String profileImageUrl) {
        String splitStr = ".com/";
        //https://"bucket-name"."region".amazonaws.com/"파일 이름.확장자"에서 파일 이름.확장자만 자르기
        String fileName = profileImageUrl.substring(profileImageUrl.lastIndexOf(splitStr) + splitStr.length());

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
