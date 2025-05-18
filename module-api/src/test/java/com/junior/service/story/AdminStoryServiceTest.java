package com.junior.service.story;

import com.junior.domain.story.Story;
import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.dto.story.AdminStoryDto;
import com.junior.page.PageCustom;
import com.junior.repository.story.StoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AdminStoryServiceTest {

    @Mock
    private StoryRepository storyRepository;

    @InjectMocks
    private AdminStoryService adminStoryService;

    @Test
    @DisplayName("관리자 전용 스토리 페이지 조회가 정상적으로 이루어져야 함")
    void findStory() {

        //given
        List<AdminStoryDto> storyDtos = new ArrayList<>();

        AdminStoryDto storyDto = AdminStoryDto.builder()
                .id(1L)
                .title("title")
                .city("서울")
                .isDeleted(false)
                .createdNickname("nickname")
                .build();

        storyDtos.add(storyDto);


        PageRequest beforePageRequest = PageRequest.of(1, 15);
        PageRequest afterPageRequest = PageRequest.of(0, 15);

        given(storyRepository.findAllStories(any(Pageable.class), anyString())).willReturn(new PageImpl<>(storyDtos, afterPageRequest, storyDtos.size()));

        //when
        PageCustom<AdminStoryDto> result = adminStoryService.findStory(beforePageRequest, "");

        //then
        List<AdminStoryDto> content = result.getContent();

        assertThat(content.get(0).city()).isEqualTo("서울");
        assertThat(content.get(0).isDeleted()).isFalse();

    }

    @Test
    @DisplayName("스토리 상세 조회 기능이 정상 동작해야 함")
    void findStoryDetail() {

        //given
        Story story = Story.builder()
                .id(1L)
                .title("title")
                .content("content")
                .thumbnailImg("thumbnail")
                .latitude(-10.0)
                .longitude(10.0)
                .city("서울")
                .likeCnt(3L)
                .imgUrls(new ArrayList<>())
                .build();

        given(storyRepository.findById(anyLong())).willReturn(Optional.ofNullable(story));

        //when
        AdminStoryDetailDto result = adminStoryService.findStoryDetail(1L);

        //then
        assertThat(result.city()).isEqualTo("서울");
        assertThat(result.likeCnt()).isEqualTo(3L);
        assertThat(result.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("스토리 삭제 기능이 정상 동작해야 함")
    void deleteStory() {

        //given
        Story story = Story.builder()
                .id(1L)
                .title("title")
                .content("content")
                .thumbnailImg("thumbnail")
                .latitude(-10.0)
                .longitude(10.0)
                .city("서울")
                .likeCnt(3L)
                .imgUrls(new ArrayList<>())
                .build();

        given(storyRepository.findById(anyLong())).willReturn(Optional.ofNullable(story));

        //when, then
        adminStoryService.deleteStory(1L);

        //then
        Story result = storyRepository.findById(1L).orElseThrow(() -> new RuntimeException());

        assertThat(result.getIsDeleted()).isTrue();

    }
}