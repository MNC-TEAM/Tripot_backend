package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.*;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.PermissionException;
import com.junior.exception.QuestionException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.qna.QuestionRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final S3Service s3Service;


    @Transactional
    public void save(UserPrincipal principal, CreateQuestionRequest createQuestionRequest) {

        Member author = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (author.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Question question = Question.builder()
                .title(createQuestionRequest.title())
                .content(createQuestionRequest.content())
                .imgUrl(createQuestionRequest.imgUrl())
                .member(author)
                .build();

        questionRepository.save(question);
    }


    public String uploadQuestionImg(UserPrincipal principal, MultipartFile newQuestionImg, CreateQuestionImgRequest createQuestionImgRequest) {
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }


        log.info("[{}] target: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername());

        //이미지를 없앨 경우 빈 문자열을 리턴
        String questionImgUrl = "";

        //사진을 업데이트 하기 위해 기존 이미지를 삭제
        if (createQuestionImgRequest.oldImgUrl() != null && !createQuestionImgRequest.oldImgUrl().isEmpty()) {
            log.info("[{}] 기존 사진 제거 target: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername());
            s3Service.deleteQuestionImg(createQuestionImgRequest.oldImgUrl());
        }

        if (!newQuestionImg.isEmpty()) {
            questionImgUrl = s3Service.saveQuestionImage(newQuestionImg);
        }

        return questionImgUrl;

    }


    public Slice<QuestionResponse> find(UserPrincipal principal, Long cursorId, int size) {
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Pageable pageable = PageRequest.of(0, size);

        return questionRepository.findQuestion(member, cursorId, pageable);

    }

    public PageCustom<QuestionAdminResponse> findQuestionAdmin(Pageable pageable) {


        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<QuestionAdminResponse> result = questionRepository.findQuestion(pageRequest);
        return new PageCustom<>(result.getContent(), result.getPageable(), result.getTotalElements());
    }

    public QuestionDetailResponse findDetail(UserPrincipal principal, Long questionId) {
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new QuestionException(StatusCode.QUESTION_NOT_FOUND));

        if (!question.getMember().equals(member) && !member.getRole().equals(MemberRole.ADMIN)) {
            throw new QuestionException(StatusCode.QUESTION_FORBIDDEN);
        }

        return QuestionDetailResponse.from(question);
    }

    @Transactional
    public void update(UserPrincipal principal, Long questionId, UpdateQuestionRequest updateQuestionRequest) {

        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new QuestionException(StatusCode.QUESTION_NOT_FOUND));

        if (!question.getMember().equals(member)) {
            throw new PermissionException(StatusCode.INVALID_MEMBER);
        }

        question.update(updateQuestionRequest);
    }

    @Transactional
    public void delete(UserPrincipal principal, Long questionId) {
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER_STATUS);
        }

        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new QuestionException(StatusCode.QUESTION_NOT_FOUND));

        if (!question.getMember().equals(member)) {
            throw new PermissionException(StatusCode.INVALID_MEMBER);
        }


        question.delete();

    }

}
