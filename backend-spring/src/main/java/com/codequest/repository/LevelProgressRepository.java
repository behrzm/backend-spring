package com.codequest.repository;

import com.codequest.domain.entity.LevelProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LevelProgressRepository extends JpaRepository<LevelProgress, Long> {
    
    Optional<LevelProgress> findByUserIdAndLanguageAndTrackAndLevelId(
            String userId, String language, String track, Integer levelId);

    List<LevelProgress> findByUserId(String userId);
}
