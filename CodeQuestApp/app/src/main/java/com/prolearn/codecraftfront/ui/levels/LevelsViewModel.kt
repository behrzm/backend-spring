package com.prolearn.codecraftfront.ui.levels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prolearn.codecraftfront.data.ApiLevelRepository
import com.prolearn.codecraftfront.data.LevelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LevelUiModel(
    val id: Int,
    val title: String,
    val unlocked: Boolean,
    val progress: Float,
    val stars: Int
)

data class LevelsUiState(
    val levels: List<LevelUiModel> = emptyList(),
    val isLoading: Boolean = false
)

class LevelsViewModel : ViewModel() {
    // Используем конструктор без параметров, так как репозиторий теперь берет настройки из ApiClient
    private val repository: LevelRepository = ApiLevelRepository()

    private val _uiState = MutableStateFlow(LevelsUiState())
    val uiState: StateFlow<LevelsUiState> = _uiState.asStateFlow()

    fun loadLevels(language: String, track: String) {
        Log.d("LevelsViewModel", "Loading levels for $language - $track")
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val list = mutableListOf<LevelUiModel>()
                for (id in 1..5) {
                    val progress = repository.getLevelProgress(language, track, id)
                    list.add(
                        LevelUiModel(
                            id = id,
                            title = "Level $id",
                            unlocked = id == 1 || (progress?.completed ?: false),
                            progress = (progress?.stars?.toFloat() ?: 0f) / 3f,
                            stars = progress?.stars ?: 0
                        )
                    )
                }
                _uiState.update { it.copy(levels = list, isLoading = false) }
            } catch (e: Exception) {
                Log.e("LevelsViewModel", "Error loading levels: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
