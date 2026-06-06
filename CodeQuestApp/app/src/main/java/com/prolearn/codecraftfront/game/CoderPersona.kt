package com.prolearn.codecraftfront.game

/**
 * Visual persona tier (1–5) on Home. Bonuses are not applied.
 * GIF: assets/coder_personas/level_1.gif … level_5.gif
 * User game level 6+ keeps tier 5 persona until more GIFs are added.
 */
data class CoderPersona(
    val tier: Int,
    val title: String,
    val appearance: String,
    val setting: String,
    val animationHint: String,
    val icon: String,
    val gifAssetPath: String,
)

object CoderPersonas {
    const val MAX_TIER = 5

    /** Persona tier 1–5; XP level above 5 uses tier 5. */
    fun tierForUserLevel(userLevel: Int): Int = userLevel.coerceIn(1, MAX_TIER)

    fun forLevel(userLevel: Int): CoderPersona = all[tierForUserLevel(userLevel) - 1]

    private val all = listOf(
        CoderPersona(
            tier = 1,
            title = "Школьник-обучающийся",
            appearance = "10–12 лет, футболка и джинсы, короткие волосы",
            setting = "Детская, учебники на столе, настольная лампа",
            animationHint = "Смотрит в телефон, улыбается, поднимает руку",
            icon = "📱",
            gifAssetPath = "coder_personas/level_1.gif",
        ),
        CoderPersona(
            tier = 2,
            title = "Школьник с ноутбуком",
            appearance = "Тот же школьник, рюкзак, футболка с Code/Python",
            setting = "Парта в школе, ноутбук, чай/кофе",
            animationHint = "Печатает, экран светится, показывает на код",
            icon = "💻",
            gifAssetPath = "coder_personas/level_2.gif",
        ),
        CoderPersona(
            tier = 3,
            title = "Студент",
            appearance = "18–20 лет, худи или джинсовка, очки",
            setting = "Университетская аудитория, проектор, ноутбуки",
            animationHint = "Листает лекцию, делает заметки, объясняет соседу",
            icon = "🎓",
            gifAssetPath = "coder_personas/level_3.gif",
        ),
        CoderPersona(
            tier = 4,
            title = "Студент с компьютером",
            appearance = "Casual: рубашка и брюки, часы",
            setting = "Дома: два монитора, мех. клавиатура, постеры",
            animationHint = "Быстро печатает, код на экранах, кивает",
            icon = "🖥️",
            gifAssetPath = "coder_personas/level_4.gif",
        ),
        CoderPersona(
            tier = 5,
            title = "Студент на работе",
            appearance = "21–23 года, business casual",
            setting = "IT-офис, стекло, мониторы, коллеги",
            animationHint = "Обсуждает код, показывает экран, презентация",
            icon = "🏢",
            gifAssetPath = "coder_personas/level_5.gif",
        ),
    )
}
