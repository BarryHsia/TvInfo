package com.tvinfo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppColors {
    val Background = Color(0xFF030406)
    val Surface = Color(0xFF14161B)
    val SurfaceCard = Color(0x08FFFFFF)
    val CardBorder = Color(0x0FFFFFFF)
    val CardHover = Color(0xFF1A1D23)

    val Accent = Color(0xFF22D3EE)

    val TextPrimary = Color.White
    val TextSecondary = Color(0xFF9CA3AF)
    val TextTertiary = Color(0xFF6B7280)
    val TextDim = Color(0xFF4B5563)

    val Orange = Color(0xFFFB923C)
    val OrangeFill = Color(0xFFF97316)

    val Purple = Color(0xFFA855F7)

    val Emerald = Color(0xFF34D399)
    val EmeraldFill = Color(0xFF10B981)

    val Blue = Color(0xFF60A5FA)

    val Rose = Color(0xFFFB7185)
    val RoseFill = Color(0xFFF43F5E)

    val White5 = Color(0x0DFFFFFF)
    val White10 = Color(0x1AFFFFFF)
}

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Accent,
    onPrimary = Color.Black,
    background = AppColors.Background,
    surface = AppColors.Surface,
    onBackground = AppColors.TextSecondary,
    onSurface = AppColors.TextSecondary,
    outline = AppColors.CardBorder
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Black, fontSize = 48.sp, letterSpacing = (-0.02).sp, color = Color.White),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, letterSpacing = (-0.02).sp, color = Color.White),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.01).sp, color = Color.White),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD1D5DB)),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.White),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color(0xFF9CA3AF)),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, color = Color(0xFF9CA3AF)),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 10.sp, color = Color(0xFF6B7280)),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 2.sp, color = Color(0xFF6B7280))
)

@Composable
fun TvInfoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = AppTypography, content = content)
}
