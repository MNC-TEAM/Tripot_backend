package com.junior.repository.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FestivalLikeRepository extends JpaRepository<FestivalLike, Long>, FestivalLikeRepositoryCustom {
    Optional<FestivalLike> findByMemberAndFestival(Member member, Festival festival);

    boolean existsByMemberAndFestival(Member member, Festival festival);
}
