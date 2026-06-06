package com.codequest.repository

import com.codequest.domain.entity.LevelProgress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LevelProgressRepository : JpaRepository<LevelProgress, Long> {
    
    fun findByUserIdAndLanguageAndTrackAndLevelId(
        userId: String,
        language: String,
        track: String,
        levelId: Int
    ): LevelProgress?
}

