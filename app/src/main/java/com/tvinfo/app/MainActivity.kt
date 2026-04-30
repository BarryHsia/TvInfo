package com.tvinfo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvinfo.app.data.SystemInfoProvider
import com.tvinfo.app.data.SystemMetrics
import com.tvinfo.app.ui.components.*
import com.tvinfo.app.ui.screens.*
import com.tvinfo.app.ui.theme.AppColors
import com.tvinfo.app.ui.theme.TvInfoTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TvInfoTheme {
                val metrics = remember { SystemInfoProvider.collect(this@MainActivity) }
                val analytics = remember { FirebaseAnalytics.getInstance(this@MainActivity) }
                TvInfoApp(metrics, analytics)
            }
        }
    }
}

private const val NAV_COLLAPSED = 72
private const val NAV_EXPANDED = 240

@Composable
fun TvInfoApp(metrics: SystemMetrics, analytics: FirebaseAnalytics) {
    var activeTab by remember { mutableStateOf("overview") }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var sidebarExpanded by remember { mutableStateOf(false) }

    // Log tab switch to Firebase Analytics
    LaunchedEffect(activeTab) {
        analytics.logEvent("tab_switch") {
            param("tab_name", activeTab)
        }
    }

    val navFocusRequesters = remember { NAV_ITEMS.map { FocusRequester() } }
    val contentFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { navFocusRequesters[0].requestFocus() }
    LaunchedEffect(Unit) {
        while (true) { currentTime = System.currentTimeMillis(); kotlinx.coroutines.delay(1000) }
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
    val timeString = timeFormat.format(Date(currentTime))
    val dateString = dateFormat.format(Date(currentTime))

    val sidebarWidth by animateDpAsState(
        if (sidebarExpanded) NAV_EXPANDED.dp else NAV_COLLAPSED.dp,
        tween(280, easing = FastOutSlowInEasing)
    )
    // Content dims slightly when sidebar expanded (0.6 = not too dark)
    val contentAlpha by animateFloatAsState(
        if (sidebarExpanded) 0.6f else 1f, tween(280)
    )
    val activeNavIndex = remember(activeTab) { NAV_ITEMS.indexOfFirst { it.id == activeTab }.coerceAtLeast(0) }

    Box(
        Modifier.fillMaxSize().background(AppColors.Background).drawBehind {
            drawCircle(Brush.radialGradient(listOf(AppColors.Accent.copy(alpha = 0.05f), Color.Transparent), center = Offset(0f, 0f), radius = size.maxDimension * 0.5f), radius = size.maxDimension * 0.5f, center = Offset(0f, 0f))
            drawCircle(Brush.radialGradient(listOf(Color(0xFF3B82F6).copy(alpha = 0.05f), Color.Transparent), center = Offset(size.width, size.height), radius = size.maxDimension * 0.5f), radius = size.maxDimension * 0.5f, center = Offset(size.width, size.height))
        }
    ) {
        Row(Modifier.fillMaxSize()) {
            // === SIDEBAR: fixed layout, only width + alpha animate ===
            NavSidebar(
                activeTab = activeTab,
                expanded = sidebarExpanded,
                width = sidebarWidth,
                onTabSelected = { activeTab = it },
                onExpandChanged = { sidebarExpanded = it },
                focusRequesters = navFocusRequesters,
                onNavigateToContent = { sidebarExpanded = false; contentFocusRequester.requestFocus() }
            )

            // === CONTENT ===
            Column(
                Modifier.weight(1f).fillMaxHeight().alpha(contentAlpha)
                    .focusRequester(contentFocusRequester)
                    .onFocusChanged { if (it.hasFocus || it.isFocused) sidebarExpanded = false }
                    .onKeyEvent { e ->
                        if (e.type == KeyEventType.KeyDown && e.key == Key.DirectionLeft) {
                            navFocusRequesters[activeNavIndex].requestFocus(); true
                        } else false
                    }
            ) {
                Header(metrics, timeString, dateString)
                Box(Modifier.weight(1f).padding(start = 16.dp, end = 32.dp, bottom = 4.dp)) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { (fadeIn(tween(200)) + slideInHorizontally(tween(250)) { it / 10 }).togetherWith(fadeOut(tween(150)) + slideOutHorizontally(tween(200)) { -it / 10 }) },
                        label = "tab"
                    ) { tab ->
                        when (tab) {
                            "overview" -> OverviewTab(metrics)
                            "hardware" -> HardwareTab(metrics)
                            "display" -> DisplayTab(metrics)
                            "network" -> NetworkTab(metrics)
                            "software" -> SoftwareTab(metrics)
                            "storage" -> StorageTab(metrics)
                        }
                    }
                }
                Footer()
            }
        }
    }
}

// === SIDEBAR: All children always composed, text uses alpha animation only, no layout changes ===
@Composable
fun NavSidebar(
    activeTab: String, expanded: Boolean, width: androidx.compose.ui.unit.Dp,
    onTabSelected: (String) -> Unit, onExpandChanged: (Boolean) -> Unit,
    focusRequesters: List<FocusRequester>, onNavigateToContent: () -> Unit
) {
    // Text alpha animates smoothly — no conditional composition
    val textAlpha by animateFloatAsState(if (expanded) 1f else 0f, tween(if (expanded) 250 else 120))

    Column(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .background(Color(0xFF0A0B0E))
            .drawBehind { drawLine(Color.White.copy(alpha = 0.08f), Offset(size.width, 0f), Offset(size.width, size.height), 1f) }
            .padding(top = 16.dp),
        // FIXED alignment — never changes, avoids layout jump
        horizontalAlignment = Alignment.Start
    ) {
        // Logo: icon always left-aligned, text always present but alpha-controlled
        Row(
            Modifier.padding(start = 15.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(AppColors.Accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Settings, null, Modifier.size(24.dp), tint = Color.Black)
            }
            // Always composed, just invisible when collapsed (no layout change)
            Column(Modifier.alpha(textAlpha)) {
                Text("Tv Info", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = (-0.5).sp, maxLines = 1, overflow = TextOverflow.Clip)
                Text("SYSTEM INFO // V1.0", fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = AppColors.Accent, maxLines = 1, overflow = TextOverflow.Clip)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Nav items
        Column(Modifier.padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            NAV_ITEMS.forEachIndexed { index, item ->
                NavButton(
                    item = item, isActive = activeTab == item.id, expanded = expanded, textAlpha = textAlpha,
                    focusRequester = focusRequesters[index],
                    onSelect = { onTabSelected(item.id) },
                    onFocusEnter = { onExpandChanged(true) },
                    onNavigateToContent = onNavigateToContent,
                    onUp = { if (index > 0) focusRequesters[index - 1].requestFocus() },
                    onDown = { if (index < NAV_ITEMS.lastIndex) focusRequesters[index + 1].requestFocus() }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // System Health: always composed, alpha-controlled, NO expandVertically
        Box(
            Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth()
                .alpha(textAlpha) // fade only, no vertical expand
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.SurfaceCard)
                .border(1.dp, AppColors.CardBorder, RoundedCornerShape(14.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)).background(AppColors.EmeraldFill.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Shield, null, Modifier.size(16.dp), tint = AppColors.EmeraldFill)
                }
                Column(Modifier.alpha(textAlpha)) {
                    Text("SYSTEM HEALTH", fontSize = 8.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary, maxLines = 1, overflow = TextOverflow.Clip)
                    Text("Optimal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Clip)
                }
            }
        }
    }
}

// === NAV BUTTON: text always composed, alpha-controlled ===
@Composable
fun NavButton(
    item: NavItem, isActive: Boolean, expanded: Boolean, textAlpha: Float,
    focusRequester: FocusRequester, onSelect: () -> Unit,
    onFocusEnter: () -> Unit, onNavigateToContent: () -> Unit,
    onUp: () -> Unit, onDown: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val bgAlpha by animateFloatAsState(when { isFocused -> 0.15f; isActive -> 0.08f; else -> 0f }, tween(150))
    val iconTint by animateColorAsState(when { isFocused || isActive -> item.color; else -> Color.White.copy(alpha = 0.4f) }, tween(150))

    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = bgAlpha))
            .then(
                if (isFocused) Modifier.border(1.5.dp, AppColors.Accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                else if (isActive) Modifier.border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                else Modifier
            )
            .focusRequester(focusRequester)
            .onFocusChanged { s -> isFocused = s.isFocused; if (s.isFocused) { onFocusEnter(); onSelect() } }
            .focusable()
            .onKeyEvent { e ->
                if (e.type == KeyEventType.KeyDown) when (e.key) {
                    Key.DirectionUp -> { onUp(); true }
                    Key.DirectionDown -> { onDown(); true }
                    Key.DirectionRight, Key.DirectionCenter, Key.Enter -> { onNavigateToContent(); true }
                    else -> false
                } else false
            }
            .padding(start = 14.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Active indicator — always takes space, just invisible when not active
            Box(
                Modifier.width(3.dp).height(22.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) AppColors.Accent else Color.Transparent)
            )
            Icon(item.icon, item.label, Modifier.size(22.dp), tint = iconTint)
            // Text always composed — alpha controlled, no layout change
            Text(
                item.label, fontSize = 15.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = (-0.3).sp,
                color = if (isActive || isFocused) Color.White else AppColors.TextTertiary,
                maxLines = 1, overflow = TextOverflow.Clip,
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}

@Composable
fun Header(metrics: SystemMetrics, timeString: String, dateString: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(AppColors.Accent))
                Text("REMOTE NODE ACCESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp, color = AppColors.TextTertiary)
            }
            Spacer(Modifier.height(4.dp))
            Text("${metrics.environment.region} // ALPHA_UNIT", fontSize = 14.sp, letterSpacing = 4.sp, color = AppColors.TextSecondary.copy(alpha = 0.6f))
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.Schedule, null, Modifier.size(16.dp), tint = AppColors.Accent)
                Text("REAL-TIME CLOCK", fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = AppColors.Accent)
            }
            Text(timeString, fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = (-2).sp)
            Text(dateString.uppercase(), fontSize = 10.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Medium, color = AppColors.TextTertiary)
        }
    }
}

@Composable
fun Footer() {
    Row(
        Modifier.fillMaxWidth().height(44.dp)
            .drawBehind { drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, 0f), Offset(size.width, 0f), 1f) }
            .background(Color.Black.copy(alpha = 0.4f)).padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(AppColors.Accent))
                Text("ENGINE ACTIVE", fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(AppColors.EmeraldFill))
                Text("SECURED NODE", fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
            }
        }
        Text("AUTHORIZED SYSTEM CONSOLE // Tv Info", fontSize = 10.sp, letterSpacing = 3.sp, color = AppColors.TextDim)
    }
}
