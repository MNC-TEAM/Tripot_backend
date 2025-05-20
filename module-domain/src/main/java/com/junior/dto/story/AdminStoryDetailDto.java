package com.junior.dto.story;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.story.Story;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdminStoryDetailDto(
        Long id,
        String title,
        String content,
        String authorNick,
        String thumbnailImg,
        double latitude,
        double longitude,
        String city,
        Long likeCnt,
        LocalDateTime createdDate,
        List<String> imgUrls,
        Boolean isDeleted,
        LocalDateTime deletedDate
) {


    public static AdminStoryDetailDto from(Story story) {
        return AdminStoryDetailDto.builder()
                .id(story.getId())
                .title(story.getTitle())
                .content(story.getContent())
                .authorNick(!story.getMember().getStatus().equals(MemberStatus.DELETE) ? story.getMember().getNickname() : "탈퇴회원")
                .thumbnailImg(story.getThumbnailImg())
                .latitude(story.getLatitude())
                .longitude(story.getLongitude())
                .city(story.getCity())
                .likeCnt(story.getLikeCnt())
                .imgUrls(story.getImgUrls())
                .createdDate(story.getCreatedDate())
                .isDeleted(story.getIsDeleted())
                .deletedDate(story.getIsDeleted() ? story.getLastModifiedDate() : null)
                .build();
    }
}
