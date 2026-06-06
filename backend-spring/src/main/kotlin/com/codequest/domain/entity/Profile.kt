package com.codequest.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime

@Entity
@Table(name = "profiles")
data class Profile(
    @Id
    @Column(name = "id", length = 255)
    val id: String,

    @Column(name = "email", length = 255)
    val email: String? = null,

    @Column(name = "display_name", length = 255)
    var displayName: String? = null,

    @Column(name = "xp")
    var xp: Int = 0,

    @Column(name = "level")
    var level: Int = 1,

    @Column(name = "streak")
    var streak: Int = 0,

    @Column(name = "wins")
    var wins: Int = 0,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: OffsetDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime? = null
)

