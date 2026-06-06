package com.codequest.util;

public final class LanguageDetect {
    private LanguageDetect() {}

    public static boolean isPrimarilyRussian(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        int cyrillic = 0;
        int latin = 0;
        for (char ch : text.toCharArray()) {
            if (ch >= '\u0400' && ch <= '\u04FF') {
                cyrillic++;
            } else if (Character.isLetter(ch) && ch < 128) {
                latin++;
            }
        }
        return cyrillic > 0 && cyrillic >= latin;
    }
}
