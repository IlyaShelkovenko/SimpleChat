package com.example.simplechat.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SimpleChatTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
)
