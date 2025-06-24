package com.junior.service.qna;

import com.junior.domain.member.Member;
import com.junior.domain.qna.Question;
import com.junior.dto.qna.*;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.QuestionException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.qna.QuestionRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import com.junior.service.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class QuestionServiceTest extends BaseServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    S3Service s3Service;

    @InjectMocks
    QuestionService questionService;


    @Test
    @DisplayName("문의용 이미지 업로드 - 이미지 업로드 성공 후 해당 주소를 리턴해야 함")
    void uploadQuestionImage() {

        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        MultipartFile newQuestionImg = createMockMultipartFile();
        CreateQuestionImgRequest createQuestionImgRequest = new CreateQuestionImgRequest("");

        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(testMember));
        given(s3Service.saveQuestionImage(newQuestionImg)).willReturn("s3.com/newQuestionImg");

        //when
        String newUrl = questionService.uploadQuestionImg(principal, newQuestionImg, createQuestionImgRequest);

        //then
        assertThat(newUrl).isEqualTo("s3.com/newQuestionImg");

    }

    @Test
    @DisplayName("문의용 이미지 업로드 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToUploadImageIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        MultipartFile profileImage = createMockMultipartFile();
        CreateQuestionImgRequest createQuestionImgRequest = new CreateQuestionImgRequest("");


        //when, then
        assertThatThrownBy(() -> questionService.uploadQuestionImg(principal, profileImage, createQuestionImgRequest))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("문의용 이미지 업로드 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToUploadImageIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        CreateQuestionImgRequest createQuestionImgRequest = new CreateQuestionImgRequest("");
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));
        MultipartFile profileImage = createMockMultipartFile();


        //when, then
        assertThatThrownBy(() -> questionService.uploadQuestionImg(principal, profileImage, createQuestionImgRequest)).isInstanceOf(NotValidMemberException.class);


    }

    @Test
    @DisplayName("문의 질문 업로드 - 저장이 정상적으로 이루어져야 함")
    void saveQuestion() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);

        CreateQuestionRequest createQuestionRequest = CreateQuestionRequest.builder()
                .title("title")
                .content("question")
                .imgUrl("s3.com/question-img")
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));
        //when
        questionService.save(principal, createQuestionRequest);

        //then
        verify(questionRepository).save(any(Question.class));

    }

    @Test
    @DisplayName("문의글 업로드 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToSaveQuestionIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        CreateQuestionRequest createQuestionRequest = CreateQuestionRequest.builder()
                .title("title")
                .content("question")
                .imgUrl("s3.com/question-img")
                .build();


        //when, then
        assertThatThrownBy(() -> questionService.save(principal, createQuestionRequest))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("문의글 업로드 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToSaveQuestionIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        CreateQuestionRequest createQuestionRequest = CreateQuestionRequest.builder()
                .title("title")
                .content("question")
                .imgUrl("s3.com/question-img")
                .build();
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> questionService.save(principal, createQuestionRequest)).isInstanceOf(NotValidMemberException.class);


    }

    @Test
    @DisplayName("문의글 조회 - 정상적으로 조회할 수 있어야 함")
    void find() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        int size = 5;

        PageRequest pageRequest = PageRequest.of(0, size);

        List<QuestionResponse> responses = new ArrayList<>();

        QuestionResponse response = QuestionResponse.builder()
                .id(1L)
                .title("title")
                .content("question")
                .isAnswered(false)
                .build();

        responses.add(response);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));
        given(questionRepository.findQuestion(any(Member.class), anyLong(), any(Pageable.class))).willReturn(new SliceImpl<>(responses, pageRequest, false));

        //when
        Slice<QuestionResponse> result = questionService.find(principal, questionId, size);

        //then
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getNumberOfElements()).isEqualTo(1);

    }

    @Test
    @DisplayName("문의글 조회 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToFindIfMemberNotExists() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        int size = 5;


        //when
        assertThatThrownBy(() -> questionService.find(principal, questionId, size))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());

    }

    @Test
    @DisplayName("문의글 관리자 조회 - 정상적으로 조회할 수 있어야 함")
    void findQuestionAdmin() throws Exception {
        //given

        PageRequest pageRequest = PageRequest.of(1, 10);
        PageRequest afterPageRequest = PageRequest.of(0, 10);

        List<QuestionAdminResponse> responses = new ArrayList<>();

        QuestionAdminResponse response = QuestionAdminResponse.builder()
                .id(1L)
                .title("title")
                .content("question")
                .isAnswered(false)
                .isDeleted(false)
                .build();

        responses.add(response);


        given(questionRepository.findQuestion(afterPageRequest)).willReturn(new PageImpl<>(responses, afterPageRequest, 1));

        //when
        PageCustom<QuestionAdminResponse> result = questionService.findQuestionAdmin(pageRequest);

        //then
        assertThat(result.getPageable().getTotalElements()).isEqualTo(1);
        assertThat(result.getPageable().getNumber()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);

    }

    @Test
    @DisplayName("문의글 상세정보 조회 - 정상적으로 조회할 수 있어야 함")
    void findDetail() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;


        Question question = Question.builder()
                .id(questionId)
                .title("title")
                .content("question")
                .imgUrl("s3.com/img")
                .member(testMember)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));
        given(questionRepository.findByIdAndIsDeletedFalse(anyLong())).willReturn(Optional.ofNullable(question));

        //when
        QuestionDetailResponse result = questionService.findDetail(principal, questionId);

        //then
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.content()).isEqualTo("question");
        assertThat(result.imgUrl()).isEqualTo("s3.com/img");

    }

    @Test
    @DisplayName("문의글 상세정보 조회 - 문의글을 못 찾을 경우 예외를 발생시켜야 함")
    void failToFindDetailIfQuestionNotFound() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;


        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));

        //when, then
        assertThatThrownBy(() -> questionService.findDetail(principal, questionId))
                .isInstanceOf(QuestionException.class)
                .hasMessageContaining(StatusCode.QUESTION_NOT_FOUND.getCustomMessage());

    }


    @Test
    @DisplayName("문의글 상세정보 조회 - 본인 질문글이 아닐 경우 예외를 발생시켜야 함")
    void failToFindDetailIfQuestionIsNotYours() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        Member testMember2 = createActiveTestMember2();
        UserPrincipal principal = new UserPrincipal(testMember2);
        Long questionId = 1L;


        Question question = Question.builder()
                .id(questionId)
                .title("title")
                .content("question")
                .imgUrl("s3.com/img")
                .member(testMember)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember2));
        given(questionRepository.findByIdAndIsDeletedFalse(anyLong())).willReturn(Optional.ofNullable(question));

        //when, then
        assertThatThrownBy(() -> questionService.findDetail(principal, questionId))
                .isInstanceOf(QuestionException.class)
                .hasMessageContaining(StatusCode.QUESTION_FORBIDDEN.getCustomMessage());
    }

    @Test
    @DisplayName("문의 질문 수정 - 수정이 정상적으로 이루어져야 함")
    void updateQuestion() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        UpdateQuestionRequest updateQuestionRequest = UpdateQuestionRequest.builder()
                .title("new title")
                .content("new question")
                .imgUrl("s3.com/question-img")
                .build();

        Question question = Question.builder()
                .id(questionId)
                .title("title")
                .content("question")
                .imgUrl("s3.com/img")
                .member(testMember)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));
        given(questionRepository.findByIdAndIsDeletedFalse(anyLong())).willReturn(Optional.ofNullable(question));

        //when
        questionService.update(principal, questionId, updateQuestionRequest);

        //then
        assertThat(question.getTitle()).isEqualTo("new title");
        assertThat(question.getContent()).isEqualTo("new question");
        assertThat(question.getImgUrl()).isEqualTo("s3.com/question-img");

    }

    @Test
    @DisplayName("문의글 수정 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToUpdateQuestionIfMemberNotFound() {


        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        UpdateQuestionRequest updateQuestionRequest = UpdateQuestionRequest.builder()
                .title("new title")
                .content("new question")
                .imgUrl("s3.com/question-img")
                .build();


        //when, then
        assertThatThrownBy(() -> questionService.update(principal, questionId, updateQuestionRequest))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("문의글 수정 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToUpdateQuestionIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        UpdateQuestionRequest updateQuestionRequest = UpdateQuestionRequest.builder()
                .title("new title")
                .content("new question")
                .imgUrl("s3.com/question-img")
                .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> questionService.update(principal, questionId, updateQuestionRequest)).isInstanceOf(NotValidMemberException.class);


    }

    @Test
    @DisplayName("문의글 수정 - 해당 문의글을 찾지 못했을 경우 예외를 발생시켜야 함")
    void failToUpdateQuestionIfQuestionNotFound() {


        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        UpdateQuestionRequest updateQuestionRequest = UpdateQuestionRequest.builder()
                .title("new title")
                .content("new question")
                .imgUrl("s3.com/question-img")
                .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> questionService.update(principal, questionId, updateQuestionRequest)).isInstanceOf(QuestionException.class)
                .hasMessageContaining(StatusCode.QUESTION_NOT_FOUND.getCustomMessage());


    }


    @Test
    @DisplayName("문의 질문 삭제 - soft delete가 정상적으로 이루어져야 함")
    void deleteQuestion() throws Exception {
        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;

        Question question = Question.builder()
                .id(questionId)
                .title("title")
                .content("question")
                .imgUrl("s3.com/img")
                .member(testMember)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));
        given(questionRepository.findByIdAndIsDeletedFalse(anyLong())).willReturn(Optional.ofNullable(question));

        //when
        questionService.delete(principal, questionId);

        //then
        assertThat(question.getIsDeleted()).isTrue();

    }

    @Test
    @DisplayName("문의글 삭제 - 회원을 찾을 수 없을 경우 예외를 처리해야 함")
    void failToDeleteQuestionIfMemberNotFound() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;


        //when, then
        assertThatThrownBy(() -> questionService.delete(principal, questionId))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("문의글 삭제 - ACTIVE 상태가 아닌 회원은 정보 조회를 할 수 없음")
    void failToDeleteQuestionIfMemberIsNotActivate() {


        //given
        Member testMember = createPreactiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> questionService.delete(principal, questionId)).isInstanceOf(NotValidMemberException.class);


    }

    @Test
    @DisplayName("문의글 삭제 - 해당 문의글을 찾지 못했을 경우 예외를 발생시켜야 함")
    void failToDeleteQuestionIfQuestionNotFound() {


        //given
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);
        Long questionId = 1L;
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));


        //when, then
        assertThatThrownBy(() -> questionService.delete(principal, questionId)).isInstanceOf(QuestionException.class)
                .hasMessageContaining(StatusCode.QUESTION_NOT_FOUND.getCustomMessage());


    }


}