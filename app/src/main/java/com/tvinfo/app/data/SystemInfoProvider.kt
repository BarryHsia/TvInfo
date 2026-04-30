package com.tvinfo.app.data

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.opengl.GLES20
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.io.RandomAccessFile
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Locale
import java.util.TimeZone

data class SystemMetrics(
    val os: OsInfo,
    val hardware: HardwareInfo,
    val display: DisplayInfo,
    val network: NetworkInfo,
    val software: SoftwareInfo,
    val storage: StorageInfo,
    val environment: EnvironmentInfo
)

data class OsInfo(
    val name: String,
    val version: String,
    val apiLevel: Int,
    val buildId: String
)

data class HardwareInfo(
    val cpuCores: Int,
    val architecture: String,
    val supportedAbis: List<String>,
    val totalRam: String,
    val totalRamBytes: Long,
    val availableRam: String,
    val availableRamBytes: Long,
    val model: String,
    val manufacturer: String,
    val device: String,
    val board: String
)

data class DisplayInfo(
    val resolution: String,
    val widthPx: Int,
    val heightPx: Int,
    val refreshRate: Float,
    val density: Float,
    val densityDpi: Int,
    val colorDepth: String
)

data class NetworkInfo(
    val isConnected: Boolean,
    val type: String,
    val ipAddress: String,
    val gateway: String,
    val dns1: String,
    val dns2: String,
    val macAddress: String,
    val linkSpeed: String,
    val ssid: String
)

data class SoftwareInfo(
    val buildFingerprint: String,
    val securityPatch: String,
    val bootloader: String,
    val kernelVersion: String,
    val seLinuxStatus: String,
    val openGlVersion: String,
    val buildType: String,
    val buildTags: String
)

data class StorageInfo(
    val totalInternal: String,
    val totalInternalBytes: Long,
    val usedInternal: String,
    val usedInternalBytes: Long,
    val availableInternal: String,
    val availableInternalBytes: Long,
    val usagePercent: Int,
    val hasExternalStorage: Boolean,
    val externalTotal: String,
    val fileSystem: String
)

data class EnvironmentInfo(
    val language: String,
    val timezone: String,
    val region: String,
    val serial: String
)

object SystemInfoProvider {

    fun collect(context: Context): SystemMetrics {
        return SystemMetrics(
            os = getOsInfo(),
            hardware = getHardwareInfo(context),
            display = getDisplayInfo(context),
            network = getNetworkInfo(context),
            software = getSoftwareInfo(context),
            storage = getStorageInfo(),
            environment = getEnvironmentInfo()
        )
    }

    private fun getOsInfo(): OsInfo {
        val name = if (Build.VERSION.SDK_INT >= 26) "Android TV" else "Android"
        return OsInfo(
            name = name,
            version = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildId = Build.DISPLAY
        )
    }

    private fun getHardwareInfo(context: Context): HardwareInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        val abis = Build.SUPPORTED_ABIS.toList()

        return HardwareInfo(
            cpuCores = Runtime.getRuntime().availableProcessors(),
            architecture = abis.firstOrNull() ?: "Unknown",
            supportedAbis = abis,
            totalRam = formatBytes(memInfo.totalMem),
            totalRamBytes = memInfo.totalMem,
            availableRam = formatBytes(memInfo.availMem),
            availableRamBytes = memInfo.availMem,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            device = Build.DEVICE,
            board = Build.BOARD
        )
    }

    @Suppress("DEPRECATION")
    private fun getDisplayInfo(context: Context): DisplayInfo {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getRealMetrics(dm)
        val refreshRate = wm.defaultDisplay.refreshRate

        return DisplayInfo(
            resolution = "${dm.widthPixels} × ${dm.heightPixels}",
            widthPx = dm.widthPixels,
            heightPx = dm.heightPixels,
            refreshRate = refreshRate,
            density = dm.density,
            densityDpi = dm.densityDpi,
            colorDepth = "32-bit"
        )
    }

    private fun getNetworkInfo(context: Context): NetworkInfo {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var connected = false
        var type = "Disconnected"
        var linkSpeed = "N/A"
        var ssid = "N/A"
        var dns1 = "N/A"
        var dns2 = "N/A"

        if (Build.VERSION.SDK_INT >= 23) {
            val net = cm.activeNetwork
            val caps = net?.let { cm.getNetworkCapabilities(it) }
            if (caps != null) {
                connected = true
                type = when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                    else -> "Other"
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val ni = cm.activeNetworkInfo
            if (ni != null && ni.isConnected) {
                connected = true
                @Suppress("DEPRECATION")
                type = when (ni.type) {
                    ConnectivityManager.TYPE_WIFI -> "WiFi"
                    ConnectivityManager.TYPE_ETHERNET -> "Ethernet"
                    else -> "Other"
                }
            }
        }

        // WiFi details + DNS from DhcpInfo
        try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            val wifiInfo = wm.connectionInfo
            if (wifiInfo != null) {
                linkSpeed = "${wifiInfo.linkSpeed} Mbps"
                ssid = wifiInfo.ssid?.replace("\"", "") ?: "N/A"
            }
            val dhcp = wm.dhcpInfo
            if (dhcp.dns1 != 0) dns1 = intToIp(dhcp.dns1)
            if (dhcp.dns2 != 0) dns2 = intToIp(dhcp.dns2)
        } catch (_: Exception) {}

        // Fallback: read DNS from system properties
        if (dns1 == "N/A") {
            dns1 = readDnsFromProp()
        }

        return NetworkInfo(
            isConnected = connected,
            type = type,
            ipAddress = getLocalIpAddress(),
            gateway = getGateway(context),
            dns1 = dns1,
            dns2 = dns2,
            macAddress = getMacAddress(),
            linkSpeed = linkSpeed,
            ssid = ssid
        )
    }

    private fun getSoftwareInfo(context: Context): SoftwareInfo {
        val kernelVersion = try {
            System.getProperty("os.version") ?: "Unknown"
        } catch (_: Exception) { "Unknown" }

        val securityPatch = if (Build.VERSION.SDK_INT >= 23) {
            Build.VERSION.SECURITY_PATCH
        } else "Unknown"

        // Read real OpenGL ES version from ActivityManager
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val glVersion = am.deviceConfigurationInfo.glEsVersion ?: "Unknown"

        return SoftwareInfo(
            buildFingerprint = Build.FINGERPRINT,
            securityPatch = securityPatch,
            bootloader = Build.BOOTLOADER,
            kernelVersion = kernelVersion,
            seLinuxStatus = getSeLinuxStatus(),
            openGlVersion = glVersion,
            buildType = Build.TYPE,
            buildTags = Build.TAGS
        )
    }

    private fun getStorageInfo(): StorageInfo {
        val stat = StatFs(Environment.getDataDirectory().path)
        val totalBytes = stat.blockSizeLong * stat.blockCountLong
        val availBytes = stat.blockSizeLong * stat.availableBlocksLong
        val usedBytes = totalBytes - availBytes
        val percent = if (totalBytes > 0) ((usedBytes * 100) / totalBytes).toInt() else 0

        val hasExternal = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        var externalTotal = "N/A"
        if (hasExternal) {
            try {
                @Suppress("DEPRECATION")
                val extStat = StatFs(Environment.getExternalStorageDirectory().path)
                externalTotal = formatBytes(extStat.blockSizeLong * extStat.blockCountLong)
            } catch (_: Exception) {}
        }

        // Detect filesystem type
        val fsType = detectFileSystem()

        return StorageInfo(
            totalInternal = formatBytes(totalBytes),
            totalInternalBytes = totalBytes,
            usedInternal = formatBytes(usedBytes),
            usedInternalBytes = usedBytes,
            availableInternal = formatBytes(availBytes),
            availableInternalBytes = availBytes,
            usagePercent = percent,
            hasExternalStorage = hasExternal,
            externalTotal = externalTotal,
            fileSystem = fsType
        )
    }

    private fun getEnvironmentInfo(): EnvironmentInfo {
        val locale = Locale.getDefault()
        val tz = TimeZone.getDefault()
        return EnvironmentInfo(
            language = "${locale.language}-${locale.country}",
            timezone = tz.id,
            region = tz.id.split("/").getOrElse(1) { "Global" },
            serial = "${Build.MANUFACTURER.take(2).uppercase()}-${Build.MODEL.take(6).uppercase()}"
        )
    }

    // === Helper functions ===

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addrs = iface.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "N/A"
                    }
                }
            }
        } catch (_: Exception) {}
        return "N/A"
    }

    @Suppress("DEPRECATION")
    private fun getGateway(context: Context): String {
        try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val gw = wm.dhcpInfo.gateway
            if (gw != 0) return intToIp(gw)
        } catch (_: Exception) {}
        return "N/A"
    }

    private fun intToIp(ip: Int): String {
        return "${ip and 0xFF}.${ip shr 8 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 24 and 0xFF}"
    }

    private fun readDnsFromProp(): String {
        return try {
            val p = Runtime.getRuntime().exec("getprop net.dns1")
            val result = p.inputStream.bufferedReader().readText().trim()
            if (result.isNotEmpty()) result else "N/A"
        } catch (_: Exception) { "N/A" }
    }

    private fun getMacAddress(): String {
        try {
            val iface = NetworkInterface.getByName("wlan0")
                ?: NetworkInterface.getByName("eth0")
            val mac = iface?.hardwareAddress ?: return "N/A"
            return mac.joinToString(":") { String.format("%02X", it) }
        } catch (_: Exception) {
            return "N/A"
        }
    }

    private fun getSeLinuxStatus(): String {
        return try {
            val file = File("/sys/fs/selinux/enforce")
            if (file.exists()) {
                if (file.readText().trim() == "1") "Enforcing" else "Permissive"
            } else "Disabled"
        } catch (_: Exception) { "Unknown" }
    }

    private fun detectFileSystem(): String {
        return try {
            val mounts = File("/proc/mounts").readText()
            val dataLine = mounts.lines().find { it.contains(" /data ") }
            when {
                dataLine?.contains("f2fs") == true -> "F2FS"
                dataLine?.contains("ext4") == true -> "EXT4"
                dataLine != null -> dataLine.split(" ").getOrElse(2) { "Unknown" }
                else -> "Unknown"
            }
        } catch (_: Exception) { "Unknown" }
    }

    /** Read real CPU usage from /proc/stat */
    fun getCpuUsage(): Float {
        return try {
            val reader = RandomAccessFile("/proc/stat", "r")
            val line1 = reader.readLine()
            reader.close()
            val parts = line1.split("\\s+".toRegex())
            val idle1 = parts[4].toLong()
            val total1 = parts.drop(1).take(7).sumOf { it.toLong() }

            Thread.sleep(100)

            val reader2 = RandomAccessFile("/proc/stat", "r")
            val line2 = reader2.readLine()
            reader2.close()
            val parts2 = line2.split("\\s+".toRegex())
            val idle2 = parts2[4].toLong()
            val total2 = parts2.drop(1).take(7).sumOf { it.toLong() }

            val idleDelta = idle2 - idle1
            val totalDelta = total2 - total1
            if (totalDelta > 0) (1f - idleDelta.toFloat() / totalDelta) else 0f
        } catch (_: Exception) { 0f }
    }

    fun formatBytes(bytes: Long): String {
        val gb = bytes / (1024.0 * 1024.0 * 1024.0)
        return if (gb >= 1.0) {
            String.format("%.1f GB", gb)
        } else {
            String.format("%.0f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
