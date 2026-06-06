package com.prolearn.codecraftfront.ui.cody

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.prolearn.codecraftfront.ui.theme.NeonCyan
import com.prolearn.codecraftfront.ui.theme.NeonGreen
import com.prolearn.codecraftfront.ui.theme.NeonPurple

@Composable
fun CodyRobot(
    modifier: Modifier = Modifier,
    excited: Boolean = false,
) {
    val infinite = rememberInfiniteTransition(label = "cody")
    val bob by infinite.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bob",
    )
    val antennaGlow by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "antenna",
    )
    val blinkCycle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400),
            repeatMode = RepeatMode.Restart,
        ),
        label = "blinkCycle",
    )
    val eyesOpen = blinkCycle < 0.92f
    val armWave by infinite.animateFloat(
        initialValue = -8f,
        targetValue = if (excited) 14f else 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (excited) 500 else 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "arm",
    )

    Canvas(modifier = modifier.size(88.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob

        // Shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(cx - 28f, size.height - 14f),
            size = Size(56f, 10f),
        )

        // Left arm
        rotate(degrees = -18f + armWave, pivot = Offset(cx - 34f, cy + 8f)) {
            drawRoundRect(
                brush = Brush.linearGradient(listOf(NeonPurple.copy(0.9f), NeonCyan.copy(0.7f))),
                topLeft = Offset(cx - 48f, cy - 4f),
                size = Size(18f, 36f),
                cornerRadius = CornerRadius(8f, 8f),
            )
        }

        // Body
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF2A1F4E), Color(0xFF1A1238)),
            ),
            topLeft = Offset(cx - 30f, cy + 2f),
            size = Size(60f, 44f),
            cornerRadius = CornerRadius(14f, 14f),
        )
        // Chest screen
        drawRoundRect(
            color = Color(0xFF0D1A2A),
            topLeft = Offset(cx - 22f, cy + 10f),
            size = Size(44f, 26f),
            cornerRadius = CornerRadius(6f, 6f),
        )
        drawRoundRect(
            color = NeonGreen.copy(alpha = 0.85f),
            topLeft = Offset(cx - 16f, cy + 18f),
            size = Size(10f, 10f),
            cornerRadius = CornerRadius(2f, 2f),
        )
        drawRoundRect(
            color = NeonCyan.copy(alpha = 0.7f),
            topLeft = Offset(cx - 2f, cy + 16f),
            size = Size(14f, 6f),
            cornerRadius = CornerRadius(2f, 2f),
        )
        drawRoundRect(
            color = NeonPurple.copy(alpha = 0.6f),
            topLeft = Offset(cx + 8f, cy + 20f),
            size = Size(8f, 8f),
            cornerRadius = CornerRadius(2f, 2f),
        )

        // Right arm
        rotate(degrees = 18f - armWave * 0.7f, pivot = Offset(cx + 34f, cy + 8f)) {
            drawRoundRect(
                brush = Brush.linearGradient(listOf(NeonCyan.copy(0.8f), NeonPurple.copy(0.7f))),
                topLeft = Offset(cx + 30f, cy - 4f),
                size = Size(18f, 36f),
                cornerRadius = CornerRadius(8f, 8f),
            )
        }

        // Head
        drawRoundRect(
            brush = Brush.linearGradient(
                listOf(Color(0xFF3D2E6B), Color(0xFF5B45A8)),
            ),
            topLeft = Offset(cx - 36f, cy - 52f),
            size = Size(72f, 58f),
            cornerRadius = CornerRadius(20f, 20f),
        )

        // Antenna
        drawLine(
            color = Color(0xFF8B7CC8),
            start = Offset(cx, cy - 52f),
            end = Offset(cx, cy - 68f),
            strokeWidth = 4f,
        )
        drawCircle(
            color = NeonCyan.copy(alpha = antennaGlow),
            radius = 7f,
            center = Offset(cx, cy - 72f),
        )
        drawCircle(
            color = NeonCyan.copy(alpha = 0.25f),
            radius = 14f,
            center = Offset(cx, cy - 72f),
        )

        // Eyes
        val eyeY = cy - 28f
        val eyeW = if (eyesOpen) 12f else 12f
        val eyeH = if (eyesOpen) 14f else 3f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx - 22f, eyeY - eyeH / 2),
            size = Size(eyeW, eyeH),
            cornerRadius = CornerRadius(6f, 6f),
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx + 10f, eyeY - eyeH / 2),
            size = Size(eyeW, eyeH),
            cornerRadius = CornerRadius(6f, 6f),
        )
        if (eyesOpen) {
            drawCircle(NeonCyan, radius = 4f, center = Offset(cx - 16f, eyeY + 2f))
            drawCircle(NeonCyan, radius = 4f, center = Offset(cx + 16f, eyeY + 2f))
        }

        // Smile
        val smile = Path().apply {
            moveTo(cx - 14f, cy - 10f)
            quadraticBezierTo(cx, cy - 2f, cx + 14f, cy - 10f)
        }
        drawPath(
            smile,
            color = NeonGreen.copy(0.9f),
            style = Stroke(width = 3f),
        )

        // Ear lights
        drawCircle(NeonPurple.copy(0.5f), 5f, Offset(cx - 40f, cy - 22f))
        drawCircle(NeonPurple.copy(0.5f), 5f, Offset(cx + 40f, cy - 22f))
    }
}
