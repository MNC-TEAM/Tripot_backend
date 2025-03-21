package com.junior.repository.story.custom;

import com.junior.domain.member.Member;
import com.junior.domain.story.Story;
import com.junior.dto.story.AdminStoryDto;
import com.junior.dto.story.GeoPointDto;
import com.junior.dto.story.ResponseStoryCntByCityDto;
import com.junior.dto.story.ResponseStoryListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface StoryCustomRepository {
//    public Slice<ResponseStoryDto> findAllStories_old(Long cursorId, Pageable pageable);

    public Slice<ResponseStoryListDto> findAllStories(Member member, Long cursorId, Pageable pageable, String city, String search);

//    public Slice<ResponseStoryDto> findStoriesByMemberAndCity(Long cursorId, Pageable pageable, String city, Member member);

    public Optional<Story> findStoryByIdAndMember(Long storyId, Member member);

    //    public Slice<ResponseStoryDto> findStoriesByMemberAndMapWithPaging(Long cursorId, Pageable pageable, GeoPointDto geoPointLt, GeoPointDto geoPointRb, Member findMember);
    public List<ResponseStoryListDto> findStoryByMap(Member findMember, GeoPointDto geoPointLt, GeoPointDto geoPointRb);

    public Slice<ResponseStoryListDto> findStoriesByMemberAndCityAndSearch(Long cursorId, Pageable pageable, Member findMember, String city, String search);

    public List<ResponseStoryCntByCityDto> getStoryCntByCity(Member findMember);

    public Boolean isLikedMember(Member findMember, Story fidStory);

    public Optional<String> getRecommendedRandomCity();

    Optional<String> getRecommendedRecentPopularCity();

    Slice<ResponseStoryListDto> getRecentPopularStories(Member member, Long cursorId, Pageable pageable);

    public Slice<ResponseStoryListDto> findLikeStories(Member findMember, Pageable pageable, Long cursorId);

    public Page<AdminStoryDto> findAllStories(Pageable pageable, String keyword);
}
