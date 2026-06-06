package com.prolearn.codecraftfront.game

/**
 * XP per level: 1→2 = 400, 2→3 = 600, 3→4 = 800 (+200 each level).
 */
object LevelProgression {
    const val BASE_XP_TO_LEVEL_UP = 400
    const val XP_INCREMENT_PER_LEVEL = 200

    fun xpRequiredForNextLevel(level: Int): Int {
        require(level >= 1) { "level must be >= 1" }
        return BASE_XP_TO_LEVEL_UP + (level - 1) * XP_INCREMENT_PER_LEVEL
    }

    fun totalXpForLevel(level: Int): Int {
        require(level >= 1) { "level must be >= 1" }
        if (level == 1) return 0
        val steps = level - 1
        return BASE_XP_TO_LEVEL_UP * steps + XP_INCREMENT_PER_LEVEL * (steps - 1) * steps / 2
    }

    fun levelFromTotalXp(totalXp: Int): Int {
        var level = 1
        while (totalXp >= totalXpForLevel(level + 1)) {
            level++
        }
        return level
    }

    fun xpInCurrentLevel(totalXp: Int, level: Int): Int =
        (totalXp - totalXpForLevel(level)).coerceAtLeast(0)
}
