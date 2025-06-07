package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.domain.qna.Answer;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.CreateAnswerRequest;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.QuestionException;
import com.junior.exception.StatusCode;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.qna.AnswerRepository;
import com.junior.repository.qna.QuestionRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class AnswerServiceTest extends BaseServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AnswerService answerService;

    @Test
    @DisplayName("답변 등록 - 답변 저장이 정상적으로 이루어져야 함")
    void save() throws Exception {
        //given
        CreateAnswerRequest createAnswerRequest = CreateAnswerRequest.builder()
                .title("title")
                .content("content")
                .build();

        Member admin = createAdmin();
        Member customer = createActiveTestMember();
        Long questionId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);

        Question question = Question.builder()
                .id(questionId)
                .title("title")
                .content("question")
                .imgUrl("s3.com/img")
                .member(customer)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(admin));
        given(questionRepository.findById(anyLong())).willReturn(Optional.ofNullable(question));

        //when
        answerService.save(principal, questionId, createAnswerRequest);

        //then
        verify(answerRepository).save(ArgumentMatchers.any(Answer.class));

        assertThat(question.getIsAnswered()).isTrue();

    }

    @Test
    @DisplayName("답변 등록 - 회원을 찾을 수 없을 경우 예외를 발생시켜야 함")
    void failToSaveIfMemberNotValid() throws Exception {
        //given
        CreateAnswerRequest createAnswerRequest = CreateAnswerRequest.builder()
                .title("title")
                .content("content")
                .build();

        Member admin = createAdmin();
        Long questionId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);


        //when, then
        assertThatThrownBy(() -> answerService.save(principal, questionId, createAnswerRequest))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("답변 등록 - 회원을 찾을 수 없을 경우 예외를 발생시켜야 함")
    void failToSaveIfQuestionNotExists() throws Exception {
        //given
        CreateAnswerRequest createAnswerRequest = CreateAnswerRequest.builder()
                .title("title")
                .content("content")
                .build();

        Member admin = createAdmin();
        Long questionId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(admin));


        //when, then
        assertThatThrownBy(() -> answerService.save(principal, questionId, createAnswerRequest))
                .isInstanceOf(QuestionException.class)
                .hasMessageContaining(StatusCode.QUESTION_NOT_FOUND.getCustomMessage());


    }
}