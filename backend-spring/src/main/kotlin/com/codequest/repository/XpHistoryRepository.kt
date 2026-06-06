package com.codequest.repository

import com.codequest.domain.entity.XpHistory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface XpHistoryRepository : JpaRepository<XpHistory, Long> {
    
    fun findByUserIdOrderByCreatedAtDesc(userId: String, pageable: Pageable): List<XpHistory>
}

