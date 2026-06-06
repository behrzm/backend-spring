package com.codequest.security;

public class SecurityContext {
    private static final ThreadLocal<String> userIdThreadLocal = new ThreadLocal<>();

    public static void setUserId(String userId) {
        userIdThreadLocal.set(userId);
    }

    public static String getUserId() {
        return userIdThreadLocal.get();
    }

    public static void clear() {
        userIdThreadLocal.remove();
    }
}

