package com.prolearn.codecraftfront.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prolearn.codecraftfront.ui.cody.CodyFloatingBubble
import com.prolearn.codecraftfront.ui.navigation.CodeQuestDestination
import com.prolearn.codecraftfront.ui.stats.StatsViewModel
import com.prolearn.codecraftfront.ui.theme.*

private data class XpPoint(val day: Int, val xp: Float)

private data class Badge(
    val title: String,
    val unlocked: Boolean,
    val color: Color,
    val icon: ImageVector,
    val description: String,
)

@Composable
fun StatsScreen(
    onOpenLeaderboard: () -> Unit = {},
) {
    val statsViewModel: StatsViewModel = viewModel()
    val state by statsViewModel.uiState.collectAsStateWithLifecycle()

    // Refresh stats when entering screen to ensure up-to-date XP
    LaunchedEffect(Unit) {
        statsViewModel.refresh()
    }

    // Map real history for the chart
    val xpPoints = remember(state.xpHistory) {
        state.xpHistory.reversed().takeLast(10).mapIndexed { index, history ->
            XpPoint(index, history.amount.toFloat())
        }
    }

    var showCodyChat by remember { mutableStateOf(false) }
    val badges = remember(state.level, state.wins, state.streak) {
        listOf(
            Badge("First Steps", true, NeonGreen, Icons.Rounded.Star, "Complete your first mission"),
            Badge("Combo x5", state.wins >= 5, NeonOrange, Icons.Rounded.Bolt, "Win 5 levels"),
            Badge("Streak Master", state.streak >= 7, NeonPurple, Icons.Rounded.LocalFireDepartment, "7-day streak"),
            Badge("Polyglot", state.languageAccuracies.size >= 3, NeonCyan, Icons.Rounded.TrendingUp, "Try 3 languages"),
            Badge("Cyber Champ", state.level >= 10, NeonPurple, Icons.Rounded.Star, "Reach level 10"),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
        Text(
            text = "Your stats",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            MetricsRow(
                totalXp = state.totalXp,
                avgAccuracy = state.accuracy,
                streak = state.streak,
                pvpWins = state.wins,
                elo = state.elo,
            )

            LeaderboardCta(onClick = onOpenLeaderboard)

            if (xpPoints.isNotEmpty()) {
                ChartCard(title = "Experience Activity", subtitle = "Your recent progress") {
                    XpLineChart(
                        points = xpPoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    )
                }
            }

            ChartCard(title = "Accuracy by language", subtitle = "Based on mission performance") {
                AccuracyBars(items = state.languageAccuracies)
            }

            ChartCard(title = "Intensity history", subtitle = "Activity last 7 days") {
                StreakColumns(
                    days = state.streakHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                )
            }

            Text(
                text = "Badges",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            BadgesGrid(badges = badges)
        }

        Spacer(modifier = Modifier.height(8.dp))
        }

        if (showCodyChat) {
            com.prolearn.codecraftfront.ui.cody.CodyChatDialog(
                screenContext = "stats",
                onDismiss = { showCodyChat = false },
            )
        }
        CodyFloatingBubble(
            route = CodeQuestDestination.Stats.route,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onOpenChat = { showCodyChat = true },
        )
    }
}

@Composable
private fun MetricsRow(
    totalXp: Int,
    avgAccuracy: Float,
    streak: Int,
    pvpWins: Int,
    elo: Int,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Bolt,
            color = NeonGreen,
            value = totalXp.toString(),
            label = "Total XP",
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.EmojiEvents,
            color = NeonPurple,
            value = pvpWins.toString(),
            label = "PvP Wins",
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.MilitaryTech,
            color = NeonCyan,
            value = elo.toString(),
            label = "ELO",
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.TrendingUp,
            color = NeonOrange,
            value = "${(avgAccuracy * 100).toInt()}%",
            label = "Accuracy · $streak day streak",
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    color: Color,
    value: String,
    label: String,
) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = modifier,
        cornerRadius = 20.dp,
        accent = color,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
        }
    }
}

@Composable
private fun XpLineChart(
    points: List<XpPoint>,
    modifier: Modifier = Modifier,
) {
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier = modifier) {
        if (points.isEmpty()) return@Canvas
        val maxXp = points.maxOf { it.xp }.coerceAtLeast(10f)
        val minXp = 0f
        val width = size.width
        val height = size.height
        val left = 0f
        val bottom = height - 16f
        val top = 8f
        val usableWidth = width
        val usableHeight = bottom - top

        val gridColor = onSurfaceVariant.copy(alpha = 0.15f)
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = top + usableHeight * (i.toFloat() / gridLines)
            drawLine(
                color = gridColor,
                start = Offset(left, y),
                end = Offset(left + usableWidth, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
            )
        }

        val stepX = if (points.size > 1) usableWidth / (points.size - 1) else usableWidth
        val pathPoints = points.mapIndexed { idx, p ->
            val x = left + idx * stepX
            val ratio = ((p.xp - minXp) / (maxXp - minXp)).coerceIn(0f, 1f)
            val y = bottom - ratio * usableHeight
            Offset(x, y)
        }

        val fillPath = Path().apply {
            moveTo(pathPoints.first().x, bottom)
            pathPoints.forEach { lineTo(it.x, it.y) }
            lineTo(pathPoints.last().x, bottom)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(NeonGreen.copy(alpha = 0.45f), Color.Transparent),
                startY = top,
                endY = bottom,
            ),
        )

        val linePath = Path().apply {
            moveTo(pathPoints.first().x, pathPoints.first().y)
            pathPoints.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            path = linePath,
            brush = Brush.horizontalGradient(listOf(NeonGreen, NeonCyan)),
            style = Stroke(width = 5f),
        )

        pathPoints.forEach { p ->
            drawCircle(color = NeonCyan.copy(alpha = 0.35f), radius = 9f, center = p)
            drawCircle(color = NeonCyan, radius = 4.5f, center = p)
        }
    }
}

@Composable
private fun AccuracyBars(items: List<com.prolearn.codecraftfront.ui.stats.LanguageAccuracyData>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (items.isEmpty()) {
            Text("No language data available yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items.forEach { item ->
            val color = when(item.name) {
                "Python" -> NeonCyan
                "JavaScript" -> NeonOrange
                "Kotlin" -> NeonPurple
                else -> NeonGreen
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${(item.accuracy * 100).toInt()}%",
                        color = color,
                        fontWeight = FontWeight.Bold,
                    )
                }
                LinearProgressIndicator(
                    progress = { item.accuracy },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp)),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun StreakColumns(
    days: List<com.prolearn.codecraftfront.ui.stats.StreakDayData>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEach { day ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((120.dp * day.value).coerceAtLeast(4.dp))
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(NeonOrange, NeonPurple),
                                ),
                            ),
                    )
                }
                Text(
                    text = day.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BadgesGrid(badges: List<Badge>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        userScrollEnabled = false,
    ) {
        items(badges) { badge ->
            BadgeChip(badge)
        }
    }
}

@Composable
private fun LeaderboardCta(onClick: () -> Unit) {
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 20.dp,
        accent = NeonOrange,
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            NeonOrange.copy(alpha = 0.16f),
                            NeonPurple.copy(alpha = 0.10f),
                        ),
                    ),
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(NeonOrange.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = NeonOrange,
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Global leaderboard",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "See how you rank against other coders",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BadgeChip(badge: Badge) {
    val transition = rememberInfiniteTransition(label = "badge")
    val pulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    com.prolearn.codecraftfront.ui.components.GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        cornerRadius = 20.dp,
        accent = if (badge.unlocked) badge.color else null,
        surfaceAlpha = if (badge.unlocked) 0.55f else 0.30f,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .graphicsLayer {
                        if (badge.unlocked) {
                            scaleX = pulse
                            scaleY = pulse
                        }
                    }
                    .clip(CircleShape)
                    .background(
                        if (badge.unlocked) badge.color.copy(alpha = 0.25f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (badge.unlocked) badge.icon else Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = if (badge.unlocked) badge.color else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = badge.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
