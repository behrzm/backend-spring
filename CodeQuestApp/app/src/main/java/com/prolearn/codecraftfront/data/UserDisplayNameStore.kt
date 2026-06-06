package com.prolearn.codecraftfront.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared display name so Home, Profile, Leaderboard refresh after edit.
 */
object UserDisplayNameStore {
    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    fun update(name: String?) {
        _displayName.value = name?.trim()?.takeIf { it.isNotBlank() }
    }
}
