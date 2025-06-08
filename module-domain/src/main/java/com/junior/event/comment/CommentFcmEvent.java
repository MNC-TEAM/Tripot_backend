package com.junior.event.comment;

import com.junior.domain.member.Member;
import com.junior.domain.story.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentFcmEvent {

    private Comment comment;
    private Member author;
}