package com.prolearn.codecraftfront.data.api

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Response

class FirebaseAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser
        
        if (user == null) {
            return chain.proceed(originalRequest)
        }
        
        // Добавляем таймаут, чтобы не блокировать запрос вечно
        val token = runBlocking {
            try {
                withTimeoutOrNull(5000) {
                    user.getIdToken(false).await().token
                }
            } catch (e: Exception) {
                null
            }
        }
        
        if (token == null) {
            return chain.proceed(originalRequest)
        }
        
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(newRequest)
    }
}
