package com.junior.repository.popUpEvent;

import com.junior.domain.popUpEvent.PopUpEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopUpEventRepository extends JpaRepository<PopUpEvent, Long> , PopUpEventCustomRepository{
}
