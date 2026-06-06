package com.prolearn.codecraftfront.util

/**
 * Detect whether the user message is primarily Russian (Cyrillic) or English/Latin.
 */
object LanguageDetect {
    fun isPrimarilyRussian(text: String): Boolean {
        var cyrillic = 0
        var latin = 0
        for (ch in text) {
            when {
                ch in '\u0400'..'\u04FF' -> cyrillic++
                ch.isLetter() && ch.code < 128 -> latin++
            }
        }
        return cyrillic > 0 && cyrillic >= latin
    }

    fun replyLanguageInstruction(userMessage: String): String {
        return if (isPrimarilyRussian(userMessage)) {
            "Reply in Russian."
        } else {
            "Reply in English."
        }
    }
}
