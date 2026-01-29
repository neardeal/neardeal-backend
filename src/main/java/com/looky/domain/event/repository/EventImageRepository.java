package com.looky.domain.event.repository;

import com.looky.domain.event.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
}
