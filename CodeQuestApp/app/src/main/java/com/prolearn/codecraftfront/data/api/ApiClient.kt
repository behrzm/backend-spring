package com.prolearn.codecraftfront.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Единая точка входа для всех сетевых запросов приложения.
 */
object ApiClient {
    private val logging = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(FirebaseAuthInterceptor())
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    val api: CodeQuestApi by lazy {
        Log.d("ApiClient", "Initializing Retrofit with BASE_URL: ${ServerConfig.BASE_URL}")
        Retrofit.Builder()
            .baseUrl(ServerConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CodeQuestApi::class.java)
    }
}
