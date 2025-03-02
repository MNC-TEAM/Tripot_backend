package com.junior.service.comment;

import com.junior.domain.member.Member;
import com.junior.domain.notification.NotificationType;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.dto.comment.CreateCommentDto;
import com.junior.dto.comment.ResponseChildCommentDto;
import com.junior.dto.comment.ResponseMyCommentDto;
import com.junior.dto.comment.ResponseParentCommentDto;
import com.junior.event.CommentFcmEvent;
import com.junior.exception.CommentNotFoundException;
import com.junior.exception.StatusCode;
import com.junior.exception.StoryNotFoundException;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.story.StoryRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;

    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void saveComment(UserPrincipal userPrincipal, CreateCommentDto createCommentDto) {

        Member findMember = userPrincipal.getMember();

        Story findStory = storyRepository.findById(createCommentDto.storyId())
                .orElseThrow(() -> new StoryNotFoundException(StatusCode.STORY_NOT_FOUND));

        Comment comment = Comment.builder()
                .member(findMember)
                .story(findStory)
                .content(createCommentDto.content())
                .build();

        // 부모 댓글에 자식댓글 추가
        if (createCommentDto.parentCommentId() != -1) {
            Comment parentComment = commentRepository.findById(createCommentDto.parentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException(StatusCode.COMMENT_NOT_FOUND));

            comment.updateParent(parentComment);

            // 부모 댓글 작성자 != 자식댓글 작성자 -> 알림 생성
            if (!parentComment.getMember().getId().equals(findMember.getId()))
                notificationService.saveNotification(parentComment.getMember(), findMember.getProfileImage(), comment.getContent(), findStory.getId(), NotificationType.COMMENT);
        }

        commentRepository.save(comment);

        // 스토리 작성자 != 댓글 작성자 -> 알림 생성
        if (!findStory.getMember().getId().equals(findMember.getId()))
            notificationService.saveNotification(findStory.getMember(), findMember.getProfileImage(), comment.getContent(), findStory.getId(), NotificationType.COMMENT);

        eventPublisher.publishEvent(new CommentFcmEvent(comment, findMember));
    }

    public Slice<ResponseParentCommentDto> findParentCommentByStoryId(UserPrincipal userPrincipal, Long storyId, Long cursorId, int size) {

        Member member = userPrincipal.getMember();

        Pageable pageable = PageRequest.of(0, size);

        return commentRepository.findParentCommentByStoryId(member, storyId, pageable, cursorId);
    }

    public Slice<ResponseChildCommentDto> findChildCommentByParentCommentId(UserPrincipal userPrincipal, Long parentCommentId, Long cursorId, int size) {

        Member member = userPrincipal.getMember();

        Pageable pageable = PageRequest.of(0, size);

        return commentRepository.findChildCommentByParentCommendId(member, parentCommentId, pageable, cursorId);
    }

    @Transactional
    public void editComment(UserPrincipal userPrincipal, Long commentId, String content) {
        Member findMember = userPrincipal.getMember();

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(StatusCode.COMMENT_NOT_FOUND));

        if (Objects.equals(findComment.getMember().getId(), findMember.getId())) {
            findComment.updateComment(content);
        } else {
            //FIXME : 권한 문제로 예외 바꾸기
            throw new CommentNotFoundException(StatusCode.COMMENT_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteComment(UserPrincipal userPrincipal, Long commentId) {
        Member findMember = userPrincipal.getMember();

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(StatusCode.COMMENT_NOT_FOUND));

        if (Objects.equals(findComment.getMember().getId(), findMember.getId())) {
            findComment.deleteComment();
        } else {
            //FIXME : 권한 문제로 예외 바꾸기
            throw new CommentNotFoundException(StatusCode.COMMENT_NOT_FOUND);
        }
    }

    public Slice<ResponseMyCommentDto> findMyCommentByMember(UserPrincipal userPrincipal, Long cursorId, int size) {

        Member findMember = userPrincipal.getMember();

        Pageable pageable = PageRequest.of(0, size);

        return commentRepository.findCommentsByMember(findMember, pageable, cursorId);
    }

    public Long findCommentCntByStoryId(UserPrincipal userPrincipal, Long storyId) {

        return commentRepository.countByStoryIdAndIsDeletedFalse(storyId);
    }
}
