package com.example.simplechat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChatMessageBubble(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleBrush = if (isUser) {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surface
            )
        )
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(bubbleBrush)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                textAlign = TextAlign.Start,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}
