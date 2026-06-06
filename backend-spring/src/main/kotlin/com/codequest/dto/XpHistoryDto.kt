package com.codequest.dto

import com.codequest.domain.entity.XpHistory
import java.time.OffsetDateTime

data class XpHistoryDto(
    val id: Long,
    val userId: String,
    val amount: Int,
    val reason: String,
    val createdAt: OffsetDateTime?
) {
    companion object {
        fun from(xpHistory: XpHistory): XpHistoryDto {
            return XpHistoryDto(
                id = xpHistory.id,
                userId = xpHistory.userId,
                amount = xpHistory.amount,
                reason = xpHistory.reason,
                createdAt = xpHistory.createdAt
            )
        }
    }
}

