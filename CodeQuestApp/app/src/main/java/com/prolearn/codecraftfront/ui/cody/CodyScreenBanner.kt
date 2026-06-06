package com.prolearn.codecraftfront.ui.cody

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.prolearn.codecraftfront.ui.components.GlassCard
import com.prolearn.codecraftfront.ui.navigation.CodeQuestDestination
import com.prolearn.codecraftfront.ui.theme.NeonCyan
import com.prolearn.codecraftfront.ui.theme.NeonGreen

/**
 * Visible Cody block embedded at the top of main tabs (always on screen, not only overlay).
 */
@Composable
fun CodyScreenBanner(
    route: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var languagesQuote by remember { mutableStateOf<CodyQuotes.Quote?>(null) }

    LaunchedEffect(route) {
        if (route == CodeQuestDestination.Languages.route) {
            languagesQuote = CodyQuotes.random()
        }
    }

    val message = remember(route, languagesQuote) {
        CodyMessageResolver.resolve(route, context, languagesQuote)
    }

    val excited = route == CodeQuestDestination.Play.route || route.startsWith("mission/")

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f),
        cornerRadius = 22.dp,
        accent = NeonCyan,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CodyRobot(
                modifier = Modifier,
                excited = excited,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Cody · ИИ-напарник",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan,
                )
                when (message) {
                    is CodyMessage.Plain -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    is CodyMessage.Quote -> {
                        Text(
                            text = "«${message.body}»",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "— ${message.author}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonGreen,
                        )
                    }
                }
            }
        }
    }
}
