package com.codequest.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
@Table(
    name = "level_progress",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "language", "track", "level_id"],
            name = "uk_level_progress"
        )
    ]
)
data class LevelProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "language", nullable = false)
    val language: String,

    @Column(name = "track", nullable = false)
    val track: String,

    @Column(name = "level_id", nullable = false)
    val levelId: Int,

    @Column(name = "stars")
    var stars: Int = 0,

    @Column(name = "completed")
    var completed: Boolean = false,

    @Column(name = "completed_at")
    var completedAt: OffsetDateTime? = null
)

