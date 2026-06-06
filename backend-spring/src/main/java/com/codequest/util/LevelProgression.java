package com.codequest.util;

/**
 * XP thresholds per level: 1→2 needs 400, 2→3 needs 600, 3→4 needs 800 (+200 each level).
 */
public final class LevelProgression {

    public static final int BASE_XP_TO_LEVEL_UP = 400;
    public static final int XP_INCREMENT_PER_LEVEL = 200;

    private LevelProgression() {}

    /** XP required to advance from {@code level} to {@code level + 1}. */
    public static int xpRequiredForNextLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        return BASE_XP_TO_LEVEL_UP + (level - 1) * XP_INCREMENT_PER_LEVEL;
    }

    /** Total XP at the start of {@code level} (level 1 starts at 0 XP). */
    public static int totalXpForLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        if (level == 1) {
            return 0;
        }
        int steps = level - 1;
        return BASE_XP_TO_LEVEL_UP * steps + XP_INCREMENT_PER_LEVEL * (steps - 1) * steps / 2;
    }

    public static int levelFromTotalXp(int totalXp) {
        int xp = Math.max(0, totalXp);
        int level = 1;
        while (xp >= totalXpForLevel(level + 1)) {
            level++;
        }
        return level;
    }

    public static int xpInCurrentLevel(int totalXp, int level) {
        return Math.max(0, totalXp - totalXpForLevel(level));
    }
}
