package com.junior.event.like;

import com.junior.domain.member.Member;
import com.junior.domain.story.Story;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeFcmEvent {

    private Member likeMember;
    private Story likedStory;
}
