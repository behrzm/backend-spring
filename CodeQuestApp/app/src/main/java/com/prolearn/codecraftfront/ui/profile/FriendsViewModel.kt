package com.prolearn.codecraftfront.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.data.api.CreateFriendChallengeRequest
import com.prolearn.codecraftfront.data.api.FriendChallengeResponse
import com.prolearn.codecraftfront.data.api.FriendRequestAction
import com.prolearn.codecraftfront.data.api.FriendResponse
import com.prolearn.codecraftfront.data.api.ProfileResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class FriendsUiState(
    val friends: List<FriendResponse> = emptyList(),
    val pendingRequests: List<FriendResponse> = emptyList(),
    val incomingDuels: List<FriendChallengeResponse> = emptyList(),
    val pendingRequestCount: Int = 0,
    val searchResults: List<ProfileResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val inviteMessage: String? = null,
    val errorMessage: String? = null,
)

class FriendsViewModel : ViewModel() {
    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (isActive) {
                refreshNotifications()
                delay(12_000)
            }
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            try {
                val pendingRes = api.getFriendRequests()
                val countRes = api.getPendingFriendCount()
                val duelsRes = api.getIncomingFriendChallenges()
                _uiState.update { state ->
                    state.copy(
                        pendingRequests = if (pendingRes.isSuccessful) pendingRes.body() ?: emptyList() else state.pendingRequests,
                        pendingRequestCount = when {
                            countRes.isSuccessful -> (countRes.body() ?: 0L).toInt()
                            pendingRes.isSuccessful -> pendingRes.body()?.size ?: 0
                            else -> state.pendingRequestCount
                        },
                        incomingDuels = if (duelsRes.isSuccessful) duelsRes.body() ?: emptyList() else state.incomingDuels,
                    )
                }
            } catch (e: Exception) {
                Log.e("FriendsViewModel", "refreshNotifications: ${e.message}")
            }
        }
    }

    fun loadFriends() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = api.getFriends()
                if (response.isSuccessful) {
                    _uiState.update { it.copy(friends = response.body() ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Could not load friends") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun loadPendingRequests() {
        refreshNotifications()
    }

    fun searchPeople(query: String) {
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        _uiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            try {
                val response = api.searchPeople(query)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(searchResults = response.body() ?: emptyList(), isSearching = false) }
                } else {
                    _uiState.update { it.copy(isSearching = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSearching = false, errorMessage = mapNetworkError(e))
                }
            }
        }
    }

    fun inviteFriend(nickname: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val response = api.inviteFriend(nickname.trim())
                if (response.isSuccessful) {
                    _uiState.update { it.copy(inviteMessage = "Заявка отправлена!") }
                    refreshNotifications()
                    onResult(true, "Заявка отправлена")
                } else {
                    val msg = response.errorBody()?.string()?.let { parseError(it) }
                        ?: "Не удалось отправить заявку"
                    _uiState.update { it.copy(errorMessage = msg) }
                    onResult(false, msg)
                }
            } catch (e: Exception) {
                val msg = mapNetworkError(e)
                _uiState.update { it.copy(errorMessage = msg) }
                onResult(false, msg)
            }
        }
    }

    fun handleFriendRequest(senderId: String, action: String) {
        viewModelScope.launch {
            try {
                val response = api.handleFriendRequest(FriendRequestAction(senderId, action))
                if (response.isSuccessful) {
                    loadFriends()
                    refreshNotifications()
                }
            } catch (e: Exception) {
                Log.e("FriendsViewModel", "handleFriendRequest: ${e.message}")
            }
        }
    }

    fun challengeFriend(targetUserId: String, language: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.createFriendChallenge(
                    CreateFriendChallengeRequest(targetUserId, language),
                )
                if (response.isSuccessful) {
                    onResult(response.body()?.challengeId)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun acceptFriendChallenge(challengeId: String, onMatch: (com.prolearn.codecraftfront.data.api.WarMatchResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.acceptFriendChallenge(challengeId)
                if (response.isSuccessful) {
                    refreshNotifications()
                    onMatch(response.body())
                } else {
                    onMatch(null)
                }
            } catch (e: Exception) {
                onMatch(null)
            }
        }
    }

    fun declineFriendChallenge(challengeId: String) {
        viewModelScope.launch {
            api.declineFriendChallenge(challengeId)
            refreshNotifications()
        }
    }

    private fun mapNetworkError(e: Exception): String {
        val raw = e.message.orEmpty()
        return when {
            raw.contains("failed to connect", ignoreCase = true) ||
                raw.contains("timeout", ignoreCase = true) ||
                raw.contains("ECONNREFUSED", ignoreCase = true) ->
                "Нет связи с сервером. ПК и телефон в одной Wi-Fi? " +
                    "В local.properties укажите API_BASE_URL=http://IP_ПК:8080/api/v1/ (ipconfig)"
            else -> raw.ifBlank { "Ошибка сети" }
        }
    }

    private fun parseError(body: String): String {
        return when {
            body.contains("Already friends", ignoreCase = true) -> "Вы уже друзья"
            body.contains("already sent", ignoreCase = true) -> "Заявка уже отправлена"
            body.contains("Player not found", ignoreCase = true) -> "Игрок не найден"
            body.contains("yourself", ignoreCase = true) -> "Нельзя добавить себя"
            else -> "Ошибка сервера"
        }
    }

    override fun onCleared() {
        pollJob?.cancel()
        super.onCleared()
    }
}
