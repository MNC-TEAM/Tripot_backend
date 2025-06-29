package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.qna.Answer;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.AnswerResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public void save(UserPrincipal principal, Long questionId, CreateAnswerRequest createAnswerRequest) {

        Member member = memberRepository.findById(principal.getMember().getId())
                .orElseThrow(() -> new NotValidMemberException(StatusCode.INVALID_MEMBER));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(StatusCode.QUESTION_NOT_FOUND));

        Answer answer = Answer.builder()
                .title(createAnswerRequest.title())
                .content(createAnswerRequest.content())
                .member(member)
                .build();

        answerRepository.save(answer);

        question.setAnswer(answer);

    }


    public AnswerResponse find(UserPrincipal principal, Long questionId) {
        Member member = memberRepository.findById(principal.getMember().getId())
                .orElseThrow(() -> new NotValidMemberException(StatusCode.INVALID_MEMBER));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(StatusCode.QUESTION_NOT_FOUND));

        //질문자와 관리자만 답변을 열람할 수 있음
        if (!member.equals(question.getMember()) && !member.getRole().equals(MemberRole.ADMIN)) {
            throw new AnswerException(StatusCode.ANSWER_FORBIDDEN);
        }

        Answer answer = question.getAnswer();

        //답변이 없는 경우 리턴하지 않음
        if (answer == null) {
            return null;
        }

        return AnswerResponse.from(answer);

    }

    @Transactional
    public void update(UserPrincipal principal, Long answerId, UpdateAnswerRequest updateAnswerRequest) {

        Member member = memberRepository.findById(principal.getMember().getId())
                .orElseThrow(() -> new NotValidMemberException(StatusCode.INVALID_MEMBER));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new QuestionException(StatusCode.ANSWER_NOT_FOUND));

        answer.update(updateAnswerRequest);


    }

    @Transactional
    public void delete(UserPrincipal principal, Long questionId) {

        Member member = memberRepository.findById(principal.getMember().getId())
                .orElseThrow(() -> new NotValidMemberException(StatusCode.INVALID_MEMBER));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(StatusCode.QUESTION_NOT_FOUND));

        Answer answer = question.getAnswer();

        if (answer == null) {
            throw new AnswerException(StatusCode.ANSWER_NOT_FOUND);
        }



        answer.delete();

        //연관관계 해제
        question.deleteAnswer();


    }


}
