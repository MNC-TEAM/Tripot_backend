package com.junior.repository.festival;

import com.junior.domain.festival.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long>, FestivalRepositoryCustom {

    boolean existsByContentId(Long contentId);

    Optional<Festival> findByContentId(Long contentId);

}
