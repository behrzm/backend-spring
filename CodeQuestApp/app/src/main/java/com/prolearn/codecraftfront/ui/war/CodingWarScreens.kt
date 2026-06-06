package com.prolearn.codecraftfront.ui.war

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prolearn.codecraftfront.game.resolveCodeMission
import com.prolearn.codecraftfront.ui.components.CodeEditorField
import com.prolearn.codecraftfront.ui.components.GlassCard
import com.prolearn.codecraftfront.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CodingWarEntryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 24.dp,
        accent = NeonOrange,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(NeonOrange, NeonPink))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.SportsEsports,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Coding War",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonOrange,
                )
                Text(
                    text = "Faceit-style PvP · starting ELO 1000 · ±25 per match",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = NeonOrange,
            )
        }
    }
}

@Composable
fun WarScreen(
    language: String,
    mode: String,
    challengeId: String? = null,
    onBack: () -> Unit,
) {
    val viewModel: WarViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(language, mode, challengeId) {
        when {
            mode == "friend" && WarSessionCache.pendingMatch != null -> {
                viewModel.startFriendDuelWithCachedMatch(language)
            }
            mode == "friend" && !challengeId.isNullOrBlank() -> {
                viewModel.startFriendDuelAsChallenger(language, challengeId)
            }
            mode != "friend" -> viewModel.startOnlineMatch(language, mode)
        }
    }

    val mission = remember(state.language, state.missionId, state.battleStarted) {
        if (state.battleStarted) {
            resolveCodeMission(state.language, "beginner", state.missionId)
        } else {
            null
        }
    }

    var editorText by remember(mission) {
        mutableStateOf(TextFieldValue(mission?.starterCode ?: ""))
    }
    var playerElapsed by remember { mutableLongStateOf(0L) }
    var opponentElapsed by remember { mutableLongStateOf(0L) }
    var timerRunning by remember(state.battleStarted) { mutableStateOf(state.battleStarted) }

    LaunchedEffect(timerRunning, state.battleStarted) {
        if (!timerRunning || !state.battleStarted) return@LaunchedEffect
        while (timerRunning) {
            delay(10)
            playerElapsed += 10
            if (opponentElapsed < state.opponentSolveTimeMs) {
                opponentElapsed = (opponentElapsed + 10).coerceAtMost(state.opponentSolveTimeMs)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoadingMatch) {
            MatchmakingOverlay(language = language)
        } else if (state.matchError != null) {
            ErrorOverlay(message = state.matchError!!, onBack = onBack)
        } else if (mission != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Coding War · $language",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                DuelTimerRow(
                    opponentName = state.opponentName,
                    playerElo = state.playerElo,
                    opponentElo = state.opponentElo,
                    playerTimeMs = playerElapsed,
                    opponentTimeMs = opponentElapsed,
                )

                Text(
                    text = mission.missionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = mission.missionStory,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                CodeEditorField(
                    value = editorText,
                    onValueChange = { editorText = it },
                    label = language,
                    syntaxProfile = mission.syntaxProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )

                Button(
                    onClick = {
                        timerRunning = false
                        val solved = mission.validateSolution(editorText.text)
                        viewModel.submitResult(playerElapsed, solved)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.resultSubmitted,
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Submit solution", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (state.showResult) {
            WarResultOverlay(
                won = state.won,
                eloDelta = state.eloDelta,
                newElo = state.newElo,
                playerTime = state.playerTimeLabel.ifBlank { formatWarMs(playerElapsed) },
                opponentTime = state.opponentTimeLabel.ifBlank { formatWarMs(opponentElapsed) },
                opponentName = state.opponentName,
                onContinue = onBack,
            )
        }
    }
}

@Composable
private fun MatchmakingOverlay(language: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = NeonCyan)
            Text(
                text = "Finding opponent…",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Language: $language",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorOverlay(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun DuelTimerRow(
    opponentName: String,
    playerElo: Int,
    opponentElo: Int,
    playerTimeMs: Long,
    opponentTimeMs: Long,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TimerCard(
            modifier = Modifier.weight(1f),
            title = "You",
            subtitle = "$playerElo ELO",
            time = formatWarMs(playerTimeMs),
            accent = NeonGreen,
        )
        TimerCard(
            modifier = Modifier.weight(1f),
            title = opponentName,
            subtitle = "$opponentElo ELO",
            time = formatWarMs(opponentTimeMs),
            accent = NeonOrange,
        )
    }
}

@Composable
private fun TimerCard(
    title: String,
    subtitle: String,
    time: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier, cornerRadius = 18.dp, accent = accent) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(time, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall, color = accent)
        }
    }
}

@Composable
private fun FriendWarPlaceholder(language: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Friend battle",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Friend duel for $language is not available here. Use Online Battle or challenge a friend from Profile.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

fun formatWarMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (ms % 1000) / 10
    return "%02d:%02d.%02d".format(minutes, seconds, millis)
}
