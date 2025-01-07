package com.junior.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.domain.member.Member;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.story.StoryRepository;
import com.junior.security.WithMockCustomAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentAdminIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

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

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);


        Story story = createStory(activeTestMember);

        storyRepository.save(story);

        for (int i = 1; i <= 18; i++) {
            Comment comment = createComment(activeTestMember, story);

            commentRepository.save(comment);
        }

    }


    @Test
    @DisplayName("관리자 댓글 조회 기능이 정상 동작해야 함")
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
                .andExpect(jsonPath("$.customCode").value("COMMENT-SUCCESS-0002"))
                .andExpect(jsonPath("$.customMessage").value("댓글 불러오기 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(18));

    }

}
