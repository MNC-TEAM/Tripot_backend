package com.junior.event;

import com.junior.domain.member.Member;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentFcmEvent {

    private Comment comment;
    private Member author;

    public boolean isCommentAuthorAndStoryAuthorSame(Long storyAuthorId) {
        return !storyAuthorId.equals(author.getId());
    }
}