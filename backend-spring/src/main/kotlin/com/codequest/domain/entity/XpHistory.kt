package com.codequest.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
@Table(name = "xp_history")
data class XpHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "amount", nullable = false)
    val amount: Int,

    @Column(name = "reason", length = 255)
    val reason: String = "level_complete",

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: OffsetDateTime? = null
)

