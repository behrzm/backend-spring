package com.prolearn.codecraftfront.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prolearn.codecraftfront.R
import com.prolearn.codecraftfront.data.api.FriendChallengeResponse
import com.prolearn.codecraftfront.data.api.FriendResponse
import com.prolearn.codecraftfront.ui.preferences.UserPreferencesViewModel
import com.prolearn.codecraftfront.ui.leaderboard.LeaderboardViewModel
import com.prolearn.codecraftfront.ui.profile.FriendsViewModel
import com.prolearn.codecraftfront.ui.profile.ProfileViewModel
import com.prolearn.codecraftfront.ui.theme.*
import com.prolearn.codecraftfront.game.LevelProgression
import com.prolearn.codecraftfront.ui.war.WarSessionCache

@Composable
fun ProfileScreen(
    email: String?,
    onStartFriendDuel: (language: String, challengeId: String) -> Unit = { _, _ -> },
    onSignOut: () -> Unit,
) {
    val viewModel: ProfileViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    val friendsViewModel: FriendsViewModel = viewModel()
    val friendsState by friendsViewModel.uiState.collectAsStateWithLifecycle()

    var showInviteDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var duelLanguagePickerFor by remember { mutableStateOf<FriendResponse?>(null) }

    val leaderboardViewModel: LeaderboardViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.refresh()
        friendsViewModel.loadFriends()
        friendsViewModel.loadPendingRequests()
        friendsViewModel.refreshNotifications()
    }

    LaunchedEffect(state.nameSavedMessage) {
        if (state.nameSavedMessage != null) {
            friendsViewModel.loadFriends()
            leaderboardViewModel.refresh()
        }
    }

    val displayName = state.displayName ?: email?.substringBefore("@") ?: "Cyber Cadet"
    val initial = displayName.firstOrNull()?.uppercase() ?: "?"
    
    val xpForNext = LevelProgression.xpRequiredForNextLevel(state.level)
    val xpInLevel = LevelProgression.xpInCurrentLevel(state.xp, state.level)

    val prefsViewModel: UserPreferencesViewModel = viewModel()
    val prefs by prefsViewModel.preferences.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.tab_profile),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        AvatarHeader(
            initial = initial,
            displayName = displayName,
            email = email,
            level = state.level,
            onEditAvatar = { showEditNameDialog = true },
        )

        LevelXpCard(
            level = state.level,
            totalXp = state.xp,
            xpInLevel = xpInLevel,
            xpForNext = xpForNext,
        )

        MiniStatsRow(streak = state.streak, wins = state.wins, elo = state.elo, totalXp = state.xp)

        FriendsSection(
            friends = friendsState.friends,
            isLoading = friendsState.isLoading,
            pendingRequestCount = friendsState.pendingRequestCount,
            pendingRequests = friendsState.pendingRequests,
            incomingDuels = friendsState.incomingDuels,
            onInviteClick = { showInviteDialog = true },
            onAcceptRequest = { friendsViewModel.handleFriendRequest(it.id, "ACCEPT") },
            onDeclineRequest = { friendsViewModel.handleFriendRequest(it.id, "DECLINE") },
            onAcceptDuel = { duel ->
                friendsViewModel.acceptFriendChallenge(duel.challengeId) { match ->
                    if (match != null) {
                        WarSessionCache.pendingMatch = match
                        onStartFriendDuel(duel.language, duel.challengeId)
                    }
                }
            },
            onDeclineDuel = { friendsViewModel.declineFriendChallenge(it.challengeId) },
            onChallengeFriend = { duelLanguagePickerFor = it },
        )

        SettingsSection(
            notificationsEnabled = prefs.notificationsEnabled,
            onToggleNotifications = prefsViewModel::setNotificationsEnabled,
            darkThemeEnabled = prefs.darkTheme,
            onToggleDarkTheme = prefsViewModel::setDarkTheme,
            soundEnabled = prefs.soundEnabled,
            onToggleSound = prefsViewModel::setSoundEnabled,
        )

        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.profile_sign_out), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }

    if (showInviteDialog) {
        InviteFriendDialog(
            onDismiss = { showInviteDialog = false },
            viewModel = friendsViewModel,
        )
    }

    if (showEditNameDialog) {
        EditDisplayNameDialog(
            currentName = displayName,
            isSaving = state.isSavingName,
            onDismiss = { showEditNameDialog = false },
            onSave = { newName ->
                viewModel.updateDisplayName(newName)
                showEditNameDialog = false
            },
        )
    }

    duelLanguagePickerFor?.let { friend ->
        DuelLanguageDialog(
            friendName = friend.displayName ?: "Friend",
            onDismiss = { duelLanguagePickerFor = null },
            onPick = { language ->
                friendsViewModel.challengeFriend(friend.id, language) { challengeId ->
                    duelLanguagePickerFor = null
                    if (challengeId != null) {
                        onStartFriendDuel(language, challengeId)
                    }
                }
            },
        )
    }
}

@Composable
private fun FriendsSection(
    friends: List<FriendResponse>,
    isLoading: Boolean,
    pendingRequestCount: Int,
    pendingRequests: List<FriendResponse>,
    incomingDuels: List<FriendChallengeResponse>,
    onInviteClick: () -> Unit,
    onAcceptRequest: (FriendResponse) -> Unit,
    onDeclineRequest: (FriendResponse) -> Unit,
    onAcceptDuel: (FriendChallengeResponse) -> Unit,
    onDeclineDuel: (FriendChallengeResponse) -> Unit,
    onChallengeFriend: (FriendResponse) -> Unit,
) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        accent = NeonCyan,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Friends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    if (pendingRequestCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = Color.Red) {
                            Text(
                                pendingRequestCount.toString(),
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.clickable { onInviteClick() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.PersonAdd, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add", color = NeonCyan, fontWeight = FontWeight.SemiBold)
                }
            }

            if (pendingRequests.isNotEmpty()) {
                Text("Friend requests", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                pendingRequests.forEach { request ->
                    FriendRequestRow(request, onAcceptRequest, onDeclineRequest)
                }
            }

            if (incomingDuels.isNotEmpty()) {
                Text("Duel invitations", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = NeonOrange)
                incomingDuels.forEach { duel ->
                    DuelRequestRow(duel, onAcceptDuel, onDeclineDuel)
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            } else if (friends.isEmpty() && pendingRequests.isEmpty()) {
                Text(
                    "No friends yet. Search by exact nickname!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            } else if (friends.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(friends) { friend ->
                        FriendChip(friend, onChallenge = { onChallengeFriend(friend) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendRequestRow(
    request: FriendResponse,
    onAccept: (FriendResponse) -> Unit,
    onDecline: (FriendResponse) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(request.displayName ?: "Unknown", fontWeight = FontWeight.SemiBold)
            Text(
                "${request.xp} XP · ${request.elo} ELO · Lv ${request.level}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = { onAccept(request) }) {
            Icon(Icons.Rounded.Check, contentDescription = "Accept", tint = NeonGreen)
        }
        IconButton(onClick = { onDecline(request) }) {
            Icon(Icons.Rounded.Close, contentDescription = "Decline", tint = Color.Red)
        }
    }
}

@Composable
private fun DuelRequestRow(
    duel: FriendChallengeResponse,
    onAccept: (FriendChallengeResponse) -> Unit,
    onDecline: (FriendChallengeResponse) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeonOrange.copy(alpha = 0.12f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(duel.challengerName ?: "Coder", fontWeight = FontWeight.Bold)
            Text(
                "Coding War · ${duel.language} · ${duel.challengerXp} XP · ${duel.challengerElo} ELO",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = { onAccept(duel) }) {
            Icon(Icons.Rounded.SportsEsports, contentDescription = "Accept duel", tint = NeonGreen)
        }
        IconButton(onClick = { onDecline(duel) }) {
            Icon(Icons.Rounded.Close, contentDescription = "Decline", tint = Color.Red)
        }
    }
}

@Composable
private fun FriendChip(friend: FriendResponse, onChallenge: () -> Unit) {
    val accent = listOf(NeonGreen, NeonOrange, NeonPurple, NeonCyan)[(friend.xp % 4).toInt().coerceAtLeast(0)]
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier.width(150.dp),
        cornerRadius = 18.dp,
        accent = accent,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.5f)))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    friend.displayName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color(0xFF062018),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                )
            }
            Text(friend.displayName ?: "User", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            Text("${friend.xp} XP · ${friend.elo} ELO", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            TextButton(onClick = onChallenge, contentPadding = PaddingValues(0.dp)) {
                Text("Duel", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun EditDisplayNameDialog(
    currentName: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Edit nickname", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Имя видно в лидерборде и при поиске друзей",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = { onSave(name.trim()) },
                        enabled = !isSaving && name.trim().length >= 2,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DuelLanguageDialog(
    friendName: String,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    val languages = listOf("Python", "JavaScript", "Kotlin", "Java")
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Duel vs $friendName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                languages.forEach { lang ->
                    Button(
                        onClick = { onPick(lang) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(lang)
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InviteFriendDialog(
    onDismiss: () -> Unit,
    viewModel: FriendsViewModel,
) {
    var searchQuery by remember { mutableStateOf("") }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Add friend", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Введите точный никнейм",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                state.errorMessage?.let { err ->
                    Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                state.inviteMessage?.let { ok ->
                    Text(ok, color = NeonGreen, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.searchPeople(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search by nickname") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp)
                )

                if (state.isSearching) {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.searchResults.forEach { person ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(person.displayName ?: "Unknown", fontWeight = FontWeight.Bold)
                                    Text("${person.xp} XP · Lv ${person.level}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = {
                                        viewModel.inviteFriend(person.displayName ?: "") { ok, msg ->
                                            if (ok) onDismiss()
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Add", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun AvatarHeader(initial: String, displayName: String, email: String?, level: Int, onEditAvatar: () -> Unit) {
    com.prolearn.codecraftfront.ui.components.GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, accent = NeonPurple) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(86.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeonPurple, NeonCyan))))
                Box(modifier = Modifier.size(78.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                    Text(text = initial, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
                Box(modifier = Modifier.size(28.dp).align(Alignment.BottomEnd).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable(onClick = onEditAvatar), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text(text = email ?: stringResource(R.string.profile_anonymous), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Brush.horizontalGradient(listOf(NeonOrange, NeonPurple))).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(text = "Level $level", color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun LevelXpCard(level: Int, totalXp: Int, xpInLevel: Int, xpForNext: Int) {
    val progress = (xpInLevel.toFloat() / xpForNext).coerceIn(0f, 1f)
    com.prolearn.codecraftfront.ui.components.GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp, accent = NeonGreen) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Total XP", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = totalXp.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                }
                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeonGreen, NeonCyan))), contentAlignment = Alignment.Center) {
                    Text(text = level.toString(), fontWeight = FontWeight.ExtraBold, color = Color(0xFF062018), fontSize = 22.sp)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))) {
                Box(modifier = Modifier.fillMaxWidth(progress).height(10.dp).clip(RoundedCornerShape(8.dp)).background(Brush.horizontalGradient(listOf(NeonGreen, NeonCyan))))
            }
            Text(text = "$xpInLevel / $xpForNext XP to level ${level + 1}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MiniStatsRow(streak: Int, wins: Int, elo: Int, totalXp: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MiniStat(modifier = Modifier.weight(1f), icon = Icons.Rounded.LocalFireDepartment, color = NeonOrange, value = streak.toString(), label = "Streak")
        MiniStat(modifier = Modifier.weight(1f), icon = Icons.Rounded.EmojiEvents, color = NeonPurple, value = wins.toString(), label = "PvP Wins")
        MiniStat(modifier = Modifier.weight(1f), icon = Icons.Rounded.MilitaryTech, color = NeonCyan, value = elo.toString(), label = "ELO")
    }
}

@Composable
private fun MiniStat(modifier: Modifier = Modifier, icon: ImageVector, color: Color, value: String, label: String) {
    com.prolearn.codecraftfront.ui.components.GlassCard(modifier = modifier, cornerRadius = 18.dp, accent = color) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Text(text = value, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsSection(notificationsEnabled: Boolean, onToggleNotifications: (Boolean) -> Unit, darkThemeEnabled: Boolean, onToggleDarkTheme: (Boolean) -> Unit, soundEnabled: Boolean, onToggleSound: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        com.prolearn.codecraftfront.ui.components.GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp, accent = NeonPurple) {
            Column {
                SettingsToggle(icon = Icons.Rounded.Palette, title = "Dark theme", subtitle = "Neon-purple cosmic mode", checked = darkThemeEnabled, onCheckedChange = onToggleDarkTheme)
                SettingsToggle(icon = Icons.AutoMirrored.Rounded.VolumeUp, title = "Sound effects", subtitle = "Play tones on success", checked = soundEnabled, onCheckedChange = onToggleSound)
                SettingsToggle(icon = Icons.Rounded.Notifications, title = "Notifications", subtitle = "Daily reminders", checked = notificationsEnabled, onCheckedChange = onToggleNotifications)
                SettingsRow(icon = Icons.Rounded.Language, title = "App language", subtitle = "English")
                SettingsRow(icon = Icons.Rounded.PrivacyTip, title = "Privacy", subtitle = "Manage data")
            }
        }
    }
}

@Composable
private fun SettingsToggle(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        SettingIcon(icon)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = NeonGreen, checkedTrackColor = NeonGreen.copy(alpha = 0.45f)))
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        SettingIcon(icon)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingIcon(icon: ImageVector) {
    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}
