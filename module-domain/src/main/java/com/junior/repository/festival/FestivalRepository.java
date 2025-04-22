package com.junior.repository.festival;

import com.junior.domain.festival.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface FestivalRepository extends JpaRepository<Festival, Long>, FestivalRepositoryCustom {

    boolean existsByContentId(Long contentId);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE festival", nativeQuery = true)
    void truncateFestival();
}
