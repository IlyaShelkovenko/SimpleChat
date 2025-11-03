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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChatMessageBubble(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(RoundedCornerShape(18.dp))
                .background(bubbleColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                textAlign = TextAlign.Start
            )
        }
    }
}
