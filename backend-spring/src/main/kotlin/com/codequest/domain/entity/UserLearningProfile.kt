package com.codequest.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime

@Entity
@Table(
    name = "user_learning_profile",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "language", "track", "level_id"],
            name = "uk_learning_profile"
        )
    ]
)
data class UserLearningProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "language", nullable = false)
    val language: String,

    @Column(name = "track", nullable = false)
    val track: String = "beginner",

    @Column(name = "level_id", nullable = false)
    val levelId: Int,

    @Column(name = "attempts")
    var attempts: Int = 0,

    @Column(name = "failed_commands", columnDefinition = "TEXT")
    var failedCommands: String? = null,

    @Column(name = "last_error", columnDefinition = "TEXT")
    var lastError: String? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime? = null
)

