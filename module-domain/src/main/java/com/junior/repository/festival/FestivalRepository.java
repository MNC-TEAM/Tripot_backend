package com.junior.repository.festival;

import com.junior.domain.festival.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    boolean existsByContentId(Long contentId);
}
