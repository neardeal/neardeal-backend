package com.neardeal.domain.store.repository;

import com.neardeal.domain.store.entity.Store;
import com.neardeal.domain.store.entity.StoreReport;
import com.neardeal.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReportRepository extends JpaRepository<StoreReport, Long> {
    boolean existsByStoreAndReporter(Store store, User reporter);
}
