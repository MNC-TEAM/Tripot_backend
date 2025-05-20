package com.junior.integration.comment;


import com.junior.domain.member.Member;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.story.StoryRepository;
import com.junior.security.WithMockCustomAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentAdminIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void init() {
        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();
        Member activeTestMember2 = createActiveTestMember2();
        Member withdrewTestMember = createWithdrewMember();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);
        memberRepository.save(withdrewTestMember);


        Story story = createStory(activeTestMember);

        storyRepository.save(story);

        for (int i = 1; i <= 18; i++) {
            Comment comment = createComment(activeTestMember, story);

            commentRepository.save(comment);
        }

        Comment withdrewComment = createComment(withdrewTestMember, story);
        commentRepository.save(withdrewComment);

    }


    @Test
    @DisplayName("관리자 댓글 조회 - 기능이 정상 동작해야 함")
    @WithMockCustomAdmin
    public void findComment() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/comments")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.COMMENT_READ_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.COMMENT_READ_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(19))
                .andExpect(jsonPath("$.data.content[1].id").value(18))
                .andExpect(jsonPath("$.data.content[0].createdNickname").value("탈퇴회원"))
                .andExpect(jsonPath("$.data.content[1].createdNickname").value("테스트사용자닉네임"));

    }

    @Test
    @DisplayName("관리자 댓글 삭제 - 기능이 정상 동작해야 함")
    @WithMockCustomAdmin
    public void deleteComment() throws Exception {
        //given
        Long targetCommentId = 10L;

        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/admin/comments/{comment_id}", targetCommentId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.COMMENT_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.COMMENT_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Comment comment = commentRepository.findById(10L).orElseThrow(() -> new RuntimeException());

        assertThat(comment.getIsDeleted()).isTrue();

    }

}
