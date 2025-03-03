package com.junior.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCode {


/*
    example)
    NOT_FOUND(404, "COMMON-ERR-404", "존재하지 않는 페이지입니다."),
*/

    // notification 관련 성공 코드
    NOTIFICATION_READ_SUCCESS(200, "NOTIFICATION-SUCCESS-001", ""),
    NOTIFICATION_DELETE_SUCCESS(200, "NOTIFICATION-SUCCESS-002", ""),
    NOTIFICATION_NOT_PERMISSION(200, "NOTIFICATION-SUCCESS-003", ""),

    // firebase 토큰 관련 예외
    FCM_CREATE_SUCCESS(200, "NOTIFICATION-SUCCESS-0001", "토큰 저장 및 갱신 성공"),

    // notification 관련 성공 코드
    NOTIFICATION_NOT_FOUND(400, "NOTIFICATION-ERR-001", "서버 에러가 발생했습니다."),

    // STORY 관련 성공 코드
    STORY_CREATE_SUCCESS(200, "STORY-SUCCESS-001", ""),
    STORY_EDIT_SUCCESS(200, "STORY-SUCCESS-002", ""),
    STORY_DELETE_SUCCESS(200, "STORY-SUCCESS-003", ""),
    STORY_READ_SUCCESS(200, "STORY-SUCCESS-004", ""),

    // STORY 관련 실패 코드
    STORY_NOT_FOUND(400, "STORY-ERR-001", "서버 에러가 발생했습니다."),
    STORY_CREATE_FAIL(500, "STORY-ERR-002", "서버 에러가 발생했습니다."),
    STORY_BAD_REQUEST(500, "STORY-ERR-003", "처음부터 다시 등록해주세요"),
    STORY_NOT_PERMISSION(400, "STORY-ERR-004", "권한이 없는 계정입니다."),
    STORY_DELETED(400, "STORY-ERR-005", "삭제된 스토리입니다."),

    //추천 관련 성공 코드
    RECOMMENDED_CITY_SUCCESS(200, "RECOMMENDED-SUCCESS-001", ""),
    RECOMMENDED_STORIES_SUCCESS(200, "RECOMMENDED-SUCCESS-002", ""),

    // like 관련 성공 코드
    LIKE_CHANGE_SUCCESS(200, "LIKE-SUCCESS-001", ""),

    // like 관련 실패 코드
    LIKE_NOT_FOUND(400, "LIKE-ERR-001", "서버 에러가 발생했습니다."),

    // comment 관련 성공 코드
    COMMENT_CREATE_SUCCESS(200, "COMMENT-SUCCESS-001", ""),
    COMMENT_READ_SUCCESS(200, "COMMENT-SUCCESS-002", ""),
    COMMENT_EDIT_SUCCESS(200, "COMMENT-SUCCESS-003", ""),
    COMMENT_DELETE_SUCCESS(200, "COMMENT-SUCCESS-004", ""),

    // comment 관련 실패 코드
    COMMENT_NOT_FOUND(400, "COMMENT-ERR-001", "서버 에러가 발생했습니다."),

    //s3 관련 성공 코드
    S3_UPLOAD_SUCCESS(200, "S3-SUCCESS-001", ""),
    S3_DELETE_SUCCESS(200, "S3-SUCCESS-002", ""),

    //s3 관련 실패 코드
    S3_UPLOAD_FAIL(500, "S3-ERR-001", "서버 에러가 발생했습니다."),
    S3_DUPLICATE_FILE(500, "S3-ERR-002", "올릴수 없는 이미지가 포함되어 있습니다."),
    S3_NOT_ALLOWED_EXTENSION(500, "S3-ERR-003", "올릴수 없는 이미지가 포함되어 있습니다."),
    S3_BIGGER_THAN_MAX_SIZE(500, "S3-ERR-004", "용량이 큰 이미지 입니다."),

    // USER 관련 성공 코드
    ACTIVATE_MEMBER(200, "MEMBER-SUCCESS-001", ""),
    CHECK_NICKNAME_MEMBER(200, "MEMBER-SUCCESS-002", ""),
    DELETE_MEMBER(200, "MEMBER-SUCCESS-003", ""),
    OAUTH2_LOGIN_SUCCESS(200, "MEMBER-SUCCESS-004", ""),
    UPDATE_NICKNAME_MEMBER(200, "MEMBER-SUCCESS-005", ""),
    UPDATE_PROFILE_IMAGE_MEMBER(200, "MEMBER-SUCCESS-006", ""),
    GET_MEMBER_INFO(200, "MEMBER-SUCCESS-007", ""),
    GET_MEMBER_ACTIVATE(200, "MEMBER-SUCCESS-008", ""),
    GET_MEMBERS(200, "MEMBER-SUCCESS-009", ""),

    // MEMBER 관련 실패 코드
    INVALID_MEMBER(401, "MEMBER-ERR-001", "없는 회원 입니다."),
    INVALID_MEMBER_STATUS(401, "MEMBER-ERR-002", "없는 회원 입니다."),
    MEMBER_NOT_FOUND(404, "MEMBER-ERR-003", "없는 회원 입니다."),

    // LOGIN 관련 성공 코드
    ADMIN_LOGIN_SUCCESS(200, "LOGIN-SUCCESS-001", ""),

    // LOGIN 관련 실패 코드
    ADMIN_LOGIN_FAILURE(401, "LOGIN-ERR-001", "오류가 발생했습니다."),
    OAUTH2_LOGIN_FAILURE(400, "LOGIN-ERR-002", "오류가 발생했습니다."),

    // JWT 관련 성공 코드
    REISSUE_SUCCESS(200, "JWT-SUCCESS-001", "JWT 재발급 완료"),
    LOGOUT(200, "JWT-SUCCESS-002", "로그아웃 완료"),

    // JWT 관련 실패 코드
    EXPIRED_ACCESS_TOKEN(401, "JWT-ERR-001", "다시 로그인 해주세요"),
    TOKEN_NOT_EXIST(400, "JWT-ERR-002", "다시 로그인 해주세요"),
    NOT_REFRESH_TOKEN(401, "JWT-ERR-003", "다시 로그인 해주세요"),
    EXPIRED_REFRESH_TOKEN(401, "JWT-ERR-004", "다시 로그인 해주세요"),
    NOT_ACCESS_TOKEN(401, "JWT-ERR-005", "다시 로그인 해주세요"),
    ACCESS_DENIED(403, "JWT-ERR-006", "권한이 없는 계정입니다."),
    INVALID_TOKEN(401, "JWT-ERR-007", "다시 로그인 해주세요"),

    // 공지사항 관련 성공 코드
    NOTICE_CREATE_SUCCESS(201, "NOTICE-SUCCESS-001", ""),
    NOTICE_DELETE_SUCCESS(200, "NOTICE-SUCCESS-002", ""),
    NOTICE_UPDATE_SUCCESS(200, "NOTICE-SUCCESS-003", ""),
    NOTICE_FIND_SUCCESS(200, "NOTICE-SUCCESS-004", ""),
    NOTICE_FIND_DETAIL_SUCCESS(200, "NOTICE-SUCCESS-005", ""),

    // 공지사항 관련 실패 코드
    NOTICE_NOT_FOUND(400, "NOTICE-ERR-001", "서버 에러가 발생했습니다."),

    // Q&A 관련 성공 코드
    QNA_CREATE_SUCCESS(201, "Q&A-SUCCESS-001", ""),
    QNA_DELETE_SUCCESS(200, "Q&A-SUCCESS-002", ""),
    QNA_UPDATE_SUCCESS(200, "Q&A-SUCCESS-003", ""),
    QNA_FIND_SUCCESS(200, "Q&A-SUCCESS-004", ""),
    QNA_FIND_DETAIL_SUCCESS(200, "Q&A-SUCCESS-005", ""),

    // Q&A 관련 실패 코드
    QNA_NOT_FOUND(400, "Q&A-ERR-001", "서버 에러가 발생했습니다."),

    // 신고 관련 성공 코드
    REPORT_CREATE_SUCCESS(201, "REPORT-SUCCESS-001", ""),
    REPORT_CONFIRM_SUCCESS(200, "REPORT-SUCCESS-002", ""),
    REPORT_DELETE_TARGET_SUCCESS(200, "REPORT-SUCCESS-003", ""),
    REPORT_FIND_SUCCESS(200, "REPORT-SUCCESS-004", ""),

    // 신고 관련 실패 코드
    REPORT_NOT_VALID(400, "REPORT-ERR-001", "서버 에러가 발생했습니다."),
    REPORT_NOT_FOUND(400, "REPORT-ERR-002", "해당 신고내역을 찾을 수 없습니다."),
    REPORT_TYPE_NOT_VALID(400, "REPORT-ERR-003", "서버 에러가 발생했습니다."),
    REPORT_EQUALS_AUTHOR(202, "REPORT-ERR-004", "본인 글은 신고가 불가능합니다."),
    REPORT_DUPLICATE(400, "REPORT-ERR-005", "이미 신고된 글 입니다."),

    //버전 관리 관련 성공 코드
    VERSION_CREATE_SUCCESS(201, "VERSION-SUCCESS-001", ""),
    VERSION_CHECK_SUCCESS(200, "VERSION-SUCCESS-002", ""),

    //버전 관리 관련 실패 코드
    VERSION_NOT_FOUND(400, "VERSION-ERR-001", "서버 에러가 발생했습니다."),


    // 공통 예외
    UNKNOWN_ERROR(500, "UNKNOWN-ERROR-001", "서버 에러가 발생했습니다.");

    private final int httpCode;
    private final String customCode;
    private final String customMessage;

}
