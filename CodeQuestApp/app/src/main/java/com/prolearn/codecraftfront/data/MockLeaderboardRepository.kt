package com.prolearn.codecraftfront.data

class MockLeaderboardRepository : LeaderboardRepository {

    private val mockData = mutableListOf(
        LeaderboardEntry("mock_1", "Neon Hiro", 4820, 12),
        LeaderboardEntry("mock_2", "Quantum Mira", 4310, 11),
        LeaderboardEntry("mock_3", "Pixel Kai", 3870, 10),
        LeaderboardEntry("mock_4", "Cyber Zoe", 3540, 9),
        LeaderboardEntry("mock_5", "Glitch Mei", 3010, 8),
        LeaderboardEntry("mock_6", "Vector Ren", 2640, 8),
        LeaderboardEntry("mock_7", "Void Ada", 2180, 7),
        LeaderboardEntry("mock_8", "Synth Leo", 1740, 6),
    )

    override suspend fun fetchCurrentUser(userId: String): LeaderboardEntry? {
        return mockData.firstOrNull { it.userId == userId }
    }

    override suspend fun fetchTop(limit: Long): List<LeaderboardEntry> {
        return mockData.sortedByDescending { it.xp }.take(limit.toInt())
    }

    override suspend fun upsertCurrentUser(userId: String, displayName: String, xp: Int, level: Int) {
        val index = mockData.indexOfFirst { it.userId == userId }
        if (index != -1) {
            mockData[index] = LeaderboardEntry(userId, displayName, xp, level)
        } else {
            mockData.add(LeaderboardEntry(userId, displayName, xp, level))
        }
    }



    override suspend fun incrementCurrentUserXp(
        userId: String,
        displayName: String,
        deltaXp: Int,
        reason: String // Этот параметр обязателен для интерфейса
    ) {
        // Логика теперь здесь
        val current = fetchCurrentUser(userId)
        if (current != null) {
            val newXp = current.xp + deltaXp
            val newLevel = com.prolearn.codecraftfront.game.LevelProgression.levelFromTotalXp(newXp)
            upsertCurrentUser(userId, displayName, newXp, newLevel)
        } else {
            upsertCurrentUser(userId, displayName, deltaXp, 1)
        }
    }
}