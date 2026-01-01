package com.neardeal.domain.affiliation.repository;

import com.neardeal.domain.affiliation.entity.Affiliation;
import com.neardeal.domain.affiliation.entity.UserAffiliation;
import com.neardeal.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAffiliationRepository extends JpaRepository<UserAffiliation, Long> {
    boolean existsByUserAndAffiliation(User user, Affiliation affiliation);

    Optional<UserAffiliation> findByUserAndAffiliation(User user, Affiliation affiliation);
}
