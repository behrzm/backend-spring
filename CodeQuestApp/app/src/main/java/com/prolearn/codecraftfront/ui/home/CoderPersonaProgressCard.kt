package com.prolearn.codecraftfront.ui.home

import android.content.res.AssetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.prolearn.codecraftfront.game.CoderPersona
import com.prolearn.codecraftfront.game.CoderPersonas
import com.prolearn.codecraftfront.game.LevelProgression
import com.prolearn.codecraftfront.ui.components.GlassCard
import com.prolearn.codecraftfront.ui.theme.NeonCyan
import com.prolearn.codecraftfront.ui.theme.NeonGreen
import com.prolearn.codecraftfront.ui.theme.NeonPurple

@Composable
fun CoderPersonaProgressCard(
    totalXp: Int,
    userLevel: Int,
    modifier: Modifier = Modifier,
) {
    val persona = remember(userLevel) { CoderPersonas.forLevel(userLevel) }
    val displayLevel = userLevel.coerceAtLeast(1)
    val xpForNext = LevelProgression.xpRequiredForNextLevel(displayLevel)
    val xpInLevel = LevelProgression.xpInCurrentLevel(totalXp, displayLevel)
    val progress = (xpInLevel.toFloat() / xpForNext).coerceIn(0f, 1f)
    
    val levelText = "Уровень $displayLevel"

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        accent = NeonGreen,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Убрали заголовок persona.title ("Студент с компьютером")

                Text(
                    text = levelText,
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold,
                )
                
                Text(
                    text = "$totalXp XP",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonGreen, NeonCyan),
                                ),
                            ),
                    )
                }
            }

            // Уменьшили размер гифки до 80.dp для лучшего баланса UI
            CoderPersonaMedia(persona = persona)
        }
    }
}

@Composable
private fun CoderPersonaMedia(persona: CoderPersona) {
    val context = LocalContext.current
    val hasGif = remember(persona.gifAssetPath) {
        assetExists(context.assets, persona.gifAssetPath)
    }

    val imageSize = 80.dp

    if (hasGif) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/${persona.gifAssetPath}")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit,
            loading = { PersonaLoadingBox(imageSize) },
            error = { CoderPersonaScene(persona = persona, size = imageSize) },
        )
    } else {
        CoderPersonaScene(persona = persona, size = imageSize)
    }
}

@Composable
private fun PersonaLoadingBox(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = NeonPurple)
    }
}

private fun assetExists(assets: AssetManager, path: String): Boolean =
    runCatching {
        assets.open(path).close()
        true
    }.getOrDefault(false)

@Composable
private fun CoderPersonaScene(persona: CoderPersona, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(persona.icon, fontSize = 28.sp)
    }
}
