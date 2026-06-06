package com.codequest.security

object SecurityContext {
    private val userIdThreadLocal = ThreadLocal<String>()

    fun setUserId(userId: String) {
        userIdThreadLocal.set(userId)
    }

    fun getUserId(): String? {
        return userIdThreadLocal.get()
    }

    fun clear() {
        userIdThreadLocal.remove()
    }
}

