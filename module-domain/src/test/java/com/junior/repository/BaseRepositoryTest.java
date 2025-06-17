package com.junior.repository;

import com.junior.TestConfig;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BaseRepositoryTest {

    public Member createActiveTestMember() {
        return Member.builder()
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
                .nickname("테스트사용자닉네임2")
                .username("테스트사용자유저네임2")
                .role(MemberRole.USER)
                .signUpType(SignUpType.KAKAO)
                .profileImage("s3.com/testProfile")
                .recommendLocation("서울")
                .status(MemberStatus.ACTIVE)
                .build();
    }

    public Story createStory(Member member) {
        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("imgUrl1");
        imgUrls.add("imgUrl2");
        imgUrls.add("imgUrl3");

        return Story.builder()
                .title("testStoryTitle")
                .member(member)
                .content("testStoryContent")
                .longitude(1.0)
                .latitude(1.0)
                .city("city")
                .isHidden(false)
                .thumbnailImg("thumbURL")
                .imgUrls(imgUrls)
                .build();
    }


    public Comment createComment(Member member, Story story) {

        return Comment.builder()
                .member(member)
                .content("testCommentContent")
                .story(story)
                .build();
    }

    public Report createStoryReport(Member member, Story story) {

        return Report.builder()
                .member(member)
                .reportType(ReportType.STORY)
                .story(story)
                .reportReason(ReportReason.SPAMMARKET)
                .build();
    }

    public Report createCommentReport(Member member, Comment comment) {
        return Report.builder()
                .member(member)
                .reportType(ReportType.COMMENT)
                .comment(comment)
                .reportReason(ReportReason.SPAMMARKET)
                .build();
    }

    public Festival createFestival(String title, String city, double lat, double logt, LocalDate startDate, LocalDate endDate) {
        return Festival.builder()
                .contentId((long) (Math.random() * 1000))
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
