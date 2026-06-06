package com.prolearn.codecraftfront.game

/**
 * Describes how the playfield should look so it matches the coding task (not a generic maze).
 */
sealed class ArenaVisualSpec {
    abstract val caption: String

    /** Two operands + operator — e.g. sum, product */
    data class BinaryFusion(
        val left: String,
        val opSymbol: String,
        val right: String,
        override val caption: String,
    ) : ArenaVisualSpec()

    /** Pick larger / compare */
    data class BalancePick(
        override val caption: String,
    ) : ArenaVisualSpec()

    /** Boolean / parity checks */
    data class LogicGate(
        val expression: String,
        override val caption: String,
    ) : ArenaVisualSpec()

    /** Three-way minimum */
    data class TriplePick(
        override val caption: String,
    ) : ArenaVisualSpec()

    /** Equality / two-label checks */
    data class TwinMatch(
        val left: String,
        val right: String,
        override val caption: String,
    ) : ArenaVisualSpec()
}

/**
 * Same task shape per level index across all languages so visuals stay aligned with exercises.
 */
fun arenaVisualForLevel(levelId: Int): ArenaVisualSpec {
    return when (levelId.coerceIn(1, 12)) {
        1 -> ArenaVisualSpec.BinaryFusion(
            left = "a",
            opSymbol = "+",
            right = "b",
            caption = "Fusion bay — your correct return links both inputs into one result.",
        )
        2 -> ArenaVisualSpec.BinaryFusion(
            left = "a",
            opSymbol = "×",
            right = "b",
            caption = "Twin amplifiers — multiply channels until the core stabilizes.",
        )
        3 -> ArenaVisualSpec.BalancePick(
            caption = "Balance scales — keep the heavier signal on top.",
        )
        4 -> ArenaVisualSpec.LogicGate(
            expression = "n % 2",
            caption = "Parity gate — only even waves pass the neon barrier.",
        )
        5 -> ArenaVisualSpec.LogicGate(
            expression = "| n |",
            caption = "Absolute corridor — fold negatives back toward the center line.",
        )
        6 -> ArenaVisualSpec.TwinMatch(
            left = "s",
            right = "t",
            caption = "Echo chamber — both strings must ring the same to unlock.",
        )
        7 -> ArenaVisualSpec.LogicGate(
            expression = "len > 0",
            caption = "Cargo bay — confirm the crate stack isn’t empty.",
        )
        8 -> ArenaVisualSpec.BinaryFusion(
            left = "a>0",
            opSymbol = "∧",
            right = "b>0",
            caption = "Dual safety locks — both channels must stay hot.",
        )
        9 -> ArenaVisualSpec.BinaryFusion(
            left = "n",
            opSymbol = "×",
            right = "n",
            caption = "Resonance chamber — square the pulse.",
        )
        10 -> ArenaVisualSpec.BinaryFusion(
            left = "n",
            opSymbol = "÷",
            right = "2",
            caption = "Splitter — halve the beam cleanly.",
        )
        11 -> ArenaVisualSpec.LogicGate(
            expression = "¬ x",
            caption = "Inverter — flip the logic bit.",
        )
        else -> ArenaVisualSpec.TriplePick(
            caption = "Tri-node vault — pick the smallest crystal.",
        )
    }
}
