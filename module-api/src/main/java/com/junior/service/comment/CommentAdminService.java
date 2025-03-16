package com.junior.service.comment;

import com.junior.domain.story.Comment;
import com.junior.dto.comment.CommentAdminDto;
import com.junior.exception.CommentNotFoundException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentAdminService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public PageCustom<CommentAdminDto> findComment(Pageable pageable) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());

        Page<Comment> pageResult = commentRepository.findAllByOrderByIdDesc(pageRequest);
        List<Comment> content = pageResult.getContent();

        List<CommentAdminDto> result = content.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        log.info("[{}] 관리자 댓글 조회 page: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), pageable.getPageNumber());

        return new PageCustom<>(result, pageResult.getPageable(), pageResult.getTotalElements());

    }

    @Transactional
    public void deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(StatusCode.COMMENT_NOT_FOUND));

        log.info("[{}] 관리자 댓글 삭제 id: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), commentId);

        comment.deleteComment();
    }

    private CommentAdminDto convertToDto(Comment comment) {

        return CommentAdminDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdUsername(comment.getMember().getUsername())
                .isDeleted(comment.getIsDeleted())
                .build();
    }

}
