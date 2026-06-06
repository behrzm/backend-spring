package com.prolearn.codecraftfront.ui.war

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.data.api.WarMatchResponse
import com.prolearn.codecraftfront.data.api.WarResultRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WarBattleState(
    val isLoadingMatch: Boolean = true,
    val matchError: String? = null,
    val matchId: String = "",
    val opponentName: String = "",
    val opponentElo: Int = 1000,
    val playerElo: Int = 1000,
    val opponentSolveTimeMs: Long = 30_000L,
    val missionId: Int = 1,
    val language: String = "Python",
    val mode: String = "online",
    val battleStarted: Boolean = false,
    val resultSubmitted: Boolean = false,
    val showResult: Boolean = false,
    val won: Boolean = false,
    val eloDelta: Int = 0,
    val newElo: Int = 1000,
    val totalWins: Int = 0,
    val playerTimeLabel: String = "",
    val opponentTimeLabel: String = "",
)

class WarViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _state = MutableStateFlow(WarBattleState())
    val state: StateFlow<WarBattleState> = _state.asStateFlow()

    fun startFriendDuelWithCachedMatch(language: String) {
        val match = WarSessionCache.pendingMatch
        WarSessionCache.pendingMatch = null
        if (match != null) {
            applyMatch(language, "friend", match)
        } else {
            _state.update {
                it.copy(isLoadingMatch = false, matchError = "Match not found")
            }
        }
    }

    fun startFriendDuelAsChallenger(language: String, challengeId: String) {
        _state.value = WarBattleState(language = language, mode = "friend", isLoadingMatch = true)
        viewModelScope.launch {
            while (isActive) {
                try {
                    val response = api.getFriendChallengeMatch(challengeId)
                    if (response.isSuccessful) {
                        applyMatch(language, "friend", response.body()!!)
                        return@launch
                    }
                } catch (_: Exception) {
                }
                delay(2000)
            }
            _state.update {
                it.copy(
                    isLoadingMatch = false,
                    matchError = "Your friend has not accepted the duel yet",
                )
            }
        }
    }

    private fun applyMatch(language: String, mode: String, body: WarMatchResponse) {
        _state.update {
            it.copy(
                isLoadingMatch = false,
                matchId = body.matchId,
                opponentName = body.opponentName,
                opponentElo = body.opponentElo,
                playerElo = body.playerElo,
                opponentSolveTimeMs = body.opponentSolveTimeMs,
                missionId = body.missionId,
                language = language,
                mode = mode,
                battleStarted = true,
            )
        }
    }

    fun startOnlineMatch(language: String, mode: String) {
        _state.value = WarBattleState(language = language, mode = mode, isLoadingMatch = true)
        viewModelScope.launch {
            try {
                val response = api.findOpponent(language)
                if (response.isSuccessful) {
                    applyMatch(language, mode, response.body()!!)
                } else {
                    _state.update {
                        it.copy(
                            isLoadingMatch = false,
                            matchError = "Could not find an opponent (${response.code()})",
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("WarViewModel", "findOpponent failed", e)
                _state.update {
                    it.copy(
                        isLoadingMatch = false,
                        matchError = "Server unavailable. Start backend-spring.",
                    )
                }
            }
        }
    }

    fun submitResult(solveTimeMs: Long, solved: Boolean) {
        val current = _state.value
        if (current.resultSubmitted || current.matchId.isBlank()) return

        val won = solved && solveTimeMs < current.opponentSolveTimeMs
        _state.update { it.copy(resultSubmitted = true) }

        viewModelScope.launch {
            try {
                val response = api.reportWarResult(
                    WarResultRequest(
                        matchId = current.matchId,
                        solveTimeMs = solveTimeMs,
                        won = won,
                        language = current.language,
                    ),
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    _state.update {
                        it.copy(
                            showResult = true,
                            won = body.won,
                            eloDelta = body.eloDelta,
                            newElo = body.newElo,
                            totalWins = body.totalWins,
                            playerTimeLabel = body.playerTime,
                            opponentTimeLabel = body.opponentTime,
                        )
                    }
                } else {
                    applyLocalResult(solveTimeMs, won)
                }
            } catch (e: Exception) {
                Log.e("WarViewModel", "reportResult failed", e)
                applyLocalResult(solveTimeMs, won)
            }
        }
    }

    private fun applyLocalResult(solveTimeMs: Long, won: Boolean) {
        val elo = _state.value.playerElo + if (won) 25 else -25
        _state.update {
            it.copy(
                showResult = true,
                won = won,
                eloDelta = if (won) 25 else -25,
                newElo = elo.coerceAtLeast(0),
                playerTimeLabel = formatMs(solveTimeMs),
                opponentTimeLabel = formatMs(it.opponentSolveTimeMs),
            )
        }
    }

    private fun formatMs(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val millis = (ms % 1000) / 10
        return "%02d:%02d.%02d".format(minutes, seconds, millis)
    }
}
