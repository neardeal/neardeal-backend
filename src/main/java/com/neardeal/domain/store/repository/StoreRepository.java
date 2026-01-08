package com.neardeal.domain.store.repository;

import com.neardeal.domain.store.entity.Store;
import com.neardeal.domain.store.entity.StoreCategory;
import com.neardeal.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Page<Store> findAll(Pageable pageable);

    boolean existsByName(String name);

    Page<Store> findByNameContaining(String keyword, Pageable pageable);

    Page<Store> findByStoreCategory(StoreCategory category, Pageable pageable);

    Page<Store> findByNameContainingAndStoreCategory(String keyword, StoreCategory category, Pageable pageable);

    List<Store> findAllByUser(User user);
}
