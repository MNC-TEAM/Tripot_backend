package com.junior.service;

import com.junior.domain.admin.Notice;
import com.junior.domain.festival.Festival;
import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.domain.report.Report;
import com.junior.domain.report.ReportReason;
import com.junior.domain.report.ReportType;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class BaseServiceTest {


    protected Member createActiveTestMember() {
        return Member.builder()
                .id(2L)
                .nickname("테스트사용자닉네임")
                .username("테스트사용자유저네임")
                .role(MemberRole.USER)
                .signUpType(SignUpType.KAKAO)
                .profileImage("s3.com/testProfile")
                .recommendLocation("서울")
                .status(MemberStatus.ACTIVE)
                .build();
    }

    protected Member createActiveTestMember2() {
        return Member.builder()
                .id(4L)
                .nickname("테스트사용자닉네임2")
                .username("테스트사용자유저네임2")
                .role(MemberRole.USER)
                .signUpType(SignUpType.KAKAO)
                .profileImage("s3.com/testProfile")
                .recommendLocation("서울")
                .status(MemberStatus.ACTIVE)
                .build();
    }

    protected Story createStory(Member member, String title, String city) {
        return Story.createStory()
                .title(title)
                .member(member)
                .content("content")
                .longitude(1.0)
                .latitude(1.0)
                .city(city)
                .isHidden(false)
                .thumbnailImg("thumbURL")
                .build();
    }

    protected Comment createComment(Member member, Story story) {

        return Comment.builder()
                .member(member)
                .content("content")
                .story(story)
                .build();
    }

    protected Report createReport(Member member, ReportType reportType, Story story) {
        return Report.builder()
                .member(member)
                .reportType(reportType)
                .reportReason(ReportReason.SPAMMARKET)
                .story(story)
                .build();
    }

    protected Report createReport(Member member, ReportType reportType, Comment comment) {
        return Report.builder()
                .member(member)
                .reportType(reportType)
                .reportReason(ReportReason.SPAMMARKET)
                .comment(comment)
                .build();
    }

    protected Notice createNotice() {
        Notice notice = Notice.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
        return notice;
    }

    protected Member createPreactiveTestMember() {
        return Member.builder()
                .id(1L)
                .nickname("테스트비활성화닉네임")
                .username("테스트비활성화유저네임")
                .role(MemberRole.USER)
                .signUpType(SignUpType.KAKAO)
                .profileImage("s3.com/testProfile")
                .recommendLocation("서울")
                .build();
    }


    protected MockMultipartFile createMockMultipartFile() {
        MockMultipartFile profileImg = new MockMultipartFile(
                "프로필 사진",
                "profiles.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );

        return profileImg;

    }

    protected Festival createFestival(String title, String city, double lat, double logt, long contentId, LocalDate startDate, LocalDate endDate) {
        return Festival.builder()
                .contentId(contentId)
                .title(title)
                .city(city)
                .location("location")
                .imgUrl("url.com")
                .startDate(startDate)
                .endDate(endDate)
                .lat(lat)
                .logt(logt)
                .build();
    }
}
