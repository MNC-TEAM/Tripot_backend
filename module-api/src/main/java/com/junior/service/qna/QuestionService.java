package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.CreateQuestionImgRequest;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.qna.QuestionRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
