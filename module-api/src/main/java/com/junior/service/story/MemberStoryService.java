package com.junior.service.story;

import com.junior.domain.like.Like;
import com.junior.domain.member.Member;
import com.junior.domain.notification.NotificationType;
import com.junior.domain.story.Story;
import com.junior.dto.story.*;
import com.junior.event.comment.CommentFcmEvent;
import com.junior.event.like.LikeFcmEvent;
import com.junior.exception.DeletedStoryException;
import com.junior.exception.PermissionException;
import com.junior.exception.StatusCode;
import com.junior.exception.StoryNotFoundException;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.story.LikeRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStoryService {
    private final StoryRepository storyRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createStory(UserPrincipal userPrincipal, CreateStoryDto createStoryDto) {

        Member member = userPrincipal.getMember();

        Story story = Story.from(member, createStoryDto);

        storyRepository.save(story);
    }

    @Transactional
    public void editStory(UserPrincipal userPrincipal, Long storyId, CreateStoryDto createStoryDto) {

        Member findMember = userPrincipal.getMember();

        Story findStory = storyRepository.findByIdAndIsDeletedFalse(storyId)
                .orElseThrow(() -> new StoryNotFoundException(StatusCode.STORY_NOT_FOUND));

        boolean isAuthor = findMember.getId().equals(findStory.getMember().getId());

        if (isAuthor) {
            // 더티 체킹을 통해 수정쿼리가 자동으로 발생
            findStory.updateStory(createStoryDto);
        } else {
            throw new PermissionException(StatusCode.STORY_NOT_PERMISSION);
        }
    }

    // findStoriesByMemberAndCityAndSearch
    public Slice<ResponseStoryListDto> findStoriesByFilter(UserPrincipal userPrincipal, Long cursorId, int size, String city, String search) {

        Member findMember = userPrincipal.getMember();

        Pageable pageable = PageRequest.of(0, size);

        return storyRepository.findStoriesByMemberAndCityAndSearch(cursorId, pageable, findMember, city, search);
    }

    public List<ResponseStoryCntByCityDto> getStoryCntByCity(UserPrincipal userPrincipal) {
        Member findMember = userPrincipal.getMember();

        List<ResponseStoryCntByCityDto> storyCntByCity = storyRepository.getStoryCntByCity(findMember);

        int totalStoryCnt = storyCntByCity.stream()
                .mapToInt(ResponseStoryCntByCityDto::cnt)
                .sum();

        ResponseStoryCntByCityDto allCityDto = new ResponseStoryCntByCityDto("all", totalStoryCnt);

        storyCntByCity.add(allCityDto);

        return storyCntByCity;
    }

    public List<ResponseStoryListDto> findStoriesByMap(UserPrincipal userPrincipal, GeoPointDto geoPointLt, GeoPointDto geoPointRb) {
        Member findMember = userPrincipal.getMember();

        return storyRepository.findStoryByMap(findMember, geoPointLt, geoPointRb);
    }

    //    @Transactional
    public ResponseStoryDto findOneStory(UserPrincipal userPrincipal, Long storyId) {
//        Member findMember = userPrincipal.getMember();

        Member findMember = (userPrincipal != null) ? userPrincipal.getMember() : null;

        Story findStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(StatusCode.STORY_NOT_FOUND));

        if (findStory.getIsDeleted()) {
            throw new DeletedStoryException(StatusCode.STORY_DELETED);
        }

        Long commentCntByStoryId = commentRepository.countByStoryIdAndIsDeletedFalse(findStory.getId());

        boolean isLikeStory = (findMember != null) && likeRepository.isLikeStory(findMember, findStory);

        boolean isAuthor = (findMember != null) && findStory.getMember().getId().equals(findMember.getId());
        boolean isHidden = findStory.isHidden();

        if (isHidden && !isAuthor) {
            throw new StoryNotFoundException(StatusCode.STORY_NOT_PERMISSION);
        }

        findStory.increaseViewCnt();

        return ResponseStoryDto.from(findStory, isLikeStory, isAuthor, commentCntByStoryId
        );
    }

    @Transactional
    public void clickLike(UserPrincipal userPrincipal, Long storyId) {
        Member findMember = userPrincipal.getMember();

        Story findStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(StatusCode.STORY_NOT_FOUND));

        // is clicked like?
        Boolean isLiked = storyRepository.isLikedMember(findMember, findStory);

        if (!isLiked) {
            Like like = Like.builder()
                    .member(findMember)
                    .story(findStory)
                    .build();

            likeRepository.save(like);

            findStory.increaseLikeCnt();

            //좋아요 누른 사람 != 스토리 주인 -> 알림 저장
            if (!findStory.getMember().getId().equals(findMember.getId())) {
                notificationService.saveNotification(findStory.getMember(), findMember.getProfileImage(), findStory.getTitle(), findStory.getId(), NotificationType.LIKED);
                eventPublisher.publishEvent(new LikeFcmEvent(findMember, findStory));
            }

        } else {
            Like findLike = likeRepository.findLikeByMemberAndStory(findMember, findStory);
            likeRepository.delete(findLike);

            findStory.decreaseLikeCnt();
        }
    }

    public Slice<ResponseStoryListDto> getLikeStories(UserPrincipal userPrincipal, Long cursorId, int size) {
        Member findMember = userPrincipal.getMember();

        Pageable pageable = PageRequest.of(0, size);

        return storyRepository.findLikeStories(findMember, pageable, cursorId);
    }

    @Transactional
    public void deleteStory(UserPrincipal userPrincipal, Long storyId) {
        Member findMember = userPrincipal.getMember();

        Story findStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(StatusCode.STORY_NOT_FOUND));

        if (findMember.getId().equals(findStory.getMember().getId())) {
            findStory.deleteStory();
        } else {
            throw new PermissionException(StatusCode.STORY_NOT_PERMISSION);
        }
    }
}