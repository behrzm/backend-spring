package com.prolearn.codecraftfront.ui.cody

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.BuildConfig
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.data.api.CodyChatRequest
import com.prolearn.codecraftfront.data.api.CodyChatTurn
import com.prolearn.codecraftfront.data.api.GroqApi
import com.prolearn.codecraftfront.data.api.GroqChatRequest
import com.prolearn.codecraftfront.data.api.GroqMessage
import com.prolearn.codecraftfront.util.LanguageDetect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class CodyChatMessage(
    val author: Author,
    val text: String,
) {
    enum class Author { User, Cody }
}

data class CodyChatUiState(
    val screenContext: String = "home",
    val messages: List<CodyChatMessage> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class CodyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CodyChatUiState())
    val uiState: StateFlow<CodyChatUiState> = _uiState.asStateFlow()

    private val groqApi: GroqApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor { Log.d("CodyGroq", it) }.apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        },
                    )
                    .build(),
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApi::class.java)
    }

    fun startSession(screenContext: String) {
        if (_uiState.value.screenContext == screenContext && _uiState.value.messages.isNotEmpty()) {
            return
        }
        _uiState.value = CodyChatUiState(
            screenContext = screenContext,
            messages = buildWelcomeMessages(screenContext),
        )
    }

    fun updateInput(value: String) {
        _uiState.update { it.copy(input = value, errorMessage = null) }
    }

    fun sendMessage() {
        val text = _uiState.value.input.trim()
        if (text.isBlank() || _uiState.value.isLoading) return
        appendUserAndRequest(text)
    }

    fun sendQuickPrompt(prompt: String) {
        if (_uiState.value.isLoading) return
        appendUserAndRequest(prompt)
    }

    private fun appendUserAndRequest(text: String) {
        val withUser = _uiState.value.messages + CodyChatMessage(CodyChatMessage.Author.User, text)
        _uiState.update {
            it.copy(messages = withUser, input = "", isLoading = true, errorMessage = null)
        }
        viewModelScope.launch {
            val reply = requestReply(text, withUser)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    messages = it.messages + CodyChatMessage(CodyChatMessage.Author.Cody, reply),
                )
            }
        }
    }

    private suspend fun requestReply(userMessage: String, history: List<CodyChatMessage>): String {
        val screen = _uiState.value.screenContext
        val turns = history.takeLast(12).map { msg ->
            CodyChatTurn(
                role = if (msg.author == CodyChatMessage.Author.User) "user" else "assistant",
                content = msg.text,
            )
        }

        try {
            val response = ApiClient.api.codyChat(
                CodyChatRequest(
                    message = userMessage,
                    screenContext = screen,
                    history = turns.dropLast(1),
                ),
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (!body?.reply.isNullOrBlank()) return body!!.reply
            }
            Log.w("CodyViewModel", "Backend chat failed: ${response.code()} ${response.errorBody()?.string()}")
        } catch (e: Exception) {
            Log.w("CodyViewModel", "Backend unavailable, trying direct Groq", e)
        }

        return requestViaGroqDirect(userMessage, history, screen)
    }

    private suspend fun requestViaGroqDirect(
        userMessage: String,
        history: List<CodyChatMessage>,
        screen: String,
    ): String {
        val apiKey = BuildConfig.GROQ_API_KEY.trim().removeSurrounding("\"").removeSurrounding("'")
        if (apiKey.isBlank() || apiKey == "null") {
            return "⚠ No Groq API key. Add GROQ_API_KEY to local.properties (Android) or .env (backend) and rebuild."
        }

        val russian = LanguageDetect.isPrimarilyRussian(userMessage)
        val system = if (russian) {
            """
            Ты Cody — ИИ-напарник CodeQuest. Отвечай по-русски, кратко и дружелюбно.
            Экран: $screen.
            """.trimIndent()
        } else {
            """
            You are Cody, the CodeQuest AI companion. Reply in English, briefly and warmly.
            Screen: $screen.
            """.trimIndent()
        }

        val groqHistory = history.takeLast(10).map { msg ->
            GroqMessage(
                role = if (msg.author == CodyChatMessage.Author.User) "user" else "assistant",
                content = msg.text,
            )
        }

        return try {
            val response = groqApi.generateChatCompletion(
                "Bearer $apiKey",
                GroqChatRequest(
                    messages = listOf(GroqMessage("system", system)) + groqHistory,
                ),
            )
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: "Could not get a reply from Cody."
            } else {
                "⚠ Groq error ${response.code()}. Check your API key."
            }
        } catch (e: Exception) {
            "⚠ Cannot reach Cody. Start the backend or check your network."
        }
    }

    private fun buildWelcomeMessages(screen: String): List<CodyChatMessage> = when (screen) {
        "languages" -> {
            val quote = CodyQuotes.random()
            listOf(
                CodyChatMessage(
                    CodyChatMessage.Author.Cody,
                    "Hi! I'm Cody. I'll help you pick a language and build a learning plan.",
                ),
                CodyChatMessage(
                    CodyChatMessage.Author.Cody,
                    "Tip of the day: \"${quote.text}\" — ${quote.author}",
                ),
                CodyChatMessage(
                    CodyChatMessage.Author.Cody,
                    "Ask me about Python, JavaScript, Kotlin, or how to get started!",
                ),
            )
        }
        "play" -> listOf(
            CodyChatMessage(
                CodyChatMessage.Author.Cody,
                "What skills are we leveling up today? Pick a mission — I'm here!",
            ),
            CodyChatMessage(
                CodyChatMessage.Author.Cody,
                "I can suggest where to train or how to earn XP faster.",
            ),
        )
        "stats" -> listOf(
            CodyChatMessage(
                CodyChatMessage.Author.Cody,
                "Checking your stats is half the win! Every XP point counts.",
            ),
        )
        else -> listOf(
            CodyChatMessage(
                CodyChatMessage.Author.Cody,
                "Hi! I'm Cody, your AI partner in CodeQuest. How can I help?",
            ),
        )
    }
}
