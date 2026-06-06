package com.codequest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelProgressionTest {

    @Test
    void xpRequiredIncreasesBy200PerLevel() {
        assertEquals(400, LevelProgression.xpRequiredForNextLevel(1));
        assertEquals(600, LevelProgression.xpRequiredForNextLevel(2));
        assertEquals(800, LevelProgression.xpRequiredForNextLevel(3));
    }

    @Test
    void levelFromTotalXp() {
        assertEquals(1, LevelProgression.levelFromTotalXp(0));
        assertEquals(1, LevelProgression.levelFromTotalXp(399));
        assertEquals(2, LevelProgression.levelFromTotalXp(400));
        assertEquals(2, LevelProgression.levelFromTotalXp(999));
        assertEquals(3, LevelProgression.levelFromTotalXp(1000));
        assertEquals(3, LevelProgression.levelFromTotalXp(1799));
        assertEquals(4, LevelProgression.levelFromTotalXp(1800));
    }
}
