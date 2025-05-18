package com.junior.service.comment;

import com.junior.domain.member.Member;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.dto.comment.CommentAdminDto;
import com.junior.page.PageCustom;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.service.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


class CommentAdminServiceTest extends BaseServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    @DisplayName("관리자용 댓글 조회 - 기능이 정상 동작해야 함")
    void findComment() {

        //given

        Member member = createActiveTestMember();

        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("imgUrl1");
        imgUrls.add("imgUrl2");
        imgUrls.add("imgUrl3");

        Story story = Story.builder()
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

        Comment comment = Comment.builder()
                .member(member)
                .content("testCommentContent")
                .story(story)
                .build();

        List<Comment> comments = new ArrayList<>();

        comments.add(comment);

        PageRequest beforePageRequest = PageRequest.of(1, 15);
        PageRequest afterPageRequest = PageRequest.of(0, 15);

        given(commentRepository.findAllByOrderByIdDesc(any(Pageable.class))).willReturn(new PageImpl<>(comments, afterPageRequest, 1L));

        //createdBy는 BaseEntity의 변수이므로 정상적으로 등록되었다 가정

        //when
        PageCustom<CommentAdminDto> result = commentAdminService.findComment(beforePageRequest);

        //then
        List<CommentAdminDto> content = result.getContent();

        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).content()).isEqualTo("testCommentContent");
    }

    @Test
    @DisplayName("관리자용 댓글 조회 - 탈퇴 회원의 댓글을 조회할 경우 닉네임란에 '탈퇴 회원'이 있어야 함")
    void findCommentOfWithdrewUser() {

        //given

        Member member = createWithdrewMember();

        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("imgUrl1");
        imgUrls.add("imgUrl2");
        imgUrls.add("imgUrl3");

        Story story = Story.builder()
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

        Comment comment = Comment.builder()
                .member(member)
                .content("testCommentContent")
                .story(story)
                .build();

        List<Comment> comments = new ArrayList<>();

        comments.add(comment);

        PageRequest beforePageRequest = PageRequest.of(1, 15);
        PageRequest afterPageRequest = PageRequest.of(0, 15);

        given(commentRepository.findAllByOrderByIdDesc(any(Pageable.class))).willReturn(new PageImpl<>(comments, afterPageRequest, 1L));

        //createdBy는 BaseEntity의 변수이므로 정상적으로 등록되었다 가정

        //when
        PageCustom<CommentAdminDto> result = commentAdminService.findComment(beforePageRequest);

        //then
        List<CommentAdminDto> content = result.getContent();

        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).content()).isEqualTo("testCommentContent");
        assertThat(content.get(0).createdNickname()).isEqualTo("탈퇴회원");
    }

    @Test
    @DisplayName("관리자용 댓글 삭제 - 기능이 정상 동작해야 함")
    public void deleteComment() throws Exception {
        //given

        Member member = createActiveTestMember();

        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("imgUrl1");
        imgUrls.add("imgUrl2");
        imgUrls.add("imgUrl3");

        Story story = Story.builder()
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

        Comment comment = Comment.builder()
                .member(member)
                .content("testCommentContent")
                .story(story)
                .build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        //when
        commentAdminService.deleteComment(1L);

        //then
        Comment result = commentRepository.findById(1L).orElseThrow(() -> new RuntimeException());

        assertThat(result.getIsDeleted()).isTrue();

    }


}