package com.prolearn.codecraftfront.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.prolearn.codecraftfront.ui.theme.*

object AuthRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
}

@Composable
fun CodeQuestBottomBar(
    destinations: List<CodeQuestDestination>,
    currentDestination: NavDestination?,
    hasNotifications: Boolean,
    onNavigate: (String) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            destinations.forEachIndexed { index, destination ->
                val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                val accent = listOf(NeonGreen, NeonCyan, NeonOrange, NeonPurple, NeonGreen)[index % 5]
                val showDot = destination == CodeQuestDestination.Profile && hasNotifications

                BottomBarPill(
                    destination = destination,
                    selected = selected,
                    accent = accent,
                    showDot = showDot,
                    onClick = { onNavigate(destination.route) },
                )
            }
        }
    }
}

@Composable
private fun BottomBarPill(
    destination: CodeQuestDestination,
    selected: Boolean,
    accent: Color,
    showDot: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(contentAlignment = Alignment.TopEnd) {
        Row(
            modifier = Modifier
                .height(46.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(if (selected) accent.copy(alpha = 0.15f) else Color.Transparent)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = null,
                tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text = " " + stringResource(destination.titleRes),
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = (-4).dp, y = 4.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}
