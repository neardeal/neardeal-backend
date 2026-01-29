package com.looky.domain.store.repository;

import com.looky.domain.store.entity.Store;
import com.looky.domain.store.entity.StoreCategory;
import com.looky.domain.store.entity.StoreMood;
import jakarta.persistence.criteria.SetJoin;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class StoreSpecification {

    public static Specification<Store> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            return cb.like(root.get("name"), "%" + keyword + "%");
        };
    }

    public static Specification<Store> hasCategories(List<StoreCategory> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) {
                return null;
            }
            SetJoin<Store, StoreCategory> categoryJoin = root.joinSet("storeCategories");
            query.distinct(true);
            return categoryJoin.in(categories);
        };
    }

    public static Specification<Store> hasMoods(List<StoreMood> moods) {
        return (root, query, cb) -> {
            if (moods == null || moods.isEmpty()) {
                return null;
            }
            SetJoin<Store, StoreMood> moodJoin = root.joinSet("storeMoods");
            query.distinct(true);
            return moodJoin.in(moods);
        };
    }
}
