package com.neardeal.domain.review.repository;

import com.neardeal.domain.review.entity.Review;
import com.neardeal.domain.review.entity.ReviewLike;
import com.neardeal.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByUserAndReview(User user, Review review);

    void deleteByUserAndReview(User user, Review review);
}
