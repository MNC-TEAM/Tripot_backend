package com.junior.repository.festival;

import com.junior.domain.festival.like.FestivalLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalLikeRepository extends JpaRepository<FestivalLike, Long> {
}
