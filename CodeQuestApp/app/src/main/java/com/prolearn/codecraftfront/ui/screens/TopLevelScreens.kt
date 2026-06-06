package com.prolearn.codecraftfront.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.prolearn.codecraftfront.ui.theme.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.prolearn.codecraftfront.R
import com.prolearn.codecraftfront.data.ApiLeaderboardRepository
import com.prolearn.codecraftfront.data.ApiLevelRepository
import com.prolearn.codecraftfront.game.resolveCodeMission
import com.prolearn.codecraftfront.ui.components.CodeSyntaxProfile
import com.prolearn.codecraftfront.ui.components.MissionArenaBoard
import com.prolearn.codecraftfront.ui.home.HomeViewModel
import com.prolearn.codecraftfront.ui.levels.LevelsViewModel
import com.prolearn.codecraftfront.ui.levels.LevelUiModel
import com.prolearn.codecraftfront.ui.stats.StatsViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prolearn.codecraftfront.ui.ai.AiAssistantBottomSheet
import com.prolearn.codecraftfront.ui.ai.AiAssistantViewModel
import com.prolearn.codecraftfront.ui.ai.HintRequestContext
import com.prolearn.codecraftfront.ui.cody.CodyFloatingBubble
import com.prolearn.codecraftfront.ui.cody.CodyChatDialog
import com.prolearn.codecraftfront.ui.cody.CodyPlayEntryCard
import com.prolearn.codecraftfront.ui.home.CoderPersonaProgressCard
import com.prolearn.codecraftfront.ui.war.CodingWarEntryCard
import com.prolearn.codecraftfront.ui.effects.CelebrationOverlay
import com.prolearn.codecraftfront.data.api.ApiClient
import com.prolearn.codecraftfront.ui.navigation.CodeQuestDestination
import java.time.LocalDate

@Composable
fun HomeScreen(
    onStartDailyChallenge: () -> Unit = {},
    onQuickStart: (language: String, track: String, levelId: Int) -> Unit = { _, _, _ -> },
) {
    val homeViewModel: HomeViewModel = viewModel()
    val state by homeViewModel.uiState.collectAsStateWithLifecycle()
    
    // Refresh data when entering the screen to ensure real-time XP
    LaunchedEffect(Unit) {
        homeViewModel.refresh()
    }
    
    var showCodyChat by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val pulse = rememberInfiniteTransition(label = "pulse")
    val fireScale by pulse.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fireScale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
        Text(
            text = stringResource(R.string.home_greeting),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        com.prolearn.codecraftfront.ui.components.GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            accent = NeonOrange,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.home_streak_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${state.streak} days in a row • keep the momentum!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = NeonOrange,
                    modifier = Modifier.scale(fireScale),
                )
            }
        }

        DailyChallengeCard(
            completedToday = state.dailyChallengeCompletedToday,
            onStart = onStartDailyChallenge,
        )

        CoderPersonaProgressCard(
            totalXp = state.xp,
            userLevel = state.level,
        )

        com.prolearn.codecraftfront.ui.components.GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            accent = NeonCyan,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_reward_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = "Return daily to get +120 XP.")
                
                AnimatedVisibility(visible = !state.rewardClaimedToday) {
                    Button(
                        onClick = { homeViewModel.claimDailyReward() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Text(stringResource(R.string.home_reward_claim))
                    }
                }
                AnimatedVisibility(visible = state.rewardClaimedToday) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reward claimed! Come back tomorrow.",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        JuicyQuickStartButton(onQuickStart = { onQuickStart("Python", "beginner", 1) })
        Spacer(modifier = Modifier.height(12.dp))
        }

        if (showCodyChat) {
            CodyChatDialog(screenContext = "home", onDismiss = { showCodyChat = false })
        }
        CodyFloatingBubble(
            route = CodeQuestDestination.Home.route,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onOpenChat = { showCodyChat = true },
        )
    }
}

@Composable
private fun JuicyQuickStartButton(onQuickStart: () -> Unit = {}) {
    var tapped by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (tapped) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 420f),
        label = "ctaScale",
    )

    OutlinedButton(
        onClick = { 
            tapped = !tapped
            onQuickStart()
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
    ) {
        Text(
            text = stringResource(R.string.home_quick_start),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun PlayHubScreen(
    onOpenLanguages: () -> Unit,
    onQuickPlay: () -> Unit,
    onDailyChallenge: () -> Unit,
    onContinueLast: () -> Unit,
    onOpenCodingWar: () -> Unit = {},
) {
    val statsViewModel: StatsViewModel = viewModel()
    val state by statsViewModel.uiState.collectAsStateWithLifecycle()
    
    // Refresh stats when entering screen
    LaunchedEffect(Unit) {
        statsViewModel.refresh()
    }

    var showCodyDialog by remember { mutableStateOf(false) }

    val pulse = rememberInfiniteTransition(label = "playHubPulse")
    val haloScale by pulse.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo",
    )

    if (showCodyDialog) {
        CodyChatDialog(
            screenContext = "play",
            onDismiss = { showCodyDialog = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Practice Arena",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "Pick your battlefield. Every win earns XP and climbs the leaderboard.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )

        CodyPlayEntryCard(onOpenCody = { showCodyDialog = true })

        com.prolearn.codecraftfront.ui.components.GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 26.dp,
            accent = NeonPurple,
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(94.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .scale(haloScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        NeonPurple.copy(alpha = 0.45f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonPurple, NeonCyan)),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = Color(0xFF120428),
                            modifier = Modifier.size(38.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Ready, coder?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Current Level: ${state.level}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        CodingWarEntryCard(onClick = onOpenCodingWar)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PlayActionTile(
                modifier = Modifier.weight(1f),
                accent = NeonCyan,
                icon = Icons.Rounded.AutoAwesome,
                title = "Quick Play",
                subtitle = "Random mission",
                onClick = onQuickPlay,
            )
            PlayActionTile(
                modifier = Modifier.weight(1f),
                accent = NeonOrange,
                icon = Icons.Rounded.LocalFireDepartment,
                title = "Daily",
                subtitle = if (state.dailyChallengeCompletedToday) "Done today" else "+200 XP today",
                onClick = {
                    if (!state.dailyChallengeCompletedToday) onDailyChallenge()
                },
                enabled = !state.dailyChallengeCompletedToday,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PlayActionTile(
                modifier = Modifier.weight(1f),
                accent = NeonGreen,
                icon = Icons.Rounded.PlayCircle,
                title = "Continue",
                subtitle = "Resume training",
                onClick = onContinueLast,
            )
            PlayActionTile(
                modifier = Modifier.weight(1f),
                accent = NeonPurple,
                icon = Icons.Rounded.Code,
                title = "Languages",
                subtitle = "Browse all",
                onClick = onOpenLanguages,
            )
        }

        Text(
            text = "Recent attempts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 6.dp),
        )
        
        if (state.xpHistory.isEmpty()) {
            Text("No recent attempts yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            state.xpHistory.take(5).forEach { history ->
                RecentAttempt(
                    title = history.reason ?: "Unknown Mission",
                    status = "Won · +${history.amount} XP",
                    color = NeonGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DailyChallengeCard(
    completedToday: Boolean,
    onStart: () -> Unit,
) {
    var remainingMs by remember { mutableStateOf(timeUntilMidnight()) }
    LaunchedEffect(Unit) {
        while (true) {
            remainingMs = timeUntilMidnight()
            delay(1000)
        }
    }
    val pulse = rememberInfiniteTransition(label = "challengePulse")
    val glow by pulse.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "challengeGlow",
    )

    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        accent = NeonOrange,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            NeonOrange.copy(alpha = 0.18f * glow),
                            NeonPurple.copy(alpha = 0.16f * glow),
                        ),
                    ),
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = NeonOrange,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daily Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Text(
                text = "Solve today's special mission for +200 XP and a rare badge.",
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Resets in",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = formatCountdown(remainingMs),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonOrange,
                    )
                }
                if (completedToday) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NeonGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Completed today — come back tomorrow!",
                            fontWeight = FontWeight.SemiBold,
                            color = NeonGreen,
                        )
                    }
                } else {
                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonOrange,
                            contentColor = Color(0xFF1A1300),
                        ),
                    ) {
                        Text(
                            text = "Take challenge",
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

private fun timeUntilMidnight(): Long {
    val now = java.util.Calendar.getInstance()
    val midnight = (now.clone() as java.util.Calendar).apply {
        add(java.util.Calendar.DAY_OF_YEAR, 1)
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
}

private fun formatCountdown(ms: Long): String {
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

@Composable
fun LanguagesScreen(
    onOpenLevels: (String) -> Unit,
) {
    var showCodyChat by remember { mutableStateOf(false) }
    val cards = remember {
        listOf(
            LanguageUiModel("Python", "Easy entry and many quests", false, Color(0xFF3478F6), Color(0xFF6AE3FF)),
            LanguageUiModel("JavaScript", "Web logic and interactivity", false, Color(0xFFF7C948), Color(0xFFFF8A3D)),
            LanguageUiModel("Kotlin", "Native Android and backend", false, Color(0xFF9B5CFF), Color(0xFF42E8FF)),
            LanguageUiModel("Java", "Unlocks at level 5", true, Color(0xFF4ED39A), Color(0xFF7A8CFF)),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.languages_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = stringResource(R.string.languages_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(cards) { card ->
                    LanguageCardVertical(
                        model = card,
                        onClick = { onOpenLevels(card.title) },
                    )
                }
            }
        }

        if (showCodyChat) {
            CodyChatDialog(
                screenContext = "languages",
                onDismiss = { showCodyChat = false },
            )
        }
        CodyFloatingBubble(
            route = CodeQuestDestination.Languages.route,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 8.dp),
            onOpenChat = { showCodyChat = true },
        )
    }
}

@Composable
private fun LanguageCardVertical(
    model: LanguageUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = !model.locked) { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(model.startColor.copy(alpha = 0.2f), model.endColor.copy(alpha = 0.2f))))
                .padding(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Language Logo/Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(model.startColor, model.endColor))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
                
                // Language Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = model.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = model.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                
                // Lock/Available Icon
                Icon(
                    imageVector = if (model.locked) Icons.Rounded.Lock else Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = if (model.locked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun LevelsScreen(
    language: String,
    onOpenPlay: (language: String, track: String, levelId: Int) -> Unit,
) {
    val levelsViewModel: LevelsViewModel = viewModel()
    val state by levelsViewModel.uiState.collectAsStateWithLifecycle()

    val tracks = listOf(
        stringResource(R.string.levels_beginner),
        stringResource(R.string.levels_advanced),
    )
    var selectedTrack by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(language, selectedTrack) {
        val track = if (selectedTrack == 0) "beginner" else "advanced"
        levelsViewModel.loadLevels(language, track)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.levels_title, language),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = stringResource(R.string.levels_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        TabRow(selectedTabIndex = selectedTrack) {
            tracks.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTrack == idx,
                    onClick = { selectedTrack = idx },
                    text = { Text(title) },
                )
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(state.levels) { level ->
                    LevelCard(
                        level = level,
                        onClick = {
                            if (level.unlocked) {
                                val track = if (selectedTrack == 0) "beginner" else "advanced"
                                onOpenPlay(language, track, level.id)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: LevelUiModel,
    onClick: () -> Unit,
) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth()
            .clickable(enabled = level.unlocked, onClick = onClick),
        cornerRadius = 16.dp,
        accent = if (level.unlocked) NeonPurple else null,
        surfaceAlpha = if (level.unlocked) 0.55f else 0.30f,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = level.title,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = if (level.progress >= 1.0f) Icons.Rounded.CheckCircle 
                                  else if (level.unlocked) Icons.Rounded.Code 
                                  else Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = if (level.progress >= 1.0f) NeonGreen
                           else if (level.unlocked) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            CircularProgressIndicator(
                progress = { level.progress },
                modifier = Modifier.fillMaxWidth(),
                strokeWidth = 5.dp,
                color = if (level.unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                trackColor = MaterialTheme.colorScheme.surface,
            )

            Text(
                text = stringResource(R.string.levels_stars_format, level.stars),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private data class LanguageUiModel(
    val title: String,
    val description: String,
    val locked: Boolean,
    val startColor: Color,
    val endColor: Color,
)

@Composable
private fun PlayActionTile(
    modifier: Modifier = Modifier,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = modifier
            .height(120.dp)
            .clickable(enabled = enabled, onClick = onClick),
        cornerRadius = 20.dp,
        accent = accent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun RecentAttempt(title: String, status: String, color: Color) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = status,
                    color = color,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun PlayScreen(
    language: String,
    track: String,
    levelId: Int,
) {
    val isDaily = track.equals("daily", ignoreCase = true)
    val trackLabel = when {
        isDaily -> stringResource(R.string.levels_daily)
        track.equals("advanced", ignoreCase = true) -> stringResource(R.string.levels_advanced)
        else -> stringResource(R.string.levels_beginner)
    }
    val xpReward = if (isDaily) 200 else 50
    val missionHeader = stringResource(R.string.play_mission_header, language, trackLabel, levelId)

    val wrongSolutionText = stringResource(R.string.play_wrong_solution)
    val successText = stringResource(R.string.play_success)
    val runningText = stringResource(R.string.play_running)
    val runText = stringResource(R.string.play_run)
    val resetDoneText = stringResource(R.string.play_reset_done)
    val resetText = stringResource(R.string.play_reset)

    val resolvedMission = remember(language, track, levelId) {
        resolveCodeMission(language, track, levelId)
    }

    var fusionProgress by remember(language, track, levelId) { mutableFloatStateOf(0f) }

    val editorLanguageLabel = when (resolvedMission.syntaxProfile) {
        CodeSyntaxProfile.Python -> "Python"
        CodeSyntaxProfile.JavaScript -> "JavaScript"
        CodeSyntaxProfile.Kotlin -> "Kotlin"
        CodeSyntaxProfile.Java -> "Java"
        CodeSyntaxProfile.Dsl -> "DSL"
    }
    val editorLabel = stringResource(R.string.play_editor_label_lang, editorLanguageLabel)

    var editorText by remember(language, track, levelId) {
        mutableStateOf(TextFieldValue(resolvedMission.starterCode))
    }
    var lives by remember(language, track, levelId) { mutableIntStateOf(3) }
    var isRunning by remember(language, track, levelId) { mutableStateOf(false) }
    var statusText by remember(language, track, levelId) { mutableStateOf<String?>(null) }
    var lastError by remember(language, track, levelId) { mutableStateOf<String?>(null) }
    var failedAttempts by remember(language, track, levelId) { mutableIntStateOf(0) }
    var showCelebration by remember(language, track, levelId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val aiViewModel: AiAssistantViewModel = viewModel()
    val aiState by aiViewModel.uiState.collectAsStateWithLifecycle()
    
    // Используем репозитории без передачи URL (они сами возьмут ApiClient.api)
    val profileRepo = remember { ApiLeaderboardRepository() }
    val levelRepo = remember { ApiLevelRepository() }
    val api = remember { ApiClient.api }

    var dailyAlreadyCompleted by remember(language, track, levelId) { mutableStateOf(false) }
    var dailyStatusLoaded by remember(language, track, levelId) { mutableStateOf(!isDaily) }

    LaunchedEffect(isDaily) {
        if (!isDaily) return@LaunchedEffect
        dailyStatusLoaded = false
        try {
            val res = api.getProfile()
            if (res.isSuccessful) {
                val today = LocalDate.now().toString()
                dailyAlreadyCompleted = res.body()?.lastDailyChallengeDate == today
            }
        } finally {
            dailyStatusLoaded = true
        }
    }

    val canPlayDaily = !isDaily || (!dailyAlreadyCompleted && dailyStatusLoaded)

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = missionHeader,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = resolvedMission.missionTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = resolvedMission.missionStory,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (isDaily && dailyStatusLoaded && dailyAlreadyCompleted) {
            com.prolearn.codecraftfront.ui.components.GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                accent = NeonGreen,
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "You already completed today's daily challenge. Come back tomorrow!",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { idx ->
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = if (idx < lives) Color(0xFFFF5D7D) else MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }

        MissionArenaBoard(
            spec = resolvedMission.arenaVisual,
            fusionProgress = fusionProgress,
            modifier = Modifier.fillMaxWidth(),
        )

        com.prolearn.codecraftfront.ui.components.CodeEditorField(
            value = editorText,
            onValueChange = { editorText = it },
            modifier = Modifier.fillMaxWidth(),
            label = editorLabel,
            minLines = 8,
            syntaxProfile = resolvedMission.syntaxProfile,
        )

        Button(
            onClick = {
                if (isRunning || lives <= 0 || !canPlayDaily) return@Button
                isRunning = true
                statusText = null
                fusionProgress = 0f

                scope.launch {
                    if (!resolvedMission.validateSolution(editorText.text)) {
                        statusText = wrongSolutionText
                        lastError = wrongSolutionText
                        failedAttempts += 1
                        lives = (lives - 1).coerceAtLeast(0)
                        isRunning = false
                        fusionProgress = 0f
                        com.prolearn.codecraftfront.ui.sound.SoundFx
                            .play(com.prolearn.codecraftfront.ui.sound.SoundFx.Effect.Failure)
                        return@launch
                    }
                    repeat(24) { step ->
                        fusionProgress = (step + 1) / 24f
                        delay(52)
                    }
                    fusionProgress = 1f
                    isRunning = false
                    statusText = successText
                    lastError = null
                    showCelebration = true
                    com.prolearn.codecraftfront.ui.sound.SoundFx
                        .play(com.prolearn.codecraftfront.ui.sound.SoundFx.Effect.Success)
                    
                    val player = FirebaseAuth.getInstance().currentUser
                    val displayName = player?.email?.substringBefore("@")
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "Cyber Cadet"
                    
                    if (isDaily) {
                        val dailyRes = api.completeDailyChallenge()
                        if (dailyRes.isSuccessful) {
                            dailyAlreadyCompleted = true
                        } else if (dailyRes.code() == 409) {
                            dailyAlreadyCompleted = true
                            statusText = "Daily challenge already completed today."
                        }
                    } else {
                        profileRepo.incrementCurrentUserXp(
                            player?.uid ?: "",
                            displayName,
                            xpReward,
                            reason = missionHeader,
                        )
                        levelRepo.updateLevelProgress(language, track, levelId, stars = lives, completed = true)
                    }
                }
            },
            enabled = !isRunning && lives > 0 && canPlayDaily,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isRunning) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(if (isRunning) runningText else runText)
        }

        OutlinedButton(
            onClick = {
                aiViewModel.requestHint(
                    HintRequestContext(
                        language = language,
                        track = trackLabel,
                        levelId = levelId,
                        storyPrompt = resolvedMission.missionStory,
                        playerCode = editorText.text,
                        lastError = lastError,
                        failedAttempts = failedAttempts,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.HelpOutline,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.play_help_button))
        }

        if (lives <= 0) {
            OutlinedButton(
                onClick = {
                    lives = 3
                    fusionProgress = 0f
                    statusText = resetDoneText
                    lastError = null
                    failedAttempts = 0
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(resetText)
            }
        }

        AnimatedVisibility(visible = statusText != null) {
            Text(
                text = statusText.orEmpty(),
                color = if (statusText == successText) Color(0xFF39FF88) else MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

        CelebrationOverlay(
            visible = showCelebration,
            xpReward = xpReward,
            onContinue = {
                showCelebration = false
                fusionProgress = 0f
            },
        )
    }

    AiAssistantBottomSheet(
        state = aiState,
        onDismiss = aiViewModel::close,
        onClearHistory = aiViewModel::resetHistory,
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(0.95f).clickable(enabled = false) {},
            )
        }
    }
}

@Composable
fun WarSelectionScreen(
    onSelectLanguage: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val languages = listOf("Python", "JavaScript", "Kotlin", "Java")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Coding War",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            text = "Select a programming language to battle",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            languages.forEach { language ->
                LanguageWarCard(
                    language = language,
                    onClick = { onSelectLanguage(language) }
                )
            }
        }
    }
}

@Composable
private fun LanguageWarCard(
    language: String,
    onClick: () -> Unit,
) {
    val colorMap = mapOf(
        "Python" to NeonGreen,
        "JavaScript" to NeonOrange,
        "Kotlin" to NeonPurple,
        "Java" to NeonCyan,
    )
    val color = colorMap[language] ?: NeonPurple
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = language.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Battle other coders",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun WarModeSelectionScreen(
    language: String,
    onStartWar: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Battle Mode",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            text = "Choose how you want to battle in $language",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WarModeCard(
                icon = Icons.Rounded.Public,
                title = "Online Battle",
                description = "Face a random opponent",
                accent = NeonCyan,
                onClick = { onStartWar("online") }
            )
            WarModeCard(
                icon = Icons.Rounded.Group,
                title = "Battle a Friend",
                description = "Challenge your friend",
                accent = NeonGreen,
                onClick = { onStartWar("friend") }
            )
        }
    }
}

@Composable
private fun WarModeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

