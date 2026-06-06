package com.prolearn.codecraftfront.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.prolearn.codecraftfront.data.UserDisplayNameStore
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.data.api.UpdateProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ProfileUiState(
    val xp: Int = 0,
    val level: Int = 1,
    val displayName: String? = null,
    val streak: Int = 0,
    val wins: Int = 0,
    val elo: Int = 1000,
    val rewardClaimedToday: Boolean = false,
    val isLoaded: Boolean = false,
    val isSavingName: Boolean = false,
    val nameSavedMessage: String? = null,
    val errorMessage: String? = null,
)

class ProfileViewModel : ViewModel() {
    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.w("ProfileViewModel", "No user logged in")
            return
        }
        
        Log.d("ProfileViewModel", "Refreshing profile for UID: ${user.uid} using ApiClient")
        _uiState.update { it.copy(isLoaded = false, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = api.getProfile()
                if (response.isSuccessful) {
                    val p = response.body()!!
                    Log.d("ProfileViewModel", "Profile loaded: XP=${p.xp}")
                    val today = LocalDate.now().toString()
                    UserDisplayNameStore.update(p.displayName)
                    _uiState.update {
                        it.copy(
                            xp = p.xp,
                            level = p.level,
                            displayName = p.displayName,
                            wins = p.wins ?: 0,
                            elo = p.elo,
                            streak = p.streak ?: 0,
                            rewardClaimedToday = p.lastRewardClaimDate == today,
                            isLoaded = true,
                        )
                    }
                } else {
                    Log.e("ProfileViewModel", "Error fetching profile: ${response.code()} ${response.message()}")
                    _uiState.update { it.copy(isLoaded = true, errorMessage = "Server error: ${response.code()}") }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Profile exception: ${e.message}", e)
                _uiState.update { it.copy(isLoaded = true, errorMessage = e.message) }
            }
        }
    }

    fun updateDisplayName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.length < 2) {
            _uiState.update { it.copy(errorMessage = "Имя должно быть не короче 2 символов") }
            return
        }
        _uiState.update { it.copy(isSavingName = true, errorMessage = null, nameSavedMessage = null) }
        viewModelScope.launch {
            try {
                val response = api.updateProfile(UpdateProfileRequest(trimmed, null))
                if (response.isSuccessful) {
                    val name = response.body()?.displayName ?: trimmed
                    UserDisplayNameStore.update(name)
                    FirebaseAuth.getInstance().currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(name).build(),
                    )
                    _uiState.update {
                        it.copy(
                            displayName = name,
                            isSavingName = false,
                            nameSavedMessage = "Имя обновлено",
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isSavingName = false, errorMessage = "Не удалось сохранить имя")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSavingName = false, errorMessage = e.message ?: "Ошибка сети")
                }
            }
        }
    }
}
