package com.codequest.repository;

import com.codequest.domain.entity.UserLearningProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLearningProfileRepository extends JpaRepository<UserLearningProfile, Long> {
    
    Optional<UserLearningProfile> findByUserIdAndLanguageAndTrackAndLevelId(
            String userId, String language, String track, Integer levelId);
}

