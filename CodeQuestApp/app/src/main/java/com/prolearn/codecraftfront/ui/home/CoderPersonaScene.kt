package com.prolearn.codecraftfront.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prolearn.codecraftfront.game.CoderPersona
import com.prolearn.codecraftfront.ui.theme.*

/**
 * Built-in loop preview (~2.5s) until GIF assets are added.
 */
@Composable
fun CoderPersonaScene(
    persona: CoderPersona,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "persona")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )
    val bob by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bob",
    )

    Box(
        modifier = modifier
            .size(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        NeonPurple.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (persona.tier) {
            1 -> SceneSchoolPhone(phase, bob)
            2 -> SceneSchoolLaptop(phase, bob)
            3 -> SceneStudent(phase, bob, dualMonitor = false)
            4 -> SceneStudent(phase, bob, dualMonitor = true)
            else -> SceneOfficeTeam(phase, bob, compact = false)
        }
        Text(
            text = persona.icon,
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .padding(6.dp),
        )
    }
}

@Composable
private fun SceneSchoolPhone(phase: Float, bob: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob
        drawRoundRect(
            color = Color(0xFF2A2048),
            topLeft = Offset(cx - 70f, cy + 20f),
            size = Size(140f, 36f),
            cornerRadius = CornerRadius(8f, 8f),
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Color(0xFF5B45A8), Color(0xFF3D2E6B))),
            topLeft = Offset(cx - 28f, cy - 40f),
            size = Size(56f, 56f),
            cornerRadius = CornerRadius(18f, 18f),
        )
        val armAngle = -25f + phase * 50f
        rotate(armAngle, pivot = Offset(cx + 20f, cy - 10f)) {
            drawRoundRect(
                color = NeonOrange.copy(0.85f),
                topLeft = Offset(cx + 18f, cy - 55f),
                size = Size(14f, 32f),
                cornerRadius = CornerRadius(6f, 6f),
            )
        }
        drawRoundRect(
            color = Color(0xFF0D1A2A),
            topLeft = Offset(cx - 50f, cy - 8f),
            size = Size(32f, 48f),
            cornerRadius = CornerRadius(6f, 6f),
        )
        drawRoundRect(
            color = NeonCyan.copy(0.5f + phase * 0.4f),
            topLeft = Offset(cx - 46f, cy - 4f),
            size = Size(24f, 20f),
            cornerRadius = CornerRadius(4f, 4f),
        )
    }
}

@Composable
private fun SceneSchoolLaptop(phase: Float, bob: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob
        drawRoundRect(
            color = Color(0xFF35456A),
            topLeft = Offset(cx - 80f, cy + 30f),
            size = Size(160f, 12f),
            cornerRadius = CornerRadius(4f, 4f),
        )
        drawRoundRect(
            color = Color(0xFF1A1238),
            topLeft = Offset(cx - 55f, cy - 20f),
            size = Size(110f, 52f),
            cornerRadius = CornerRadius(6f, 6f),
        )
        drawRoundRect(
            color = NeonGreen.copy(0.35f + phase * 0.45f),
            topLeft = Offset(cx - 48f, cy - 14f),
            size = Size(96f, 36f),
            cornerRadius = CornerRadius(4f, 4f),
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(NeonPurple, NeonCyan)),
            topLeft = Offset(cx - 24f, cy - 55f),
            size = Size(48f, 48f),
            cornerRadius = CornerRadius(14f, 14f),
        )
        val pointX = cx + 30f + phase * 8f
        drawCircle(NeonOrange, 6f, Offset(pointX, cy - 30f))
    }
}

@Composable
private fun SceneStudent(phase: Float, bob: Float, dualMonitor: Boolean) {
    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob
        drawRoundRect(
            color = Color(0xFF2E3A52).copy(0.6f),
            topLeft = Offset(20f, 30f),
            size = Size(size.width - 40f, size.height - 60f),
            cornerRadius = CornerRadius(12f, 12f),
        )
        val monW = if (dualMonitor) 44f else 60f
        drawRoundRect(
            Color(0xFF0D1A2A),
            Offset(cx - monW - 8f, cy - 30f),
            Size(monW, 40f),
            CornerRadius(4f, 4f),
        )
        if (dualMonitor) {
            drawRoundRect(
                Color(0xFF0D1A2A),
                Offset(cx + 8f, cy - 30f),
                Size(monW, 40f),
                CornerRadius(4f, 4f),
            )
        }
        drawRoundRect(
            NeonCyan.copy(0.4f + phase * 0.35f),
            Offset(cx - monW + 4f, cy - 24f),
            Size(monW - 8f, 28f),
            CornerRadius(3f, 3f),
        )
        drawRoundRect(
            Brush.linearGradient(listOf(NeonPurple, NeonGreen)),
            Offset(cx - 22f, cy - 65f),
            Size(44f, 44f),
            CornerRadius(12f, 12f),
        )
        rotate(phase * 12f - 6f, Offset(cx + 50f, cy - 20f)) {
            drawRoundRect(
                NeonOrange.copy(0.7f),
                Offset(cx + 42f, cy - 28f),
                Size(28f, 8f),
                CornerRadius(4f, 4f),
            )
        }
    }
}

@Composable
private fun SceneOfficeTeam(phase: Float, bob: Float, compact: Boolean) {
    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob
        drawRoundRect(
            Color(0xFF1E2838).copy(0.7f),
            Offset(16f, 24f),
            Size(size.width - 32f, size.height - 48f),
            CornerRadius(10f, 10f),
        )
        repeat(if (compact) 2 else 3) { i ->
            val ox = cx - 50f + i * 35f
            drawRoundRect(
                Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                Offset(ox, cy - 50f),
                Size(28f, 36f),
                CornerRadius(8f, 8f),
            )
        }
        drawRoundRect(
            Color(0xFF0D1A2A),
            Offset(cx - 45f, cy - 5f),
            Size(90f, 50f),
            CornerRadius(6f, 6f),
        )
        drawRoundRect(
            NeonGreen.copy(0.35f + phase * 0.4f),
            Offset(cx - 40f, cy + 2f),
            Size(80f, 30f),
            CornerRadius(4f, 4f),
        )
    }
}
