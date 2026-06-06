package com.prolearn.codecraftfront.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prolearn.codecraftfront.game.ArenaVisualSpec
import com.prolearn.codecraftfront.ui.theme.NeonCyan
import com.prolearn.codecraftfront.ui.theme.NeonGreen
import com.prolearn.codecraftfront.ui.theme.NeonOrange
import com.prolearn.codecraftfront.ui.theme.NeonPink
import com.prolearn.codecraftfront.ui.theme.NeonPurple

/**
 * Visual playfield aligned with the programming task (operands, gates, scales — not a generic maze).
 */
@Composable
fun MissionArenaBoard(
    spec: ArenaVisualSpec,
    fusionProgress: Float,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        accent = NeonPink,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = spec.caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )
            when (spec) {
                is ArenaVisualSpec.BinaryFusion -> BinaryFusionRow(spec, fusionProgress)
                is ArenaVisualSpec.BalancePick -> BalancePickVisual(fusionProgress)
                is ArenaVisualSpec.LogicGate -> LogicGateVisual(spec, fusionProgress)
                is ArenaVisualSpec.TriplePick -> TriplePickVisual(fusionProgress)
                is ArenaVisualSpec.TwinMatch -> TwinMatchVisual(spec, fusionProgress)
            }
        }
    }
}

@Composable
private fun BinaryFusionRow(
    spec: ArenaVisualSpec.BinaryFusion,
    fusionProgress: Float,
) {
    val pulse = rememberInfiniteTransition(label = "arenaPulse")
    val idleGlow by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "idleGlow",
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OperandChip(spec.left, idleGlow)
            Text(
                text = spec.opSymbol,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = NeonCyan,
            )
            OperandChip(spec.right, idleGlow)
        }
        FusionBeam(fusionProgress)
    }
}

@Composable
private fun OperandChip(label: String, idleGlow: Float) {
    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 52.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        NeonPurple.copy(alpha = 0.35f + idleGlow * 0.15f),
                        NeonCyan.copy(alpha = 0.25f + idleGlow * 0.12f),
                    ),
                ),
                cornerRadius = CornerRadius(14f, 14f),
                size = Size(size.width, size.height),
            )
        }
        Text(
            text = label.ifBlank { "·" },
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
        )
    }
}

@Composable
private fun FusionBeam(progress: Float) {
    val fill = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = Color.White.copy(alpha = 0.08f),
            cornerRadius = CornerRadius(h / 2, h / 2),
            size = Size(w, h),
        )
        val fw = w * fill
        if (fw > 4f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(NeonPink.copy(alpha = 0.5f), NeonOrange.copy(alpha = 0.85f), NeonGreen.copy(alpha = 0.9f)),
                ),
                cornerRadius = CornerRadius(h / 2, h / 2),
                size = Size(fw, h),
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.9f * fill), Color.Transparent),
            ),
            radius = h * 0.65f,
            center = Offset(fw.coerceAtLeast(h / 2), h / 2),
        )
    }
    Text(
        text = when {
            fill <= 0.05f -> "Run correct code to charge the fusion lane →"
            fill >= 0.95f -> "Core synced!"
            else -> "Charging… ${(fill * 100).toInt()}%"
        },
        style = MaterialTheme.typography.labelMedium,
        fontFamily = FontFamily.Monospace,
        color = NeonOrange.copy(alpha = 0.85f),
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun BalancePickVisual(fusionProgress: Float) {
    val boost = fusionProgress.coerceIn(0f, 1f) * 0.18f
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        ScaleBar((0.62f + boost).coerceAtMost(1f), NeonOrange)
        Text(
            text = "⚖",
            fontSize = 36.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        ScaleBar((0.42f + boost * 0.7f).coerceAtMost(1f), NeonCyan)
    }
}

@Composable
private fun ScaleBar(fraction: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .width(56.dp)
                .height(120.dp),
        ) {
            val h = size.height * fraction
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.95f), color.copy(alpha = 0.35f))),
                topLeft = Offset(0f, size.height - h),
                size = Size(size.width, h),
                cornerRadius = CornerRadius(12f, 12f),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "signal",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LogicGateVisual(spec: ArenaVisualSpec.LogicGate, fusionProgress: Float) {
    val glow = fusionProgress.coerceIn(0f, 1f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(180.dp, 100.dp)) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        listOf(
                            NeonPurple.copy(alpha = 0.25f + glow * 0.45f),
                            NeonCyan.copy(alpha = 0.20f + glow * 0.5f),
                        ),
                    ),
                    cornerRadius = CornerRadius(28f, 28f),
                    size = size,
                )
                drawRoundRect(
                    color = NeonGreen.copy(alpha = 0.35f + glow * 0.5f),
                    style = Stroke(width = 3f),
                    cornerRadius = CornerRadius(28f, 28f),
                    size = size,
                )
            }
            Text(
                text = spec.expression,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (glow > 0.9f) "Gate open — logic checks out!" else "Solve the exercise to energize the gate.",
            style = MaterialTheme.typography.labelMedium,
            color = NeonGreen.copy(alpha = 0.75f + glow * 0.25f),
        )
    }
}

@Composable
private fun TriplePickVisual(fusionProgress: Float) {
    val highlight = fusionProgress.coerceIn(0f, 1f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        listOf("a", "b", "c").forEachIndexed { i, label ->
            val accent = when (i) {
                0 -> NeonOrange
                1 -> NeonCyan
                else -> NeonPurple
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.size(52.dp, 90.dp)) {
                    val pick = if (i == 2) highlight else 0.35f
                    drawRoundRect(
                        color = accent.copy(alpha = 0.35f + pick * 0.45f),
                        cornerRadius = CornerRadius(16f, 16f),
                        size = size,
                    )
                }
                Text(label, fontFamily = FontFamily.Monospace, color = accent)
            }
        }
    }
}

@Composable
private fun TwinMatchVisual(spec: ArenaVisualSpec.TwinMatch, fusionProgress: Float) {
    val m = fusionProgress.coerceIn(0f, 1f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = spec.left,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = NeonOrange,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (m > 0.85f) "≡" else "?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = NeonCyan.copy(alpha = 0.6f + m * 0.4f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = spec.right,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = NeonPink,
        )
    }
}
