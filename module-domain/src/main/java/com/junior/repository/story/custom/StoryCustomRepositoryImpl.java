package com.junior.repository.story.custom;

import com.junior.domain.like.QLike;
import com.junior.domain.member.Member;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.story.QStory;
import com.junior.domain.story.Story;
import com.junior.dto.story.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.junior.domain.like.QLike.like;
import static com.junior.domain.member.QMember.member;
import static com.junior.domain.story.QStory.story;


@Slf4j
@RequiredArgsConstructor
public class StoryCustomRepositoryImpl implements StoryCustomRepository {

    private final JPAQueryFactory query;

    QResponseStoryListDto createQResponseStoryListDto() {
        return new QResponseStoryListDto(story.thumbnailImg, story.title, story.content, story.city, story.id, story.latitude, story.longitude, story.likeCnt, story.createdDate);
    }

    private boolean isHaveNextStoryList(List<ResponseStoryListDto> stories, Pageable pageable) {

        boolean hasNext;

        if (stories.size() == pageable.getPageSize() + 1) {
            stories.remove(pageable.getPageSize());
            hasNext = true;
        } else {
            hasNext = false;
        }

        return hasNext;
    }

    private BooleanExpression eqCursorId(Long cursorId) {
        if (cursorId != null) {
            return story.id.lt(cursorId);
        }
        return null;
    }

    //FIXME: 필요에 따라 eqCursorId와 하나로 합칠 필요 있음
    private BooleanExpression PopularEqCursorId(Long cursorId) {
        if (cursorId != null) {
            return story.likeCnt.lt(cursorId);
        }
        return null;
    }

    private OrderSpecifier<?> getOrderByClause(String sortCondition) {
        if ("asc".equalsIgnoreCase(sortCondition)) {
            return story.createdDate.asc(); // 생성 날짜 오름차순
        } else if ("desc".equalsIgnoreCase(sortCondition)) {
            return story.createdDate.desc(); // 생성 날짜 내림차순
        } else if ("popular".equalsIgnoreCase(sortCondition)) {
            return story.likeCnt.desc(); // 인기순 (좋아요 수 기반)
        } else {
            return story.createdDate.desc(); // 기본값: 생성 날짜 내림차순
        }
    }

    private BooleanBuilder getHiddenCondition(Member member) {
        BooleanBuilder hiddenCondition = new BooleanBuilder();

        hiddenCondition.or(story.isHidden.eq(false));
        hiddenCondition.or(
                story.isHidden.eq(true).and(story.member.eq(member))
        );

        return hiddenCondition;
    }

    private BooleanBuilder getIsAuthorCondition(Member member) {
        BooleanBuilder isAuthorCondition = new BooleanBuilder();

        isAuthorCondition.and(story.member.eq(member));

        return isAuthorCondition;
    }

    private BooleanBuilder getCityCondition(String city) {
        BooleanBuilder cityCondition = new BooleanBuilder();

        if (!StringUtils.isBlank(city)) {
            cityCondition.and(story.city.eq(city));
        }

        return cityCondition;
    }

    private BooleanBuilder getSearchCondition(String search) {
        BooleanBuilder searchCondition = new BooleanBuilder();

        if (!StringUtils.isBlank(search)) {
            searchCondition.and((story.title.contains(search)).or(story.content.contains(search)));
        }

        return searchCondition;
    }

    private BooleanBuilder getDeleteCondition() {
        BooleanBuilder deleteCondition = new BooleanBuilder();

        deleteCondition.and(story.isDeleted.eq(false));

        return deleteCondition;
    }

    @Override
    public Slice<ResponseStoryListDto> findAllStories(Member member, Long cursorId, Pageable pageable, String city, String search) {

        List<ResponseStoryListDto> stories = query.select(createQResponseStoryListDto())
                .from(story)
                .where(getCityCondition(city),
                        eqCursorId(cursorId),
                        getSearchCondition(search),
                        getDeleteCondition(),
                        story.isHidden.eq(false)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderByClause("desc"))
                .fetch();

        boolean hasNext = isHaveNextStoryList(stories, pageable);

        return new SliceImpl<>(stories, pageable, hasNext);
    }

    @Override
    public Optional<Story> findStoryByIdAndMember(Long storyId, Member member) {

        /*
            Dto 가 아닌 엔티티 그 자체를 반환
            게시글을 수정할 때 더티체킹이 진행되기 위함
         */
        Story stories = query.select(story)
                .from(story)
                .where(
                        getIsAuthorCondition(member),
                        story.id.eq(storyId),
                        getDeleteCondition()
                )
                .fetchOne();

        return Optional.ofNullable(stories);
    }

    @Override
    public List<ResponseStoryListDto> findStoryByMap(Member findMember, GeoPointDto geoPointLt, GeoPointDto geoPointRb) {

        return query.select(createQResponseStoryListDto())
                .from(story)
                .where(story.latitude.between(
                                Math.min(geoPointLt.latitude(), geoPointRb.latitude()),
                                Math.max(geoPointLt.latitude(), geoPointRb.latitude())
                        ),
                        story.longitude.between(
                                Math.min(geoPointLt.longitude(), geoPointRb.longitude()),
                                Math.max(geoPointLt.longitude(), geoPointRb.longitude())
                        ),
                        getIsAuthorCondition(findMember),
                        getHiddenCondition(findMember),
                        getDeleteCondition()
                )
                .orderBy(getOrderByClause("desc"))
                .fetch();
    }

    @Override
    public Slice<ResponseStoryListDto> findStoriesByMemberAndCityAndSearch(Long cursorId, Pageable pageable, Member findMember, String city, String search) {

        List<ResponseStoryListDto> stories = query.select(createQResponseStoryListDto())
                .from(story)
                .where(
                        getIsAuthorCondition(findMember),
                        getCityCondition(city),
                        getSearchCondition(search),
                        eqCursorId(cursorId),
                        getHiddenCondition(findMember),
                        getDeleteCondition()
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderByClause("desc"))
                .fetch();

        boolean hasNext = isHaveNextStoryList(stories, pageable);

        return new SliceImpl<>(stories, pageable, hasNext);
    }

    @Override
    public List<ResponseStoryCntByCityDto> getStoryCntByCity(Member findMember) {

        return query.select(new QResponseStoryCntByCityDto(story.city, story.count().intValue()))
                .from(story)
                .where(
                        getIsAuthorCondition(findMember),
                        getDeleteCondition()
                )
                .groupBy(story.city)
                .orderBy(story.city.asc())
                .fetch();
    }

    @Override
    public Boolean isLikedMember(Member findMember, Story findStory) {

        QLike like = QLike.like;

        return query.selectOne()
                .from(like)
                .where(like.member.eq(findMember),
                        like.story.eq(findStory))
                .fetchFirst() != null;
    }

    @Override
    public Optional<String> getRecommendedRandomCity() {
        String randomCity = query.select(story.city)
                .from(story)
                .where(
                        getDeleteCondition(),
                        story.isHidden.eq(false)
                )
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(1L)
                .fetchOne();

        return Optional.ofNullable(randomCity);
    }

    @Override
    public Optional<String> getRecommendedRecentPopularCity() {

        // 서브쿼리로 최신 n개의 글을 가져온 후, 그 중에서 도시별로 그룹화하여 개수를 카운트
        QStory subStory = new QStory("subStory");

        List<Tuple> result = query.select(story.city, story.city.count())
                .from(story)
                .where(story.createdDate.in(
                        JPAExpressions.select(subStory.createdDate)
                                .from(subStory)
                                .where(
                                        getDeleteCondition(),
                                        story.isHidden.eq(false)
                                )
                                .orderBy(subStory.createdDate.desc())  // 최신 글 순으로 정렬
                                .limit(100L) // 최신 n개의 글
                ))
                .groupBy(story.city) // 도시별로 그룹화
                .orderBy(story.city.count().desc()) // 도시별 게시글 수 내림차순
                .limit(1) // 가장 많이 등장한 도시만 반환
                .fetch();

        // 결과가 있다면, 첫 번째 튜플의 도시 값을 반환
        return result.stream()
                .findFirst()
                .map(tuple -> tuple.get(story.city));
    }

    @Override
    public Slice<ResponseStoryListDto> getRecentPopularStories(Member member, Long cursorId, Pageable pageable) {

        List<ResponseStoryListDto> stories = query.select(createQResponseStoryListDto())
                .from(story)
                .where(
                        PopularEqCursorId(cursorId),
                        getDeleteCondition(),
                        story.isHidden.eq(false)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(
                        getOrderByClause("popular")
                )
                .fetch();

        boolean hasNext = isHaveNextStoryList(stories, pageable);

        return new SliceImpl<>(stories, pageable, hasNext);
    }

    @Override
    public Slice<ResponseStoryListDto> findLikeStories(Member findMember, Pageable pageable, Long cursorId) {
        List<ResponseStoryListDto> stories = query.select(createQResponseStoryListDto())
                .from(like)
                .where(like.member.id.eq(findMember.getId()),
                        eqCursorId(cursorId),
                        getDeleteCondition()
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderByClause("desc"))
                .fetch();

        boolean hasNext = isHaveNextStoryList(stories, pageable);

        return new SliceImpl<>(stories, pageable, hasNext);
    }


    /**
     * 관리자 페이지에서 스토리의 리스트를 페이지로 반환
     * @param pageable
     * @param keyword
     * @return
     */
    @Override
    public Page<AdminStoryDto> findAllStories(Pageable pageable, String keyword) {

        List<AdminStoryDto> result = query.select(new QAdminStoryDto(
                        story.title,
                        story.city,
                        story.id,
                        new CaseBuilder()
                                .when(story.member.status.eq(MemberStatus.DELETE)).then("탈퇴회원")
                                .otherwise(story.member.nickname),
                        story.isDeleted,
                        story.createdDate,
                        story.lastModifiedDate
                ))
                .from(story)
                .leftJoin(story.member, member)
                .where(getSearchCondition(keyword))
                .orderBy(story.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = query.select(story.count())
                .from(story)
                .where(getSearchCondition(keyword));

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }
}
