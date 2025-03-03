package com.junior.repository.member;

import com.junior.domain.member.MemberRole;
import com.junior.dto.member.MemberListResponseDto;
import com.junior.dto.member.QMemberListResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.junior.domain.member.QMember.member;

@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberListResponseDto> findMember(Pageable pageable, String q) {

        List<MemberListResponseDto> fetch = queryFactory.select(
                        new QMemberListResponseDto(
                                member.id,
                                member.nickname,
                                member.signUpType,
                                member.status,
                                member.createdDate
                        )
                )
                .from(member)
                .where(queryContains(q), member.role.eq(MemberRole.USER))
                .orderBy(member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory.select(member.count())
                .from(member)
                .where(queryContains(q), member.role.eq(MemberRole.USER));


        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    private BooleanExpression queryContains(String q) {
        return member.nickname.contains(q);
    }
}
