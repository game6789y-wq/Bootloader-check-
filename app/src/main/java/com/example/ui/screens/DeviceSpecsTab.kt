package com.example.ui.screens

import android.app.ActivityManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import java.util.Locale

@Composable
fun DeviceSpecsTab(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
    val dataStats = StatFs(context.filesDir.absolutePath)
    val totalStorage = dataStats.totalBytes
    val freeStorage = dataStats.availableBytes
    val usedStorage = (totalStorage - freeStorage).coerceAtLeast(0L)

    val specs = listOf(
        Triple("Manufacturer", Build.MANUFACTURER.orDash(), Icons.Default.PhoneAndroid),
        Triple("Device model", Build.MODEL.orDash(), Icons.Default.PhoneAndroid),
        Triple("Device codename", Build.DEVICE.orDash(), Icons.Default.Tune),
        Triple("Product", Build.PRODUCT.orDash(), Icons.Default.Android),
        Triple("Android version", Build.VERSION.RELEASE.orDash(), Icons.Default.Android),
        Triple("API level", Build.VERSION.SDK_INT.toString(), Icons.Default.Tune),
        Triple("Build ID", Build.DISPLAY.orDash(), Icons.Default.Tune),
        Triple("Security patch", Build.VERSION.SECURITY_PATCH.orDash(), Icons.Default.Tune),
        Triple("CPU architecture", Build.SUPPORTED_ABIS.firstOrNull().orDash(), Icons.Default.Memory),
        Triple("CPU cores", Runtime.getRuntime().availableProcessors().toString(), Icons.Default.Memory),
        Triple("Total RAM", formatBytes(memoryInfo.totalMem), Icons.Default.Memory),
        Triple("Available RAM", formatBytes(memoryInfo.availMem), Icons.Default.Memory),
        Triple("App data storage", formatBytes(totalStorage), Icons.Default.Storage),
        Triple("Available app storage", formatBytes(freeStorage), Icons.Default.Storage),
        Triple("Used app storage", formatBytes(usedStorage), Icons.Default.Storage),
        Triple("Bootloader", Build.BOOTLOADER.orDash(), Icons.Default.Tune),
        Triple("Hardware", Build.HARDWARE.orDash(), Icons.Default.Tune),
        Triple("Board", Build.BOARD.orDash(), Icons.Default.Tune)
    )

    LazyColumn(
        modifier = modifier.fillMaxSize().background(CyberDarkBg).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(6.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, null, tint = CyberCyanPrimary, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.size(10.dp))
                    Text("Device specifications", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                }
                Spacer(Modifier.height(8.dp))
                Text("Hardware and Android details reported by this device.", color = CyberTextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniStat("Android", Build.VERSION.RELEASE.orDash(), Modifier.weight(1f))
                    MiniStat("CPU cores", Runtime.getRuntime().availableProcessors().toString(), Modifier.weight(1f))
                    MiniStat("RAM", formatBytes(memoryInfo.totalMem), Modifier.weight(1f))
                }
            }
        }
        item {
            Text("SYSTEM INFORMATION", color = CyberCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        }
        items(specs.size) { index ->
            val (label, value, icon) = specs[index]
            SpecRow(label = label, value = value, icon = icon)
        }
        item {
            Text(
                "Note: storage figures refer to the app's data filesystem and may not equal the phone's entire physical storage. Some device fields are vendor-specific.",
                color = CyberTextTertiary,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(CyberSurfaceVariant, RoundedCornerShape(14.dp)).padding(10.dp)
    ) {
        Text(label, color = CyberTextTertiary, fontSize = 10.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = CyberTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
    }
}

@Composable
private fun SpecRow(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = CyberTextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(3.dp))
                Text(value, color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
