package com.prolearn.codecraftfront.config

import com.prolearn.codecraftfront.data.api.CodeQuestApi
import com.prolearn.codecraftfront.data.api.FirebaseAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ПРИМЕЧАНИЕ: Если используете реальное устройство, замените 10.0.2.2 на ваш локальный IP
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(FirebaseAuthInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideCodeQuestApi(okHttpClient: OkHttpClient): CodeQuestApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CodeQuestApi::class.java)
    }
}
