package com.prolearn.codecraftfront.ui.leaderboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.prolearn.codecraftfront.data.ApiLeaderboardRepository
import com.prolearn.codecraftfront.data.LeaderboardEntry
import com.prolearn.codecraftfront.data.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val entries: List<LeaderboardEntry> = emptyList(),
    val currentUserId: String? = null,
    val isMock: Boolean = false,
    val errorMessage: String? = null,
)

class LeaderboardViewModel : ViewModel() {

    // Используется ApiLeaderboardRepository с встроенным API клиентом
    private val repository: LeaderboardRepository = ApiLeaderboardRepository()

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        Log.d("LeaderboardViewModel", "Refreshing leaderboard...")
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val remote = repository.fetchTop(20)
                Log.d("LeaderboardViewModel", "Loaded ${remote.size} entries")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entries = remote,
                        isMock = false,
                        currentUserId = FirebaseAuth.getInstance().currentUser?.uid,
                    )
                }
            } catch (e: Exception) {
                Log.e("LeaderboardViewModel", "Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load leaderboard: ${e.message}"
                    )
                }
            }
        }
    }
}
