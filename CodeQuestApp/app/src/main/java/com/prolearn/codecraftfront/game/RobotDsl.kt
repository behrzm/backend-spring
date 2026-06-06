package com.prolearn.codecraftfront.game

/**
 * Robot mini-DSL for the arena celebration — not shown to the player; generated from the mission grid.
 */
enum class RobotDirection {
    UP,
    RIGHT,
    DOWN,
    LEFT,
}

data class PlayMission(
    val startX: Int,
    val startY: Int,
    val targetX: Int,
    val targetY: Int,
    val direction: RobotDirection,
) {
    fun toRobotState(): RobotState = RobotState(
        x = startX,
        y = startY,
        direction = direction,
        collectedCoin = false,
    )
}

data class RobotState(
    val x: Int,
    val y: Int,
    val direction: RobotDirection,
    val collectedCoin: Boolean,
)

sealed interface DslCommand {
    data class Move(val steps: Int) : DslCommand
    data class Turn(val direction: String) : DslCommand
    data object Collect : DslCommand
}

private enum class PathDir {
    U,
    R,
    D,
    L,
}

private fun RobotDirection.toPathDir(): PathDir = when (this) {
    RobotDirection.UP -> PathDir.U
    RobotDirection.RIGHT -> PathDir.R
    RobotDirection.DOWN -> PathDir.D
    RobotDirection.LEFT -> PathDir.L
}

private fun turnLeft(d: PathDir): PathDir = when (d) {
    PathDir.U -> PathDir.L
    PathDir.L -> PathDir.D
    PathDir.D -> PathDir.R
    PathDir.R -> PathDir.U
}

private fun turnRight(d: PathDir): PathDir = when (d) {
    PathDir.U -> PathDir.R
    PathDir.R -> PathDir.D
    PathDir.D -> PathDir.L
    PathDir.L -> PathDir.U
}

private fun leftTurns(from: PathDir, to: PathDir): Int {
    val a = from.ordinal
    val b = to.ordinal
    return (b - a + 4) % 4
}

/**
 * Builds a minimal turn/move/collect script so the robot reaches the coin from the mission start pose.
 */
fun buildCelebrationScript(mission: PlayMission): String {
    if (mission.startX == mission.targetX && mission.startY == mission.targetY) {
        return "collect()"
    }

    var x = mission.startX
    var y = mission.startY
    var d = mission.direction.toPathDir()
    val lines = mutableListOf<String>()
    val tx = mission.targetX
    val ty = mission.targetY

    fun face(want: PathDir) {
        val lt = leftTurns(d, want)
        val rt = (4 - lt) % 4
        if (lt <= rt) {
            repeat(lt) {
                lines += "turn(left)"
                d = turnLeft(d)
            }
        } else {
            repeat(rt) {
                lines += "turn(right)"
                d = turnRight(d)
            }
        }
    }

    val dx = tx - x
    if (dx != 0) {
        val want = if (dx > 0) PathDir.R else PathDir.L
        face(want)
        lines += "move(${kotlin.math.abs(dx)})"
        x = tx
    }

    val dy = ty - y
    if (dy != 0) {
        val want = if (dy > 0) PathDir.D else PathDir.U
        face(want)
        lines += "move(${kotlin.math.abs(dy)})"
        y = ty
    }

    lines += "collect()"
    return lines.joinToString("\n")
}

fun buildMissionFromSeed(language: String, track: String, levelId: Int): PlayMission {
    fun modPositive(value: Int, m: Int): Int = ((value % m) + m) % m

    val advanced = track.equals("advanced", ignoreCase = true)
    val bonus = if (advanced) 2 else 0
    val seed = language.lowercase().hashCode() xor "track:$track".hashCode() xor (levelId * 7919)
    var tx = modPositive(seed + levelId + bonus, 5)
    var ty = modPositive(seed / 7 + levelId * 2 + bonus, 5)
    if (tx == 0 && ty == 0) {
        tx = modPositive(levelId + 1, 4) + 1
        ty = modPositive(levelId + 2, 4) + 1
    }
    val dirOrdinal = modPositive(seed, 4)
    return PlayMission(
        startX = 0,
        startY = 0,
        targetX = tx.coerceIn(0, 4),
        targetY = ty.coerceIn(0, 4),
        direction = RobotDirection.entries[dirOrdinal],
    )
}

fun parseCommands(code: String): Result<List<DslCommand>> = runCatching {
    code
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { line ->
            when {
                line.startsWith("move(") && line.endsWith(")") -> {
                    val steps = line.removePrefix("move(").removeSuffix(")").trim().toIntOrNull()
                        ?: error("move() expects integer steps.")
                    DslCommand.Move(steps)
                }

                line.startsWith("turn(") && line.endsWith(")") -> {
                    val side = line.removePrefix("turn(").removeSuffix(")").trim().lowercase()
                    if (side != "left" && side != "right") {
                        error("turn() expects left or right.")
                    }
                    DslCommand.Turn(side)
                }

                line == "collect()" -> DslCommand.Collect
                else -> error("Unknown command: $line")
            }
        }
}

fun applyCommand(
    state: RobotState,
    command: DslCommand,
    mission: PlayMission,
): Result<RobotState> = runCatching {
    when (command) {
        is DslCommand.Move -> {
            var x = state.x
            var y = state.y
            repeat(command.steps) {
                when (state.direction) {
                    RobotDirection.UP -> y--
                    RobotDirection.RIGHT -> x++
                    RobotDirection.DOWN -> y++
                    RobotDirection.LEFT -> x--
                }
                if (x !in 0..4 || y !in 0..4) error("Robot crashed into wall.")
            }
            state.copy(x = x, y = y)
        }

        is DslCommand.Turn -> {
            val next = when (state.direction) {
                RobotDirection.UP -> if (command.direction == "left") RobotDirection.LEFT else RobotDirection.RIGHT
                RobotDirection.RIGHT -> if (command.direction == "left") RobotDirection.UP else RobotDirection.DOWN
                RobotDirection.DOWN -> if (command.direction == "left") RobotDirection.RIGHT else RobotDirection.LEFT
                RobotDirection.LEFT -> if (command.direction == "left") RobotDirection.DOWN else RobotDirection.UP
            }
            state.copy(direction = next)
        }

        DslCommand.Collect -> {
            if (state.x == mission.targetX && state.y == mission.targetY) {
                state.copy(collectedCoin = true)
            } else {
                error("No coin at current position.")
            }
        }
    }
}
