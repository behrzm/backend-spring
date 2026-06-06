package com.prolearn.codecraftfront.ui.cody

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prolearn.codecraftfront.ui.navigation.CodeQuestDestination
import com.prolearn.codecraftfront.ui.theme.NeonCyan
import com.prolearn.codecraftfront.ui.theme.NeonGreen
import com.prolearn.codecraftfront.ui.theme.NeonPurple
import kotlinx.coroutines.delay

private const val AUTO_SHOW_DELAY_MS = 400L
private const val BUBBLE_VISIBLE_MS = 8_000L
private const val BUBBLE_HIDDEN_MS = 5_000L

/**
 * Compact Cody bottom-right: speech appears and hides automatically; tap opens full chat.
 */
@Composable
fun CodyFloatingBubble(
    route: String,
    modifier: Modifier = Modifier,
    onOpenChat: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var bubbleVisible by remember { mutableStateOf(false) }
    var languagesQuote by remember { mutableStateOf<CodyQuotes.Quote?>(null) }

    LaunchedEffect(route) {
        if (route == CodeQuestDestination.Languages.route) {
            languagesQuote = CodyQuotes.random()
        }
        bubbleVisible = true
        delay(BUBBLE_VISIBLE_MS)
        bubbleVisible = false
        while (true) {
            delay(BUBBLE_HIDDEN_MS)
            bubbleVisible = true
            delay(BUBBLE_VISIBLE_MS)
            bubbleVisible = false
            delay(BUBBLE_HIDDEN_MS)
        }
    }

    val message = remember(route, languagesQuote) {
        CodyMessageResolver.resolve(route, context, languagesQuote)
    }

    val excited = route == CodeQuestDestination.Play.route || route.startsWith("mission/")

    Column(
        modifier = modifier.widthIn(max = 260.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(
            visible = bubbleVisible,
            enter = fadeIn() + scaleIn(initialScale = 0.88f),
            exit = fadeOut() + scaleOut(targetScale = 0.88f),
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    when (message) {
                        is CodyMessage.Plain -> {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        is CodyMessage.Quote -> {
                            Text(
                                text = "«${message.body}»",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${message.author}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonGreen,
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.45f),
                            NeonPurple.copy(alpha = 0.28f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            CodyRobot(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (onOpenChat != null) {
                        onOpenChat()
                    } else {
                        bubbleVisible = !bubbleVisible
                    }
                },
                excited = excited,
            )
        }
    }
}
