package com.prolearn.codecraftfront.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glassmorphism-styled card. Layers a translucent surface, a subtle white
 * highlight gradient on top, an optional accent tint and a soft border so
 * cards feel like frosted glass over the neon backdrop.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    accent: Color? = null,
    border: Boolean = true,
    surfaceAlpha: Float = 0.55f,
    content: @Composable () -> Unit,
) {
    val baseSurface = MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha)
    val highlight = Color.White.copy(alpha = 0.07f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.30f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (border) BorderStroke(1.dp, borderColor) else null,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(baseSurface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(highlight, Color.Transparent),
                        ),
                    ),
            )
            if (accent != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.22f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                )
            }
            content()
        }
    }
}
