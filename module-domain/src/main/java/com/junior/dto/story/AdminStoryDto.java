package com.junior.dto.story;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdminStoryDto(

        String title,
        String city,
        Long id,
        String createdNickname,
        Boolean isDeleted,
        LocalDateTime createdDateTime,
        LocalDateTime deletedDateTime
) {

    @QueryProjection
    public AdminStoryDto(String title, String city, Long id, String createdNickname, Boolean isDeleted, LocalDateTime createdDateTime, LocalDateTime deletedDateTime) {
        this.title = title;
        this.city = city;
        this.id = id;
        this.createdNickname = createdNickname;
        this.isDeleted = isDeleted;
        this.createdDateTime = createdDateTime;
        this.deletedDateTime = isDeleted ? deletedDateTime : null;       //삭제일자는 삭제되었을 경우에만
    }
}
