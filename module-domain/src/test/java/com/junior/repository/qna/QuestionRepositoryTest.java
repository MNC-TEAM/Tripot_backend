package com.junior.repository.qna;

import com.junior.domain.admin.Qna;
import com.junior.domain.member.Member;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.QnaAdminDto;
import com.junior.dto.qna.QnaUserDto;
import com.junior.dto.qna.QuestionResponse;
import com.junior.repository.BaseRepositoryTest;
import com.junior.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void init() throws InterruptedException {

        Member testMember = createActiveTestMember();
        Member testMember2 = createActiveTestMember2();

        memberRepository.save(testMember);
        memberRepository.save(testMember2);

        for (int i = 1; i <= 100; i++) {

            Question question = Question.builder()
                    .title("title " + i)
                    .content("content " + i)
                    .imgUrl("imgurl.com")
                    .member(i % 2 == 1 ? testMember : testMember2)
                    .build();

            //원활한 정렬을 위해 sleep
            Thread.sleep(5);

            questionRepository.save(question);

        }

    }



    @Test
    @DisplayName("질문글 조회 - 무한스크롤 및 회원 필터링이 구현이 정상적으로 동작해야 함")
    public void findQuestionSlice() throws Exception {
        //given
        Long cursorId = 91L;
        int size = 5;

        Member testMember = memberRepository.findById(1L).orElseThrow(RuntimeException::new);


        PageRequest pageRequest = PageRequest.of(0, size);
        //when
        Slice<QuestionResponse> question = questionRepository.findQuestion(testMember, cursorId, pageRequest);
        List<QuestionResponse> content = question.getContent();

        //then
        assertThat(content.size()).isEqualTo(5);


        /**
         * expected: title 89, title 87, title 85, title 83, title 81
         */
        for (int i = 0; i < 5; i++) {
            assertThat(content.get(i).title()).isEqualTo("title " + (89 - i * 2));
        }





    }


    @Test
    @DisplayName("질문글 조회 - 무한스크롤 처음이 정상적으로 동작해야 함")
    public void findQuestionSliceFirst() throws Exception {
        //given
        Long cursorId = null;
        int size = 5;

        Member testMember = memberRepository.findById(1L).orElseThrow(RuntimeException::new);


        PageRequest pageRequest = PageRequest.of(0, size);
        //when
        Slice<QuestionResponse> question = questionRepository.findQuestion(testMember, cursorId, pageRequest);
        List<QuestionResponse> content = question.getContent();

        //then
        assertThat(content.size()).isEqualTo(5);


        /**
         * expected: title 99, title 97, title 95, title 93, title 91
         */
        for (int i = 0; i < 5; i++) {
            assertThat(content.get(i).title()).isEqualTo("title " + (99 - i * 2));
        }



    }

    @Test
    @DisplayName("질문글 조회 - 무한스크롤 마지막이 정상적으로 동작해야 함")
    public void findQuestionSliceLast() throws Exception {
        //given
        Long cursorId = 7L;
        int size = 5;

        Member testMember = memberRepository.findById(1L).orElseThrow(RuntimeException::new);


        PageRequest pageRequest = PageRequest.of(0, size);
        //when
        Slice<QuestionResponse> question = questionRepository.findQuestion(testMember, cursorId, pageRequest);
        List<QuestionResponse> content = question.getContent();

        //then
        assertThat(content.size()).isEqualTo(3);


        /**
         * expected: title 5, title 3, title 1
         */
        for (int i = 0; i < 3; i++) {
            assertThat(content.get(i).title()).isEqualTo("title " + (5 - i * 2));
        }



    }


}