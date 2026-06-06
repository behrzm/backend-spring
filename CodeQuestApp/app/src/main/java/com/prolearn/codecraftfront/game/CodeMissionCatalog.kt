package com.prolearn.codecraftfront.game

import com.prolearn.codecraftfront.ui.components.CodeSyntaxProfile

/**
 * Each mission pairs a real coding exercise (language-specific) with the arena celebration script.
 * Solution checks use normalized text + regex — flexible on whitespace but strict on intent.
 */
data class ResolvedCodeMission(
    val missionTitle: String,
    val missionStory: String,
    val starterCode: String,
    val playMission: PlayMission,
    val celebrationScript: String,
    val syntaxProfile: CodeSyntaxProfile,
    val validateSolution: (String) -> Boolean,
    val arenaVisual: ArenaVisualSpec,
)

private data class MissionTemplate(
    val title: String,
    val story: String,
    val starterCode: String,
    val syntaxProfile: CodeSyntaxProfile,
    val validate: (String) -> Boolean,
)

fun resolveCodeMission(language: String, track: String, levelId: Int): ResolvedCodeMission {
    val langKey = language.trim().lowercase().let { raw ->
        when {
            raw.contains("python") -> "python"
            raw.contains("javascript") || raw == "js" -> "javascript"
            raw.contains("kotlin") -> "kotlin"
            raw.contains("java") -> "java"
            else -> "python"
        }
    }
    val trackKey = when {
        track.equals("daily", ignoreCase = true) -> "daily"
        track.equals("advanced", ignoreCase = true) -> "advanced"
        else -> "beginner"
    }
    val lid = levelId.coerceIn(1, 12)
    val effectiveTrack = if (trackKey == "daily") "beginner" else trackKey

    val template = when (langKey) {
        "python" -> pythonMission(effectiveTrack, lid)
        "javascript" -> javascriptMission(lid)
        "kotlin" -> kotlinMission(lid)
        "java" -> javaMission(lid)
        else -> pythonMission(effectiveTrack, lid)
    }

    val playMission = buildMissionFromSeed(langKey, trackKey, lid)
    val celebrationScript = buildCelebrationScript(playMission)

    val prefix = when (trackKey) {
        "daily" -> "[Daily bonus] "
        "advanced" -> "⚡ Advanced · "
        else -> ""
    }

    val langLabel = langKey.replaceFirstChar { it.uppercase() }

    return ResolvedCodeMission(
        missionTitle = "$prefix$langLabel · ${template.title}",
        missionStory = template.story,
        starterCode = template.starterCode,
        playMission = playMission,
        celebrationScript = celebrationScript,
        syntaxProfile = template.syntaxProfile,
        validateSolution = template.validate,
        arenaVisual = arenaVisualForLevel(lid),
    )
}

/* ---------- Normalization & checks ---------- */

private fun norm(code: String): String {
    var s = code
    s = s.replace(Regex("/\\*[\\s\\S]*?\\*/"), " ")
    s = s.replace(Regex("//[^\\n]*"), " ")
    s = s.replace(Regex("#[^\\n]*"), " ")
    return s.lowercase().replace(Regex("\\s+"), " ").trim()
}

private fun hasReturn(code: String): Boolean = norm(code).contains("return")

private fun pySum(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+a\\s*\\+\\s*b|return\\s+b\\s*\\+\\s*a|return\\s*\\(\\s*a\\s*\\+\\s*b\\s*\\)|return\\s*\\(\\s*b\\s*\\+\\s*a\\s*\\)").containsMatchIn(s)
}

private fun pyMul(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+a\\s*\\*\\s*b|return\\s+b\\s*\\*\\s*a|return\\s*\\(\\s*a\\s*\\*\\s*b\\s*\\)").containsMatchIn(s)
}

private fun pyMax(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+max\\s*\\(\\s*a\\s*,\\s*b\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+a\\s+if\\s+a\\s*>\\s*b\\s+else\\s+b").containsMatchIn(s) ||
        Regex("return\\s+b\\s+if\\s+a\\s*>\\s*b\\s+else\\s+a").containsMatchIn(s)
}

private fun pyEven(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return s.contains("%") && s.contains("2") && (s.contains("==") || s.contains("== 0")) &&
        (s.contains("n") || s.contains("( n )"))
}

private fun pyAbs(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+abs\\s*\\(\\s*n\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+n\\s+if\\s+n\\s*>\\s*=\\s*0\\s+else\\s+-\\s*n").containsMatchIn(s) ||
        Regex("return\\s+-\\s*n\\s+if\\s+n\\s*<\\s*0\\s+else\\s+n").containsMatchIn(s)
}

private fun pyStrEq(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return s.contains("s") && s.contains("t") &&
        (s.contains("s == t") || s.contains("s==t"))
}

private fun pyListNonempty(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return (s.contains("len") && s.contains("items") && (s.contains("> 0") || s.contains("!= 0"))) ||
        Regex("return\\s+bool\\s*\\(\\s*items\\s*\\)").containsMatchIn(s)
}

private fun pyBothPositive(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return s.contains("a") && s.contains("b") && s.contains("and") && s.contains(">") && s.contains("0")
}

private fun pySquare(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+n\\s*\\*\\s*\\*\\s*2|return\\s+n\\s*\\*\\s*n|return\\s+pow\\s*\\(\\s*n\\s*,\\s*2\\s*\\)").containsMatchIn(s)
}

private fun pyHalf(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+n\\s*//\\s*2|return\\s+n\\s*/\\s*2").containsMatchIn(s)
}

private fun pyNegate(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+not\\s+x|return\\s+not\\s*\\(\\s*x\\s*\\)").containsMatchIn(s)
}

private fun pyMin3(code: String): Boolean {
    val s = norm(code)
    if (!hasReturn(code)) return false
    return Regex("return\\s+min\\s*\\(\\s*a\\s*,\\s*b\\s*,\\s*c\\s*\\)").containsMatchIn(s) ||
        Regex("min\\s*\\(\\s*min\\s*\\(\\s*a\\s*,\\s*b\\s*\\)\\s*,\\s*c\\s*\\)").containsMatchIn(s)
}

private fun jsSum(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+a\\s*\\+\\s*b|return\\s+b\\s*\\+\\s*a").containsMatchIn(s)
}

private fun jsMul(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+a\\s*\\*\\s*b|return\\s+b\\s*\\*\\s*a").containsMatchIn(s)
}

private fun jsMax(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.max\\s*\\(\\s*a\\s*,\\s*b\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+a\\s*>\\s*b\\s*\\?\\s*a\\s*:\\s*b").containsMatchIn(s)
}

private fun jsEven(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("%") && s.contains("2") && (s.includesTripleEqOrEq())
}

private fun String.includesTripleEqOrEq(): Boolean = contains("===") || contains("==")

private fun jsAbs(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.abs\\s*\\(\\s*n\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+n\\s*<\\s*0\\s*\\?\\s*-n\\s*:\\s*n").containsMatchIn(s)
}

private fun jsStrEq(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+s\\s*===\\s*t|return\\s+s\\s*==\\s*t").containsMatchIn(s)
}

private fun jsArrLen(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("items.length") && (s.contains("> 0") || s.includesTripleEqOrEq())
}

private fun jsBothPositive(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("a") && s.contains("b") && s.contains("&&") && s.contains(">")
}

private fun jsSquare(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+n\\s*\\*\\s*n|return\\s+math\\.pow\\s*\\(\\s*n\\s*,\\s*2\\s*\\)").containsMatchIn(s)
}

private fun jsHalf(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.floor\\s*\\(\\s*n\\s*/\\s*2\\s*\\)|return\\s+n\\s*/\\s*2").containsMatchIn(s)
}

private fun jsNot(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+!\\s*x|return\\s+!\\s*\\(\\s*x\\s*\\)").containsMatchIn(s)
}

private fun jsMin3(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.min\\s*\\(\\s*a\\s*,\\s*b\\s*,\\s*c\\s*\\)").containsMatchIn(s) ||
        Regex("math\\.min\\s*\\(\\s*math\\.min\\s*\\(\\s*a\\s*,\\s*b\\s*\\)\\s*,\\s*c\\s*\\)").containsMatchIn(s)
}

private fun ktSum(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+a\\s*\\+\\s*b|return\\s+b\\s*\\+\\s*a").containsMatchIn(s)
}

private fun ktMul(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+a\\s*\\*\\s*b|return\\s+b\\s*\\*\\s*a").containsMatchIn(s)
}

private fun ktMax(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+maxof\\s*\\(\\s*a\\s*,\\s*b\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+if\\s*\\(\\s*a\\s*>\\s*b\\s*\\)\\s+a\\s+else\\s+b").containsMatchIn(s)
}

private fun ktEven(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("%") && s.contains("2") && s.contains("==")
}

private fun ktAbs(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+kotlin\\.math\\.abs\\s*\\(\\s*n\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+abs\\s*\\(\\s*n\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+if\\s*\\(\\s*n\\s*<\\s*0\\s*\\)\\s+-\\s*n\\s+else\\s+n").containsMatchIn(s)
}

private fun ktStrEq(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+s\\s*==\\s*t").containsMatchIn(s)
}

private fun ktListNonempty(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("isnotempty()") || (s.contains("size") && s.contains(">") && s.contains("0"))
}

private fun ktBothPositive(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("a") && s.contains("b") && s.contains("&&") && s.contains(">")
}

private fun ktSquare(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+n\\s*\\*\\s*n").containsMatchIn(s)
}

private fun ktHalf(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+n\\s*/\\s*2").containsMatchIn(s)
}

private fun ktNot(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+!\\s*x").containsMatchIn(s)
}

private fun ktMin3(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+minof\\s*\\(\\s*a\\s*,\\s*b\\s*,\\s*c\\s*\\)").containsMatchIn(s) ||
        Regex("minof\\s*\\(\\s*minof\\s*\\(\\s*a\\s*,\\s*b\\s*\\)\\s*,\\s*c\\s*\\)").containsMatchIn(s)
}

private fun javaSum(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+a\\s*\\+\\s*b|return\\s+b\\s*\\+\\s*a").containsMatchIn(s)
}

private fun javaMul(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+a\\s*\\*\\s*b|return\\s+b\\s*\\*\\s*a").containsMatchIn(s)
}

private fun javaMax(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.max\\s*\\(\\s*a\\s*,\\s*b\\s*\\)").containsMatchIn(s)
}

private fun javaEven(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("%") && s.contains("2") && s.contains("==")
}

private fun javaAbs(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.abs\\s*\\(\\s*n\\s*\\)").containsMatchIn(s)
}

private fun javaStrEq(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains(".equals(") || Regex("return\\s+s\\s*==\\s*t").containsMatchIn(s)
}

private fun javaArrLen(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains(".length") && s.contains(">")
}

private fun javaBothPositive(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return s.contains("a") && s.contains("b") && s.contains("&&") && s.contains(">")
}

private fun javaSquare(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+n\\s*\\*\\s*n").containsMatchIn(s)
}

private fun javaHalf(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+n\\s*/\\s*2").containsMatchIn(s)
}

private fun javaNot(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+!\\s*x").containsMatchIn(s)
}

private fun javaMin3(code: String): Boolean {
    val s = norm(code)
    if (!s.contains("return")) return false
    return Regex("return\\s+math\\.min\\s*\\(\\s*math\\.min\\s*\\(\\s*a\\s*,\\s*b\\s*\\)\\s*,\\s*c\\s*\\)").containsMatchIn(s) ||
        Regex("return\\s+math\\.min\\s*\\(\\s*a\\s*,\\s*math\\.min\\s*\\(\\s*b\\s*,\\s*c\\s*\\)\\s*\\)").containsMatchIn(s)
}

/* ---------- Python ---------- */

private fun pythonMission(track: String, levelId: Int): MissionTemplate {
    val hint = if (track == "advanced") {
        "Use idiomatic Python — keep it short."
    } else {
        "Fill in only the missing logic."
    }
    return when (levelId) {
        1 -> MissionTemplate(
            title = "Sum two numbers",
            story = "Implement add(a, b) so it returns a + b. When your logic is correct, the fusion lane charges and the level clears. $hint",
            starterCode = """
                def add(a, b):
                    # Return the sum of a and b
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pySum,
        )

        2 -> MissionTemplate(
            title = "Multiply",
            story = "Return the product of a and b.",
            starterCode = """
                def multiply(a, b):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyMul,
        )

        3 -> MissionTemplate(
            title = "Pick the larger value",
            story = "Return the greater of a and b (use max() or an if-expression).",
            starterCode = """
                def larger(a, b):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyMax,
        )

        4 -> MissionTemplate(
            title = "Even or odd",
            story = "Return True if n is divisible by 2, otherwise False.",
            starterCode = """
                def is_even(n):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyEven,
        )

        5 -> MissionTemplate(
            title = "Absolute beginner",
            story = "Return the absolute value of n without using abs(), or use abs(n) — both work.",
            starterCode = """
                def absolute(n):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyAbs,
        )

        6 -> MissionTemplate(
            title = "String match",
            story = "Return True if strings s and t are equal.",
            starterCode = """
                def same_word(s, t):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyStrEq,
        )

        7 -> MissionTemplate(
            title = "Non-empty list",
            story = "Return True if items has at least one element.",
            starterCode = """
                def has_items(items):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyListNonempty,
        )

        8 -> MissionTemplate(
            title = "Double positive",
            story = "Return True only if both a and b are strictly greater than 0.",
            starterCode = """
                def both_positive(a, b):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyBothPositive,
        )

        9 -> MissionTemplate(
            title = "Square it",
            story = "Return n squared.",
            starterCode = """
                def square(n):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pySquare,
        )

        10 -> MissionTemplate(
            title = "Halfway",
            story = "Return integer half of n (use // or floor division).",
            starterCode = """
                def half(n):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyHalf,
        )

        11 -> MissionTemplate(
            title = "Flip the flag",
            story = "Return the logical negation of x.",
            starterCode = """
                def negate(x):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyNegate,
        )

        else -> MissionTemplate(
            title = "Minimum of three",
            story = "Return the smallest of a, b, and c.",
            starterCode = """
                def smallest(a, b, c):
                    
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Python,
            validate = ::pyMin3,
        )
    }
}

/* ---------- JavaScript ---------- */

private fun javascriptMission(levelId: Int): MissionTemplate {
    return when (levelId) {
        1 -> MissionTemplate(
            title = "Sum two numbers",
            story = "Complete function add(a,b) so it returns a + b.",
            starterCode = """
                function add(a, b) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsSum,
        )

        2 -> MissionTemplate(
            title = "Multiply",
            story = "Return a * b.",
            starterCode = """
                function multiply(a, b) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsMul,
        )

        3 -> MissionTemplate(
            title = "Maximum",
            story = "Return the larger of a and b (Math.max or ternary).",
            starterCode = """
                function larger(a, b) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsMax,
        )

        4 -> MissionTemplate(
            title = "Even check",
            story = "Return true if n is divisible by 2.",
            starterCode = """
                function isEven(n) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsEven,
        )

        5 -> MissionTemplate(
            title = "Absolute value",
            story = "Return absolute value of n.",
            starterCode = """
                function absolute(n) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsAbs,
        )

        6 -> MissionTemplate(
            title = "Strict equality",
            story = "Return whether strings s and t are the same (use ===).",
            starterCode = """
                function sameText(s, t) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsStrEq,
        )

        7 -> MissionTemplate(
            title = "Array length",
            story = "Return true if items array has at least one element.",
            starterCode = """
                function hasItems(items) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsArrLen,
        )

        8 -> MissionTemplate(
            title = "Both positive",
            story = "Return true only if a > 0 && b > 0.",
            starterCode = """
                function bothPositive(a, b) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsBothPositive,
        )

        9 -> MissionTemplate(
            title = "Square",
            story = "Return n * n.",
            starterCode = """
                function square(n) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsSquare,
        )

        10 -> MissionTemplate(
            title = "Half",
            story = "Return integer half of n.",
            starterCode = """
                function half(n) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsHalf,
        )

        11 -> MissionTemplate(
            title = "Logical NOT",
            story = "Return !x.",
            starterCode = """
                function negate(x) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsNot,
        )

        else -> MissionTemplate(
            title = "Min of three",
            story = "Return the smallest of a, b, c using Math.min.",
            starterCode = """
                function smallest(a, b, c) {
                  
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.JavaScript,
            validate = ::jsMin3,
        )
    }
}

/* ---------- Kotlin ---------- */

private fun kotlinMission(levelId: Int): MissionTemplate {
    return when (levelId) {
        1 -> MissionTemplate(
            title = "Sum two numbers",
            story = "Implement add so it returns a + b.",
            starterCode = """
                fun add(a: Int, b: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktSum,
        )

        2 -> MissionTemplate(
            title = "Multiply",
            story = "Return a * b.",
            starterCode = """
                fun multiply(a: Int, b: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktMul,
        )

        3 -> MissionTemplate(
            title = "Maximum",
            story = "Return maxOf(a,b) or equivalent if/else.",
            starterCode = """
                fun larger(a: Int, b: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktMax,
        )

        4 -> MissionTemplate(
            title = "Even check",
            story = "Return true if n % 2 == 0.",
            starterCode = """
                fun isEven(n: Int): Boolean {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktEven,
        )

        5 -> MissionTemplate(
            title = "Absolute value",
            story = "Return kotlin.math.abs(n) or branch on sign.",
            starterCode = """
                fun absolute(n: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktAbs,
        )

        6 -> MissionTemplate(
            title = "String equality",
            story = "Return s == t.",
            starterCode = """
                fun sameText(s: String, t: String): Boolean {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktStrEq,
        )

        7 -> MissionTemplate(
            title = "Collection check",
            story = "Return true if items is not empty.",
            starterCode = """
                fun hasItems(items: List<Int>): Boolean {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktListNonempty,
        )

        8 -> MissionTemplate(
            title = "Both positive",
            story = "Return a > 0 && b > 0.",
            starterCode = """
                fun bothPositive(a: Int, b: Int): Boolean {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktBothPositive,
        )

        9 -> MissionTemplate(
            title = "Square",
            story = "Return n * n.",
            starterCode = """
                fun square(n: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktSquare,
        )

        10 -> MissionTemplate(
            title = "Half",
            story = "Return n / 2 (integer division).",
            starterCode = """
                fun half(n: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktHalf,
        )

        11 -> MissionTemplate(
            title = "Negate boolean",
            story = "Return !x.",
            starterCode = """
                fun negate(x: Boolean): Boolean {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktNot,
        )

        else -> MissionTemplate(
            title = "Minimum of three",
            story = "Return minOf(a,b,c).",
            starterCode = """
                fun smallest(a: Int, b: Int, c: Int): Int {
                    
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Kotlin,
            validate = ::ktMin3,
        )
    }
}

/* ---------- Java ---------- */

private fun javaMission(levelId: Int): MissionTemplate {
    return when (levelId) {
        1 -> MissionTemplate(
            title = "Sum two numbers",
            story = "Return a + b inside Solution.add.",
            starterCode = """
                public class Solution {
                    public static int add(int a, int b) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaSum,
        )

        2 -> MissionTemplate(
            title = "Multiply",
            story = "Return the product.",
            starterCode = """
                public class Solution {
                    public static int multiply(int a, int b) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaMul,
        )

        3 -> MissionTemplate(
            title = "Maximum",
            story = "Use Math.max(a, b).",
            starterCode = """
                public class Solution {
                    public static int larger(int a, int b) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaMax,
        )

        4 -> MissionTemplate(
            title = "Even check",
            story = "Return true when n % 2 == 0.",
            starterCode = """
                public class Solution {
                    public static boolean isEven(int n) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaEven,
        )

        5 -> MissionTemplate(
            title = "Absolute value",
            story = "Return Math.abs(n).",
            starterCode = """
                public class Solution {
                    public static int absolute(int n) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaAbs,
        )

        6 -> MissionTemplate(
            title = "String equality",
            story = "Return whether s equals t (equals or ==).",
            starterCode = """
                public class Solution {
                    public static boolean sameText(String s, String t) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaStrEq,
        )

        7 -> MissionTemplate(
            title = "Array length",
            story = "Return arr.length > 0.",
            starterCode = """
                public class Solution {
                    public static boolean hasItems(int[] arr) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaArrLen,
        )

        8 -> MissionTemplate(
            title = "Both positive",
            story = "Return a > 0 && b > 0.",
            starterCode = """
                public class Solution {
                    public static boolean bothPositive(int a, int b) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaBothPositive,
        )

        9 -> MissionTemplate(
            title = "Square",
            story = "Return n * n.",
            starterCode = """
                public class Solution {
                    public static int square(int n) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaSquare,
        )

        10 -> MissionTemplate(
            title = "Half",
            story = "Return n / 2.",
            starterCode = """
                public class Solution {
                    public static int half(int n) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaHalf,
        )

        11 -> MissionTemplate(
            title = "Logical NOT",
            story = "Return !x.",
            starterCode = """
                public class Solution {
                    public static boolean negate(boolean x) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaNot,
        )

        else -> MissionTemplate(
            title = "Minimum of three",
            story = "Nest Math.min twice.",
            starterCode = """
                public class Solution {
                    public static int smallest(int a, int b, int c) {
                        
                    }
                }
            """.trimIndent(),
            syntaxProfile = CodeSyntaxProfile.Java,
            validate = ::javaMin3,
        )
    }
}
