package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.domain.qna.Answer;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.CreateAnswerRequest;
import com.junior.dto.qna.UpdateAnswerRequest;
import com.junior.exception.AnswerException;
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
    @DisplayName("답변 등록 - 질문글을 찾을 수 없을 경우 예외를 발생시켜야 함")
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

    @Test
    @DisplayName("답변 수정 - 답변 수정이 정상적으로 이루어져야 함")
    void update() throws Exception {
        //given
        UpdateAnswerRequest updateAnswerRequest = UpdateAnswerRequest.builder()
                .title("new title")
                .content("new answer")
                .build();

        Member admin = createAdmin();
        Member customer = createActiveTestMember();
        Long answerId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);

        Answer answer = Answer.builder()
                .id(answerId)
                .title("title")
                .content("answer")
                .member(customer)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(admin));
        given(answerRepository.findById(anyLong())).willReturn(Optional.ofNullable(answer));

        //when
        answerService.update(principal, answerId, updateAnswerRequest);

        //then

        assertThat(answer.getTitle()).isEqualTo("new title");
        assertThat(answer.getContent()).isEqualTo("new answer");

    }

    @Test
    @DisplayName("답변 수정 - 회원을 찾을 수 없을 경우 예외를 발생시켜야 함")
    void failToUpdateIfMemberNotValid() throws Exception {
        //given
        UpdateAnswerRequest updateAnswerRequest = UpdateAnswerRequest.builder()
                .title("title")
                .content("content")
                .build();

        Member admin = createAdmin();
        Long questionId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);


        //when, then
        assertThatThrownBy(() -> answerService.update(principal, questionId, updateAnswerRequest))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("답변 수정 - 답변을 찾을 수 없을 경우 예외를 발생시켜야 함")
    void failToUpdateIfAnswerNotExists() throws Exception {
        //given
        UpdateAnswerRequest updateAnswerRequest = UpdateAnswerRequest.builder()
                .title("title")
                .content("content")
                .build();

        Member admin = createAdmin();
        Long answerId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(admin));


        //when, then
        assertThatThrownBy(() -> answerService.update(principal, answerId, updateAnswerRequest))
                .isInstanceOf(QuestionException.class)
                .hasMessageContaining(StatusCode.ANSWER_NOT_FOUND.getCustomMessage());


    }

    @Test
    @DisplayName("답변 삭제 - 답변 삭제가 정상적으로 이루어져야 함")
    void delete() throws Exception {
        //given

        Member admin = createAdmin();
        Member customer = createActiveTestMember();
        Long answerId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);

        Answer answer = Answer.builder()
                .id(answerId)
                .title("title")
                .content("answer")
                .member(customer)
                .build();



        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(admin));
        given(answerRepository.findById(anyLong())).willReturn(Optional.ofNullable(answer));

        //when
        answerService.delete(principal, answerId);

        //then

        assertThat(answer.getIsDeleted()).isTrue();


    }

    @Test
    @DisplayName("답변 삭제 - 회원을 찾을 수 없을 경우 예외를 발생시켜야 함")
    void failToDeleteIfMemberNotValid() throws Exception {
        //given

        Member admin = createAdmin();
        Long questionId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);


        //when, then
        assertThatThrownBy(() -> answerService.delete(principal, questionId))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("답변 삭제 - 질문을 찾을 수 없을 경우 예외를 발생시켜야 함")
    void failToDeleteIfQuestionNotExists() throws Exception {
        //given

        Member admin = createAdmin();
        Long questionId = 1L;

        UserPrincipal principal = new UserPrincipal(admin);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(admin));


        //when, then
        assertThatThrownBy(() -> answerService.delete(principal, questionId))
                .isInstanceOf(AnswerException.class)
                .hasMessageContaining(StatusCode.ANSWER_NOT_FOUND.getCustomMessage());


    }
}