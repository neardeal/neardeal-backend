package com.looky.domain.store.repository;

import com.looky.domain.store.entity.StoreUniversity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreUniversityRepository extends JpaRepository<StoreUniversity, Long> {
    boolean existsByStoreIdAndUniversityId(Long storeId, Long universityId);

    List<StoreUniversity> findByUniversityId(Long universityId);
}
