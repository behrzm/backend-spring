package com.prolearn.codecraftfront.ui.war

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prolearn.codecraftfront.ui.theme.*
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun WarResultOverlay(
    won: Boolean,
    eloDelta: Int,
    newElo: Int,
    playerTime: String,
    opponentTime: String,
    opponentName: String,
    onContinue: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center,
    ) {
        if (won) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = remember {
                    listOf(
                        Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(
                                0xFF39FF88.toInt(),
                                0xFF42E8FF.toInt(),
                                0xFF9B5CFF.toInt(),
                                0xFFFF8A3D.toInt(),
                            ),
                            position = Position.Relative(0.5, 0.3),
                            emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(40),
                        ),
                    )
                },
            )
        }

        val accent = if (won) NeonGreen else Color(0xFFFF5D7D)
        val title = if (won) "VICTORY!" else "DEFEAT"
        val subtitle = if (won) {
            "You beat $opponentName"
        } else {
            "$opponentName solved it faster"
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(accent, accent.copy(alpha = 0.4f)),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (won) Icons.Rounded.EmojiEvents else Icons.Rounded.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp),
                    )
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = accent,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TimeColumn("You", playerTime, accent)
                    TimeColumn("Opponent", opponentTime, MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = "${if (eloDelta >= 0) "+" else ""}$eloDelta ELO  →  $newElo",
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "CodeQuest Faceit rating",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TimeColumn(label: String, time: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(time, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
    }
}
