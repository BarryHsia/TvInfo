package com.tvinfo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvinfo.app.ui.theme.AppColors

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    borderColor: Color = AppColors.CardBorder,
    backgroundColor: Color = AppColors.SurfaceCard,
    focusable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val animBorder by animateColorAsState(
        if (isFocused) AppColors.Accent.copy(alpha = 0.6f) else borderColor, tween(200)
    )
    val animBg by animateColorAsState(
        if (isFocused) AppColors.CardHover else backgroundColor, tween(200)
    )
    val elevation by animateFloatAsState(if (isFocused) 12f else 0f, tween(200))

    Column(
        modifier = modifier
            .then(
                if (focusable) Modifier.onFocusChanged { isFocused = it.isFocused }.focusable()
                else Modifier
            )
            .shadow(elevation.dp, RoundedCornerShape(24.dp), ambientColor = AppColors.Accent.copy(alpha = 0.3f), spotColor = AppColors.Accent.copy(alpha = 0.3f))
            .border(if (isFocused) 2.dp else 1.dp, animBorder, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(animBg)
            .padding(20.dp),
        content = content
    )
}

@Composable
fun StatLabel(text: String, color: Color = AppColors.TextTertiary) {
    Text(text.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp, color = color)
}

@Composable
fun MonoValue(text: String, color: Color = AppColors.Accent, fontSize: Int = 14) {
    Text(text, fontSize = fontSize.sp, color = color)
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = AppColors.TextTertiary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

@Composable
fun Tag(text: String, color: Color = AppColors.Purple) {
    Box(
        Modifier.clip(RoundedCornerShape(4.dp)).background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = color)
    }
}

@Composable
fun StatusDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(label.uppercase(), fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
    }
}

@Composable
fun ProgressBar(progress: Float, color: Color = AppColors.Accent, modifier: Modifier = Modifier, height: Dp = 4.dp) {
    val animProgress by animateFloatAsState(progress.coerceIn(0f, 1f), tween(800))
    Box(modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(height / 2)).background(AppColors.White5)) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(animProgress).clip(RoundedCornerShape(height / 2)).background(color))
    }
}

@Composable
fun SegmentedBar(segments: List<Pair<Float, Color>>, modifier: Modifier = Modifier, height: Dp = 16.dp) {
    Row(modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(height / 2)).background(AppColors.White5)) {
        segments.forEach { (fraction, color) ->
            val animFraction by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(800))
            Box(Modifier.fillMaxHeight().weight(animFraction.coerceAtLeast(0.001f)).background(color))
        }
    }
}

@Composable
fun IconBox(
    color: Color,
    size: Dp = 56.dp,
    cornerRadius: Dp = 20.dp,
    icon: ImageVector? = null,
    iconSize: Dp = 28.dp,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(
        Modifier.size(size).clip(RoundedCornerShape(cornerRadius))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize), tint = color)
        }
        content?.invoke(this)
    }
}

// NavItem now uses ImageVector
data class NavItem(val id: String, val label: String, val icon: ImageVector, val color: Color)

val NAV_ITEMS = listOf(
    NavItem("overview", "Overview", Icons.Rounded.FlashOn, AppColors.Accent),
    NavItem("hardware", "Hardware", Icons.Rounded.Memory, AppColors.Orange),
    NavItem("display", "Display", Icons.Rounded.Monitor, AppColors.Purple),
    NavItem("network", "Network", Icons.Rounded.Wifi, AppColors.Emerald),
    NavItem("software", "Software", Icons.Rounded.Shield, AppColors.Blue),
    NavItem("storage", "Storage", Icons.Rounded.Storage, AppColors.Rose),
)
