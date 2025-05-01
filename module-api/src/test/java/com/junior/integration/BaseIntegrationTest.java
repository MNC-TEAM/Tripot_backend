package com.junior.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.config.SecurityConfig;
import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=local")
@Transactional
@Import(SecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BaseIntegrationTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

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

    protected Member createActiveTestMember(String username) {
        return Member.builder()
                .id(2L)
                .nickname("테스트사용자닉네임")
                .username(username)
                .role(MemberRole.USER)
                .signUpType(SignUpType.KAKAO)
                .profileImage("s3.com/testProfile")
                .recommendLocation("서울")
                .status(MemberStatus.ACTIVE)
                .build();
    }

    protected Member createAdmin() {
        return Member.builder()
                .id(3L)
                .nickname("테스트관리자닉네임")
                .username("테스트관리자유저네임")
                .role(MemberRole.ADMIN)
                .signUpType(SignUpType.USERNAME)
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

    protected MockMultipartFile createMockMultipartFile() {
        MockMultipartFile profileImg = new MockMultipartFile(
                "profileimg",
                "profiles.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );

        return profileImg;

    }

    protected Story createStory(Member member) {
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


    protected Comment createComment(Member member, Story story) {

        return Comment.builder()
                .member(member)
                .content("content")
                .story(story)
                .build();
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

    protected FestivalLike createFestivalLike(Member member, Festival festival) {
        return FestivalLike.builder()
                .member(member)
                .festival(festival)
                .build();
    }
}
