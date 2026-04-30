package com.tvinfo.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvinfo.app.data.SystemInfoProvider
import com.tvinfo.app.data.SystemMetrics
import com.tvinfo.app.ui.components.*
import com.tvinfo.app.ui.theme.AppColors

// ==================== OVERVIEW TAB ====================
@Composable
fun OverviewTab(m: SystemMetrics) {
    var cpuUsage by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        cpuUsage = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            SystemInfoProvider.getCpuUsage()
        }
    }
    val memUsedBytes = m.hardware.totalRamBytes - m.hardware.availableRamBytes
    val memUsage = if (m.hardware.totalRamBytes > 0) memUsedBytes.toFloat() / m.hardware.totalRamBytes else 0f

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Device Profile — Smartphone/Tv icon
            BentoCard(modifier = Modifier.weight(4f).heightIn(min = 160.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        StatLabel("Device Profile")
                        Spacer(Modifier.height(8.dp))
                        Text("${m.os.name} Device", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-1).sp)
                        Spacer(Modifier.height(8.dp))
                        MonoValue("Build Version ${m.os.version} // API ${m.os.apiLevel}", color = AppColors.Accent, fontSize = 10)
                    }
                    IconBox(color = Color.White.copy(alpha = 0.2f), size = 56.dp, icon = Icons.Rounded.Tv, iconSize = 32.dp)
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.fillMaxWidth().height(1.dp).background(AppColors.White5))
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column { StatLabel("Model"); Text(m.hardware.model, fontSize = 14.sp, color = AppColors.TextSecondary) }
                    Column { StatLabel("Region"); Text(m.environment.region.uppercase(), fontSize = 14.sp, color = AppColors.TextSecondary) }
                    Column { StatLabel("Language"); Text(m.environment.language.uppercase(), fontSize = 14.sp, color = AppColors.TextSecondary) }
                }
            }

            // Processor — Cpu icon
            BentoCard(modifier = Modifier.weight(2f).heightIn(min = 160.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatLabel("Processor", color = AppColors.Orange)
                    Icon(Icons.Rounded.Memory, contentDescription = null, modifier = Modifier.size(20.dp), tint = AppColors.Orange.copy(alpha = 0.5f))
                }
                Spacer(Modifier.weight(1f))
                Text("${m.hardware.cpuCores} Cores", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(m.hardware.architecture, fontSize = 12.sp, color = AppColors.TextTertiary)
                Spacer(Modifier.height(12.dp))
                ProgressBar(progress = cpuUsage, color = AppColors.OrangeFill)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Memory — Activity/ShowChart icon
            BentoCard(modifier = Modifier.weight(2f).heightIn(min = 160.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatLabel("Memory", color = AppColors.Purple)
                    Icon(Icons.Rounded.ShowChart, contentDescription = null, modifier = Modifier.size(20.dp), tint = AppColors.Purple.copy(alpha = 0.5f))
                }
                Spacer(Modifier.weight(1f))
                Text(m.hardware.totalRam, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${String.format("%.0f", memUsage * 100)}% Utilized", fontSize = 12.sp, color = AppColors.TextTertiary)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ProgressBar(progress = memUsage, color = AppColors.Purple, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                    MonoValue("${m.hardware.availableRam} Free", color = AppColors.Purple, fontSize = 12)
                }
            }

            // Connectivity
            BentoCard(modifier = Modifier.weight(4f).heightIn(min = 160.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        StatLabel("Connectivity", color = AppColors.Emerald)
                        Spacer(Modifier.height(4.dp))
                        Text(if (m.network.isConnected) "LINK_ESTABLISHED" else "DISCONNECTED", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.5).sp)
                    }
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(AppColors.EmeraldFill.copy(alpha = 0.1f))
                            .border(1.dp, AppColors.EmeraldFill.copy(alpha = 0.2f), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(if (m.network.isConnected) AppColors.EmeraldFill else Color.Red))
                        Text(if (m.network.isConnected) "ONLINE" else "OFFLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = if (m.network.isConnected) AppColors.Emerald else Color.Red)
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column { StatLabel("Downlink"); Text(m.network.linkSpeed, fontSize = 18.sp, color = Color.White) }
                    Column { StatLabel("Type"); Text(m.network.type.uppercase(), fontSize = 18.sp, color = Color.White) }
                    Column { StatLabel("Protocol"); Text("IPv4/v6", fontSize = 18.sp, color = Color.White) }
                }
            }
        }
    }
}

// ==================== HARDWARE TAB ====================
@Composable
fun HardwareTab(m: SystemMetrics) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BentoCard(backgroundColor = AppColors.OrangeFill.copy(alpha = 0.05f), borderColor = AppColors.OrangeFill.copy(alpha = 0.1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconBox(color = AppColors.Orange, size = 72.dp, cornerRadius = 24.dp, icon = Icons.Rounded.Memory, iconSize = 40.dp)
                Column {
                    StatLabel("Main Processor (SoC)", color = AppColors.Orange)
                    Spacer(Modifier.height(4.dp))
                    Text("Architecture: ${m.hardware.architecture}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${m.hardware.cpuCores}-core • ${m.hardware.board}", fontSize = 14.sp, color = AppColors.TextTertiary)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("Device Info")
                Spacer(Modifier.height(12.dp))
                InfoRow("Model", m.hardware.model)
                InfoRow("Manufacturer", m.hardware.manufacturer.replaceFirstChar { it.uppercase() })
                InfoRow("Device", m.hardware.device)
                InfoRow("Board", m.hardware.board)
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("RAM Info")
                Spacer(Modifier.height(12.dp))
                InfoRow("Total", m.hardware.totalRam)
                InfoRow("Available", m.hardware.availableRam)
                InfoRow("Used", SystemInfoProvider.formatBytes(m.hardware.totalRamBytes - m.hardware.availableRamBytes))
            }
        }

        BentoCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                StatLabel("Supported ABIs")
                Icon(Icons.Rounded.Layers, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppColors.TextDim)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                m.hardware.supportedAbis.forEach { abi ->
                    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(AppColors.White5).border(1.dp, AppColors.White10, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(abi.uppercase(), fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextSecondary)
                    }
                }
            }
        }
    }
}

// ==================== DISPLAY TAB ====================
@Composable
fun DisplayTab(m: SystemMetrics) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(3f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        StatLabel("Current Resolution", color = AppColors.Purple)
                        Spacer(Modifier.height(4.dp))
                        Text(m.display.resolution, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-2).sp)
                    }
                    IconBox(color = AppColors.Purple, size = 48.dp, cornerRadius = 12.dp, icon = Icons.Rounded.Fullscreen, iconSize = 24.dp)
                }
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("HDR10+", "HLG", "V-Sync", "BT2020").forEach { tag -> Tag(tag, AppColors.Purple) }
                }
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    StatLabel("Refresh")
                    Spacer(Modifier.height(8.dp))
                    Text("${m.display.refreshRate.toInt()}Hz", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("SMOOTH MOTION", fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("Color Space")
                Spacer(Modifier.height(12.dp))
                InfoRow("Bit Depth", m.display.colorDepth)
                InfoRow("Pixel Ratio", "${m.display.density}x")
                InfoRow("Density", "${m.display.densityDpi} dpi")
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("Orientation")
                Spacer(Modifier.height(8.dp))
                Text("Landscape (Primary)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("LOCKED BY TV HARDWARE", fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
            }
        }
    }
}

// ==================== NETWORK TAB ====================
@Composable
fun NetworkTab(m: SystemMetrics) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BentoCard(backgroundColor = AppColors.EmeraldFill.copy(alpha = 0.05f), borderColor = AppColors.EmeraldFill.copy(alpha = 0.1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconBox(color = AppColors.Emerald, size = 56.dp, icon = Icons.Rounded.Wifi, iconSize = 32.dp)
                Column {
                    Text("Active Interface: ${m.network.type}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.5).sp)
                    if (m.network.ssid != "N/A" && m.network.ssid.isNotEmpty()) {
                        MonoValue("SSID: ${m.network.ssid}", color = AppColors.Emerald.copy(alpha = 0.7f), fontSize = 12)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column { StatLabel("IP_ADDR"); Text(m.network.ipAddress, fontSize = 16.sp, color = Color.White) }
                Column { StatLabel("GATEWAY"); Text(m.network.gateway, fontSize = 16.sp, color = Color.White) }
                Column { StatLabel("DNS"); Text(m.network.dns1, fontSize = 16.sp, color = Color.White) }
                Column { StatLabel("LINK_SPEED"); Text(m.network.linkSpeed, fontSize = 16.sp, color = Color.White) }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("Connection Details")
                Spacer(Modifier.height(12.dp))
                InfoRow("DNS 1", m.network.dns1)
                InfoRow("DNS 2", m.network.dns2)
                InfoRow("SSID", m.network.ssid)
                InfoRow("Status", if (m.network.isConnected) "Connected" else "Disconnected", valueColor = if (m.network.isConnected) AppColors.Emerald else Color.Red)
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("MAC Address")
                Spacer(Modifier.weight(1f))
                Text(m.network.macAddress, fontSize = 18.sp, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("PHYSICAL HARDWARE ID", fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextDim)
            }
        }
    }
}

// ==================== SOFTWARE TAB ====================
@Composable
fun SoftwareTab(m: SystemMetrics) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BentoCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconBox(color = AppColors.Blue, size = 56.dp, icon = Icons.Rounded.Shield, iconSize = 32.dp)
                Column {
                    StatLabel("Environment Details", color = AppColors.Blue)
                    Spacer(Modifier.height(4.dp))
                    Text("Build: ${m.software.buildType.uppercase()}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(m.os.buildId, fontSize = 12.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("Core Runtime")
                Spacer(Modifier.height(12.dp))
                InfoRow("Android API", "Level ${m.os.apiLevel}")
                InfoRow("OpenGLES", "v${m.software.openGlVersion}")
                InfoRow("Build Tags", m.software.buildTags)
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                StatLabel("Security")
                Spacer(Modifier.height(12.dp))
                InfoRow("S-Patch", m.software.securityPatch)
                InfoRow("SELinux", m.software.seLinuxStatus, valueColor = AppColors.Blue)
                InfoRow("Bootloader", m.software.bootloader, valueColor = AppColors.Orange)
            }
        }

        BentoCard(backgroundColor = Color.White.copy(alpha = 0.01f)) {
            StatLabel("Kernel Identity")
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.4f)).border(1.dp, AppColors.White5, RoundedCornerShape(12.dp)).padding(16.dp)) {
                Text(m.software.kernelVersion, fontSize = 10.sp, color = AppColors.Blue.copy(alpha = 0.7f), lineHeight = 18.sp)
            }
        }

        BentoCard(backgroundColor = Color.White.copy(alpha = 0.01f)) {
            StatLabel("Build Fingerprint")
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.4f)).border(1.dp, AppColors.White5, RoundedCornerShape(12.dp)).padding(16.dp)) {
                Text(m.software.buildFingerprint, fontSize = 10.sp, color = AppColors.Blue.copy(alpha = 0.7f), lineHeight = 18.sp)
            }
        }
    }
}

// ==================== STORAGE TAB ====================
@Composable
fun StorageTab(m: SystemMetrics) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BentoCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    StatLabel("Internal Storage", color = AppColors.Rose)
                    Spacer(Modifier.height(4.dp))
                    Text("${m.storage.usedInternal} / ${m.storage.totalInternal}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("USAGE STATUS", fontSize = 10.sp, letterSpacing = 2.sp, color = AppColors.TextTertiary)
                    Text("${m.storage.usagePercent}% Full", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Rose)
                }
            }
            Spacer(Modifier.height(16.dp))
            val usedFraction = if (m.storage.totalInternalBytes > 0) m.storage.usedInternalBytes.toFloat() / m.storage.totalInternalBytes else 0f
            SegmentedBar(segments = listOf(
                usedFraction * 0.58f to AppColors.RoseFill,
                usedFraction * 0.26f to AppColors.OrangeFill.copy(alpha = 0.6f),
                usedFraction * 0.16f to Color(0xFF4B5563),
                (1f - usedFraction) to AppColors.White10
            ))
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatusDot(AppColors.RoseFill, "System OS")
                StatusDot(AppColors.OrangeFill.copy(alpha = 0.6f), "Installed Apps")
                StatusDot(Color(0xFF4B5563), "Temp Cache")
                StatusDot(AppColors.White10, "Available")
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    StatLabel("External Expansion")
                    Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppColors.TextDim)
                }
                Spacer(Modifier.height(12.dp))
                Text(if (m.storage.hasExternalStorage) "External: ${m.storage.externalTotal}" else "No USB Drive Detected", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD1D5DB))
                Text("MOUNT POINT: /mnt/media_rw/", fontSize = 12.sp, letterSpacing = 2.sp, color = AppColors.TextDim)
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    StatLabel("File System")
                    Icon(Icons.Rounded.Storage, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppColors.TextDim)
                }
                Spacer(Modifier.height(12.dp))
                Text("Format: ${m.storage.fileSystem}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD1D5DB))
                Text("ENCRYPTION: FBE", fontSize = 12.sp, letterSpacing = 2.sp, color = AppColors.TextDim)
            }
        }
    }
}
