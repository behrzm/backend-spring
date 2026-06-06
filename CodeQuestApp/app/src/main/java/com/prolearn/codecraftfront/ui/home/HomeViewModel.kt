package com.prolearn.codecraftfront.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.data.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val xp: Int = 0,
    val streak: Int = 0,
    val level: Int = 1,
    val wins: Int = 0,
    val elo: Int = 1000,
    val rewardClaimedToday: Boolean = false,
    val dailyChallengeCompletedToday: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {
    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        Log.d("HomeViewModel", "Refreshing home data...")
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = api.getProfile()
                if (response.isSuccessful) {
                    val p = response.body()!!
                    val today = LocalDate.now().toString()
                    _uiState.update {
                        it.copy(
                            xp = p.xp,
                            level = p.level,
                            streak = p.streak,
                            wins = p.wins,
                            elo = p.elo,
                            rewardClaimedToday = p.lastRewardClaimDate == today,
                            dailyChallengeCompletedToday = p.lastDailyChallengeDate == today,
                            isLoading = false
                        )
                    }
                } else {
                    Log.e("HomeViewModel", "Error fetching profile: ${response.code()}")
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun claimDailyReward() {
        if (_uiState.value.rewardClaimedToday) return
        
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Attempting to claim reward...")
                val response = api.claimDailyReward()
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "Reward claimed successfully!")
                    refresh()
                } else {
                    Log.e("HomeViewModel", "Failed to claim reward: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Claim error: ${e.message}")
            }
        }
    }
}
