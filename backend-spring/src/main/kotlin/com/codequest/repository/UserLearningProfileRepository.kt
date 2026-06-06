package com.codequest.repository

import com.codequest.domain.entity.UserLearningProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserLearningProfileRepository : JpaRepository<UserLearningProfile, Long> {
    
    fun findByUserIdAndLanguageAndTrackAndLevelId(
        userId: String,
        language: String,
        track: String,
        levelId: Int
    ): UserLearningProfile?
}

