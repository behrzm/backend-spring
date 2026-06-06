package com.prolearn.codecraftfront.ui.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.BuildConfig
import com.prolearn.codecraftfront.data.api.GroqApi
import com.prolearn.codecraftfront.data.api.GroqChatRequest
import com.prolearn.codecraftfront.data.api.GroqMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class HintMessage(
    val author: Author,
    val text: String,
) {
    enum class Author { Player, Assistant }
}

data class AiAssistantUiState(
    val isOpen: Boolean = false,
    val isLoading: Boolean = false,
    val messages: List<HintMessage> = emptyList(),
    val errorMessage: String? = null,
)

data class HintRequestContext(
    val language: String,
    val track: String,
    val levelId: Int,
    val storyPrompt: String,
    val playerCode: String,
    val lastError: String?,
    val failedAttempts: Int,
)

class AiAssistantViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("GroqNetwork", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val groqApi: GroqApi = Retrofit.Builder()
        .baseUrl("https://api.groq.com/openai/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GroqApi::class.java)

    fun open() {
        _uiState.update { it.copy(isOpen = true, errorMessage = null) }
    }

    fun close() {
        _uiState.update { it.copy(isOpen = false) }
    }

    fun resetHistory() {
        _uiState.update { it.copy(messages = emptyList(), errorMessage = null) }
    }

    fun requestHint(context: HintRequestContext) {
        // Очищаем ключ от кавычек, пробелов и случайных префиксов
        val rawKey = BuildConfig.GROQ_API_KEY
        val apiKey = rawKey.trim()
            .removeSurrounding("\"")
            .removeSurrounding("'")
            .removePrefix("Bearer ")

        if (apiKey.isBlank() || apiKey == "null") {
            _uiState.update {
                it.copy(
                    isOpen = true,
                    errorMessage = "Error: Groq API key is empty. Please check local.properties, add GROQ_API_KEY=gsk_... and SYNC Gradle (Elephant icon).",
                )
            }
            return
        }

        val playerMessage = buildPlayerMessage(context)
        _uiState.update {
            it.copy(
                isOpen = true,
                isLoading = true,
                errorMessage = null,
                messages = it.messages + HintMessage(HintMessage.Author.Player, playerMessage),
            )
        }
        
        viewModelScope.launch {
            try {
                // Используем более легкую модель для быстрой проверки
                val request = GroqChatRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = listOf(
                        GroqMessage(role = "system", content = "You are a coding tutor. Give a single helpful hint."),
                        GroqMessage(role = "user", content = buildPrompt(context))
                    )
                )
                
                val response = groqApi.generateChatCompletion("Bearer $apiKey", request)
                
                if (response.isSuccessful) {
                    val reply = response.body()?.choices?.firstOrNull()?.message?.content
                    val cleaned = reply?.ifBlank { "No hint generated." } ?: "AI returned empty response."
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = it.messages + HintMessage(HintMessage.Author.Assistant, cleaned),
                        )
                    }
                } else {
                    val errorDetail = response.errorBody()?.string()
                    Log.e("GroqNetwork", "Full error: $errorDetail")
                    
                    val msg = when(response.code()) {
                        401 -> "401 Unauthorized: Your API Key is invalid or expired. Check local.properties."
                        429 -> "429 Rate Limit: Too many requests to Groq."
                        else -> "Groq Error ${response.code()}: Please check Logcat for details."
                    }
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = msg,
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("GroqNetwork", "Request failed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Connection error. Check your internet.",
                    )
                }
            }
        }
    }

    private fun buildPlayerMessage(context: HintRequestContext): String = buildString {
        append("Hint for Level ")
        append(context.levelId)
        append(" (")
        append(context.language)
        append(")")
    }

    private fun buildPrompt(context: HintRequestContext): String {
        return "Task: ${context.storyPrompt}\nMy code: ${context.playerCode}\nGive me one short hint in English."
    }
}
