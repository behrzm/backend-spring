package com.codequest.repository

import com.codequest.domain.entity.Profile
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : JpaRepository<Profile, String> {
    
    @Query(
        "SELECT p FROM Profile p ORDER BY p.xp DESC, p.level DESC",
        nativeQuery = false
    )
    fun findTopPlayers(pageable: Pageable): List<Profile>

    fun findById(id: String): Profile?
}

