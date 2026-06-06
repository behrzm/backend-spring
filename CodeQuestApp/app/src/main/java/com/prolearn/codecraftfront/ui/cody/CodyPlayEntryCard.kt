package com.prolearn.codecraftfront.ui.cody

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prolearn.codecraftfront.ui.components.GlassCard
import com.prolearn.codecraftfront.ui.theme.NeonCyan

@Composable
fun CodyPlayEntryCard(
    onOpenCody: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenCody),
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
            CodyRobot(excited = true)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cody · ИИ-напарник",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan,
                )
                Text(
                    text = "Нажми — откроется диалог с Cody",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = NeonCyan,
            )
        }
    }
}
