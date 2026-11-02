package com.example.ui.screens

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.StatFs
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGreenSuccess
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import java.util.Locale

private data class SpecItem(val label: String, val value: String, val icon: ImageVector)

@Composable
fun DeviceSpecsTab(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
    val dataStats = StatFs(context.filesDir.absolutePath)
    val totalStorage = dataStats.totalBytes
    val freeStorage = dataStats.availableBytes
    val usedStorage = (totalStorage - freeStorage).coerceAtLeast(0L)
    val cpuCores = Runtime.getRuntime().availableProcessors().toString()

    val systemSpecs = listOf(
        SpecItem("Manufacturer", Build.MANUFACTURER.orDash(), Icons.Default.PhoneAndroid),
        SpecItem("Device model", Build.MODEL.orDash(), Icons.Default.PhoneAndroid),
        SpecItem("Device codename", Build.DEVICE.orDash(), Icons.Default.Tune),
        SpecItem("Product", Build.PRODUCT.orDash(), Icons.Default.Android),
        SpecItem("Hardware", Build.HARDWARE.orDash(), Icons.Default.Tune),
        SpecItem("Board", Build.BOARD.orDash(), Icons.Default.Tune),
        SpecItem("Bootloader", Build.BOOTLOADER.orDash(), Icons.Default.Security)
    )
    val androidSpecs = listOf(
        SpecItem("Android version", Build.VERSION.RELEASE.orDash(), Icons.Default.Android),
        SpecItem("API level", Build.VERSION.SDK_INT.toString(), Icons.Default.Tune),
        SpecItem("Build ID", Build.DISPLAY.orDash(), Icons.Default.Tune),
        SpecItem("Security patch", Build.VERSION.SECURITY_PATCH.orDash(), Icons.Default.Security)
    )
    val performanceSpecs = listOf(
        SpecItem("CPU architecture", Build.SUPPORTED_ABIS.firstOrNull().orDash(), Icons.Default.Memory),
        SpecItem("Available CPU cores", cpuCores, Icons.Default.Memory),
        SpecItem("Total RAM", formatBytes(memoryInfo.totalMem), Icons.Default.Memory),
        SpecItem("Available RAM", formatBytes(memoryInfo.availMem), Icons.Default.Memory)
    )
    val storageSpecs = listOf(
        SpecItem("App data capacity", formatBytes(totalStorage), Icons.Default.Storage),
        SpecItem("Available app storage", formatBytes(freeStorage), Icons.Default.Storage),
        SpecItem("Used app storage", formatBytes(usedStorage), Icons.Default.Storage)
    )

    LazyColumn(
        modifier = modifier.fillMaxSize().background(CyberDarkBg).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(6.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CyberSurface)
                    .border(1.dp, CyberCyanPrimary.copy(alpha = 0.24f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(15.dp))
                            .background(CyberCyanPrimary.copy(alpha = 0.12f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.size(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DEVICE PROFILE", color = CyberCyanPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(Build.MODEL.orDash(), color = CyberTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp)
                        Text(Build.MANUFACTURER.orDash(), color = CyberTextSecondary, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniStat("ANDROID", Build.VERSION.RELEASE.orDash(), Modifier.weight(1f))
                    MiniStat("CPU CORES", cpuCores, Modifier.weight(1f))
                    MiniStat("TOTAL RAM", formatBytes(memoryInfo.totalMem), Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.size(7.dp).clip(CircleShape).background(CyberGreenSuccess))
                    Spacer(Modifier.size(7.dp))
                    Text("Values read from the current device", color = CyberTextTertiary, fontSize = 11.sp)
                }
            }
        }

        item { SectionHeading("DEVICE", "Hardware and identity", Icons.Default.PhoneAndroid) }
        items(systemSpecs, key = { it.label }) { spec -> SpecRow(spec) }

        item { SectionHeading("ANDROID", "OS and security details", Icons.Default.Security) }
        items(androidSpecs, key = { it.label }) { spec -> SpecRow(spec) }

        item { SectionHeading("PERFORMANCE", "Memory and processor", Icons.Default.Memory) }
        items(performanceSpecs, key = { it.label }) { spec -> SpecRow(spec) }

        item { SectionHeading("STORAGE", "App data filesystem", Icons.Default.Storage) }
        items(storageSpecs, key = { it.label }) { spec -> SpecRow(spec) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 18.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberSurfaceVariant)
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = CyberTextTertiary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text(
                    "Storage values describe the app's data filesystem, not necessarily the phone's full physical storage. Some fields depend on the device manufacturer.",
                    color = CyberTextTertiary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SectionHeading(kicker: String, title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(kicker, color = CyberCyanPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(title, color = CyberTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
            .background(CyberSurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Text(label, color = CyberTextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
        Spacer(Modifier.height(5.dp))
        Text(value, color = CyberTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, lineHeight = 16.sp)
    }
}

@Composable
private fun SpecRow(spec: SpecItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                    .background(CyberCyanPrimary.copy(alpha = 0.10f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(spec.icon, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(spec.label, color = CyberTextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text(spec.value, color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 19.sp)
            }
        }
    }
}

private fun String?.orDash(): String = this?.takeIf { it.isNotBlank() } ?: "Unavailable"

private fun formatBytes(bytes: Long): String {
    if (bytes < 0L) return "Unavailable"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unit = 0
    while (value >= 1024 && unit < units.lastIndex) {
        value /= 1024
        unit++
    }
    return String.format(Locale.getDefault(), if (unit == 0) "%.0f %s" else "%.1f %s", value, units[unit])
}
