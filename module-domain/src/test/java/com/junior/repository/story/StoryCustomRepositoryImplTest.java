package com.junior.repository.story;

import com.junior.TestConfig;
import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.domain.story.Story;
import com.junior.dto.story.*;
import com.junior.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/*
    @DirtiesContext
    테스트가 끝날 때마다 context 를 초기화하는 어노테이션
    AFTER_EACH_TEST_METHOD : 테스트가 끝난 후 context 를 초기화
    BEFORE, AFTER 등 다양하게 있음
 */

@DataJpaTest
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StoryCustomRepositoryImplTest {
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    MemberRepository memberRepository;

    public Story createStory(Member member, String title, String city) {

        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("imgUrl1");
        imgUrls.add("imgUrl2");
        imgUrls.add("imgUrl3");

        CreateStoryDto createStoryDto = CreateStoryDto.builder()
                .title(title)
                .content("content")
                .longitude(1.0)
                .latitude(1.0)
                .city(city)
                .isHidden(false)
                .thumbnailImg("thumbURL")
                .imgUrls(imgUrls)
                .build();

        return Story.from(member, createStoryDto);
    }

    public Member createMember(String nickname) {
        return Member.builder()
                .nickname(nickname)
                .username("username")
                .password("password")
                .profileImage("profilePath")
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .recommendLocation("DEAJEON")
                .signUpType(SignUpType.KAKAO)
                .build();
    }

    @Test
    @DisplayName("Story를 저장할 때 아무런 예외가 발생하지 않는다.")
    public void saveStoryTest() {

        Member member = createMember("nickname");

        Story story = createStory(member, "title", "city");

        memberRepository.save(member);

        Assertions.assertThatCode(() -> storyRepository.save(story)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("단일 story를 조회할 수 있다.")
    public void getStoryByIdTest() {
        Member member = createMember("nickname");
        Story story = createStory(member, "title1", "DAEJEON");

        memberRepository.save(member);
        storyRepository.save(story);

        Story findStory = storyRepository.findById(1L).orElseThrow(RuntimeException::new);

        Assertions.assertThat(findStory).isEqualTo(story);
        Assertions.assertThat(findStory.getImgUrls().get(0)).isEqualTo("imgUrl1");
        Assertions.assertThat(findStory.getImgUrls().get(1)).isEqualTo("imgUrl2");
        Assertions.assertThat(findStory.getImgUrls().get(2)).isEqualTo("imgUrl3");
    }

    @Test
    @DisplayName("story를 수정할 수 있다.")
    public void editStoryTest() {
        Member member = createMember("nickname");
        Story story = createStory(member, "title1", "DAEJEON");

        memberRepository.save(member);
        storyRepository.save(story);

        CreateStoryDto createStoryDto = CreateStoryDto.builder()
                .title("editStory")
                .content("editContent")
                .city("seoul")
                .thumbnailImg("editThumb")
                .latitude(1.0)
                .longitude(1.0)
                .isHidden(true)
                .build();

        Story findStory = storyRepository.findStoryByIdAndMember(1L, member).orElseThrow();

        findStory.updateStory(createStoryDto);

        Story editStory = storyRepository.findStoryByIdAndMember(1L, member).orElseThrow();

        Assertions.assertThat(editStory.getTitle()).isEqualTo("editStory");
        Assertions.assertThat(editStory.getContent()).isEqualTo("editContent");
        Assertions.assertThat(editStory.getCity()).isEqualTo("seoul");
    }

    @Test
    @DisplayName("회원이 보고 있는 지도를 기준으로 그 회원이 작성한 story를 최신 순으로 가져올 수 있다.")
    public void findStoriesByMemberAndMap() {
        Member member = createMember("nickname1");
        memberRepository.save(member);

        GeoPointDto geoPointLt = GeoPointDto.builder()
                .latitude(0.0)
                .longitude(10.0)
                .build();

        GeoPointDto geoPointRb = GeoPointDto.builder()
                .latitude(10.0)
                .longitude(0.0)
                .build();

        Story inside1 = Story.createStory()
                .title("inside1")
                .member(member)
                .content("content")
                .longitude(5.0)
                .latitude(5.0)
                .city("city")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story inside2 = Story.createStory()
                .title("inside2")
                .member(member)
                .content("content")
                .longitude(5.0)
                .latitude(5.0)
                .city("city")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story outside1 = Story.createStory()
                .title("outside1")
                .member(member)
                .content("content")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("city")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story outside2 = Story.createStory()
                .title("outside2")
                .member(member)
                .content("content")
                .longitude(20.0)
                .latitude(20.0)
                .city("city")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        storyRepository.save(inside1);
        storyRepository.save(inside2);
        storyRepository.save(outside1);
        storyRepository.save(outside2);

        List<ResponseStoryListDto> stories = storyRepository.findStoryByMap(member, geoPointLt, geoPointRb);

        Assertions.assertThat(stories.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("회원이 지역과 검색어를 기반으로 스토리를 조회할 수 있다.")
    public void findStoriesByMemberAndCityAndSearch() {
        Member member = createMember("nickname1");
        memberRepository.save(member);

        Story hasSearchStory = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("city")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story hasNotSearchStory = Story.createStory()
                .title("title")
                .member(member)
                .content("content")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("city")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        storyRepository.save(hasSearchStory);
        storyRepository.save(hasNotSearchStory);

        Pageable pageable = PageRequest.of(0, 3);

        Slice<ResponseStoryListDto> stories = storyRepository.findStoriesByMemberAndCityAndSearch(null, pageable, member, null, "filter");
        List<ResponseStoryListDto> contents = stories.getContent();

        Assertions.assertThat(contents.size()).isEqualTo(1);
        Assertions.assertThat(contents.get(0).title()).isEqualTo("filterTitle");

        Slice<ResponseStoryListDto> stories1 = storyRepository.findStoriesByMemberAndCityAndSearch(null, pageable, member, "city", null);
        List<ResponseStoryListDto> contents1 = stories1.getContent();

        Assertions.assertThat(contents1.size()).isEqualTo(2);
        Assertions.assertThat(contents1.get(0).title()).isEqualTo("title");
        Assertions.assertThat(contents1.get(1).title()).isEqualTo("filterTitle");
    }

    @Test
    @DisplayName("지역별 스토리 수를 사전 순으로 정렬해서 얻을 수 있다.")
    public void getStoryCntByCityTest() {
        Member member = createMember("nickname");
        memberRepository.save(member);

        Story story1 = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("서울")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story story2 = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("대전")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story story3 = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("대구")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story story4 = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("부산")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story story5 = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("서울")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        Story story6 = Story.createStory()
                .title("filterTitle")
                .member(member)
                .content("filterContent")
                .longitude(-10.0)
                .latitude(-10.0)
                .city("대전")
                .isHidden(true)
                .thumbnailImg("thumbURL")
                .build();

        storyRepository.save(story1);
        storyRepository.save(story2);
        storyRepository.save(story3);
        storyRepository.save(story4);
        storyRepository.save(story5);
        storyRepository.save(story6);

        List<ResponseStoryCntByCityDto> storyCntByCity = storyRepository.getStoryCntByCity(member);

        System.out.println("storyCntByCity = " + storyCntByCity);

        Assertions.assertThat(storyCntByCity.size()).isEqualTo(4);

        Assertions.assertThat(storyCntByCity.get(0).city()).isEqualTo("대구");
        Assertions.assertThat(storyCntByCity.get(0).cnt()).isEqualTo(1);

        Assertions.assertThat(storyCntByCity.get(1).city()).isEqualTo("대전");
        Assertions.assertThat(storyCntByCity.get(1).cnt()).isEqualTo(2);

        Assertions.assertThat(storyCntByCity.get(2).city()).isEqualTo("부산");
        Assertions.assertThat(storyCntByCity.get(2).cnt()).isEqualTo(1);

        Assertions.assertThat(storyCntByCity.get(3).city()).isEqualTo("서울");
        Assertions.assertThat(storyCntByCity.get(3).cnt()).isEqualTo(2);
    }

    @Test
    @DisplayName("키워드 검색을 통한 페이징된 스토리 리스트를 불러올 수 있어야 한다.")
    public void findAllStoriesAdmin() throws Exception {
        //given
        Member member = createMember("nickname");
        memberRepository.save(member);

        //총 18개의 데이터 저장
        for (int i = 0; i < 3; i++) {
            Story story1 = Story.createStory()
                    .title("filterTitle")
                    .member(member)
                    .content("filterContent")
                    .longitude(-10.0)
                    .latitude(-10.0)
                    .city("서울")
                    .isHidden(true)
                    .thumbnailImg("thumbURL")
                    .build();

            Story story2 = Story.createStory()
                    .title("filterTitle")
                    .member(member)
                    .content("filterContent")
                    .longitude(-10.0)
                    .latitude(-10.0)
                    .city("대전")
                    .isHidden(true)
                    .thumbnailImg("thumbURL")
                    .build();

            Story story3 = Story.createStory()
                    .title("filterTitle")
                    .member(member)
                    .content("filterContent")
                    .longitude(-10.0)
                    .latitude(-10.0)
                    .city("대구")
                    .isHidden(true)
                    .thumbnailImg("thumbURL")
                    .build();

            Story story4 = Story.createStory()
                    .title("filterTitle")
                    .member(member)
                    .content("filterContent")
                    .longitude(-10.0)
                    .latitude(-10.0)
                    .city("부산")
                    .isHidden(true)
                    .thumbnailImg("thumbURL")
                    .build();

            Story story5 = Story.createStory()
                    .title("filterTitle")
                    .member(member)
                    .content("filterContent")
                    .longitude(-10.0)
                    .latitude(-10.0)
                    .city("서울")
                    .isHidden(true)
                    .thumbnailImg("thumbURL")
                    .build();

            Story story6 = Story.createStory()
                    .title("filterTitle")
                    .member(member)
                    .content("filterContent")
                    .longitude(-10.0)
                    .latitude(-10.0)
                    .city("대전")
                    .isHidden(true)
                    .thumbnailImg("thumbURL")
                    .build();

            storyRepository.save(story1);
            storyRepository.save(story2);
            storyRepository.save(story3);
            storyRepository.save(story4);
            storyRepository.save(story5);
            storyRepository.save(story6);
        }

        PageRequest pageRequest = PageRequest.of(0, 15);

        //when
        Page<AdminStoryDto> resultPage = storyRepository.findAllStories(pageRequest, "");

        //then
        List<AdminStoryDto> content = resultPage.getContent();

        Assertions.assertThat(content.size()).isEqualTo(15);


        //id가 가장 높은 두 개의 스토리에 대해서 확인
        Assertions.assertThat(content.get(0).city()).isEqualTo("대전");
        Assertions.assertThat(content.get(0).id()).isEqualTo(18);
        Assertions.assertThat(content.get(0).isDeleted()).isFalse();
        Assertions.assertThat(content.get(0).createdNickname()).isEqualTo("nickname");
        Assertions.assertThat(content.get(0).createdDateTime()).isNotNull();
        Assertions.assertThat(content.get(0).deletedDateTime()).isNull();           //삭제되지 않았으므로 미표기
        Assertions.assertThat(content.get(1).city()).isEqualTo("서울");
        Assertions.assertThat(content.get(1).id()).isEqualTo(17);


    }


}