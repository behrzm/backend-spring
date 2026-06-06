package com.prolearn.codecraftfront.ui.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.data.api.LevelProgressResponse
import com.prolearn.codecraftfront.data.api.XpHistoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class StatsUiState(
    val totalXp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val wins: Int = 0,
    val elo: Int = 1000,
    val accuracy: Float = 0f,
    val languageAccuracies: List<LanguageAccuracyData> = emptyList(),
    val streakHistory: List<StreakDayData> = emptyList(),
    val xpHistory: List<XpHistoryResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val dailyChallengeCompletedToday: Boolean = false,
)

data class LanguageAccuracyData(val name: String, val accuracy: Float, val color: String)
data class StreakDayData(val label: String, val value: Float)

class StatsViewModel : ViewModel() {
    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        Log.d("StatsViewModel", "Refreshing stats via ApiClient...")
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val profileRes = api.getProfile()
                val historyRes = api.getXpHistory(50)
                val progressRes = api.getAllLevelProgress()
                
                if (profileRes.isSuccessful && historyRes.isSuccessful && progressRes.isSuccessful) {
                    val p = profileRes.body()!!
                    val history = historyRes.body() ?: emptyList()
                    val progress = progressRes.body() ?: emptyList()
                    
                    val langAccs = calculateLanguageAccuracies(progress)
                    val streakHist = calculateStreakHistory(history)
                    val avgAcc = if (langAccs.isNotEmpty()) langAccs.map { it.accuracy }.average().toFloat() else 0f
                    val today = LocalDate.now().toString()

                    _uiState.update {
                        it.copy(
                            totalXp = p.xp,
                            level = p.level,
                            streak = p.streak ?: 0,
                            wins = p.wins ?: 0,
                            elo = p.elo,
                            accuracy = avgAcc,
                            languageAccuracies = langAccs,
                            streakHistory = streakHist,
                            xpHistory = history,
                            dailyChallengeCompletedToday = p.lastDailyChallengeDate == today,
                            isLoading = false
                        )
                    }
                    Log.d("StatsViewModel", "Stats updated successfully. XP: ${p.xp}")
                } else {
                    Log.e("StatsViewModel", "Error: ${profileRes.code()}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Server error") }
                }
            } catch (e: Exception) {
                Log.e("StatsViewModel", "Exception: ${e.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun calculateLanguageAccuracies(progress: List<LevelProgressResponse>): List<LanguageAccuracyData> {
        return progress.groupBy { it.language }.map { (lang, levels) ->
            val totalStars = levels.size * 3
            val earnedStars = levels.sumOf { it.stars ?: 0 }
            val acc = if (totalStars > 0) earnedStars.toFloat() / totalStars else 0f
            LanguageAccuracyData(lang, acc, "NeonGreen")
        }
    }

    private fun calculateStreakHistory(history: List<XpHistoryResponse>): List<StreakDayData> {
        val last7Days = (0..6).reversed().map { LocalDate.now().minusDays(it.toLong()) }
        val formatter = DateTimeFormatter.ofPattern("EEE")
        val dailyActivity = history.filter { it.createdAt != null }.groupBy { 
            if (it.createdAt!!.length >= 10) it.createdAt.substring(0, 10) else ""
        }

        return last7Days.map { date ->
            val dateStr = date.toString()
            val activityCount = dailyActivity[dateStr]?.size ?: 0
            val value = (activityCount.toFloat() / 3f).coerceAtMost(1f)
            StreakDayData(date.format(formatter), value)
        }
    }
}
