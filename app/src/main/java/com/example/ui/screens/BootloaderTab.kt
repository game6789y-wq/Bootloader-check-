package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BootloaderInfo
import com.example.data.model.BootloaderLockStatus
import com.example.data.model.VerifiedBootState
import com.example.ui.components.CyberCard
import com.example.ui.components.PropertyRow
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberGreenSuccess
import com.example.ui.theme.CyberOrangeWarning
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import com.example.ui.viewmodel.PropertyDetail

@Composable
fun BootloaderTab(
    bootloader: BootloaderInfo,
    onShowDetail: (PropertyDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredProps = remember(bootloader.allBootProps, searchQuery) {
        if (searchQuery.isBlank()) {
            bootloader.allBootProps.toList()
        } else {
            bootloader.allBootProps.filter { (key, value) ->
                key.contains(searchQuery, ignoreCase = true) || value.contains(searchQuery, ignoreCase = true)
            }.toList()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Verified Boot State Hero Card
            val vbColor = Color(bootloader.verifiedBootState.colorHex)
            CyberCard(
                borderColor = vbColor.copy(alpha = 0.5f),
                modifier = Modifier.testTag("bootloader_hero_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(vbColor.copy(alpha = 0.15f))
                                    .border(1.dp, vbColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (bootloader.lockStatus == BootloaderLockStatus.LOCKED) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = vbColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "BOOTLOADER ${bootloader.lockStatus.label.uppercase()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = "Android Verified Boot 2.0",
                                    fontSize = 12.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        StatusBadge(
                            text = "AVB: ${bootloader.verifiedBootState.label}",
                            color = vbColor
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = bootloader.verifiedBootState.summary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // Visual Boot Chain Architecture Pipeline
        item {
            Text(
                text = "Hardware Chain of Trust Pipeline",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            BootChainPipeline(bootloader = bootloader)
        }

        // Boot & Partition Properties
        item {
            Text(
                text = "Bootloader & Partitions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(
                        title = "Flash Locked (ro.boot.flash.locked)",
                        value = bootloader.flashLockedRaw,
                        isSafe = bootloader.lockStatus == BootloaderLockStatus.LOCKED,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "Flash Locked (ro.boot.flash.locked)",
                                    value = bootloader.flashLockedRaw,
                                    subtitle = "Hardware Bootloader Lock Flag",
                                    explanation = "When 1, the bootloader prevents flashing custom partitions via fastboot. When 0, the bootloader is unlocked and partitions can be overwritten.",
                                    impact = "Unlocking disables hardware attestation and exposes stored encryption keys to memory dump attacks.",
                                    recommendation = "Lock the bootloader to re-enable device integrity guarantees.",
                                    isSafe = bootloader.lockStatus == BootloaderLockStatus.LOCKED
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "Verified Boot State (ro.boot.verifiedbootstate)",
                        value = bootloader.rawVerifiedBootState,
                        isSafe = bootloader.verifiedBootState == VerifiedBootState.GREEN,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "Verified Boot State",
                                    value = bootloader.rawVerifiedBootState,
                                    subtitle = "Android Verified Boot (AVB) 2.0 Verdict",
                                    explanation = "Green = OEM root key; Yellow = User-enrolled custom key; Orange = Unlocked bootloader without signature check; Red = Verification failed / damaged.",
                                    impact = "Play Integrity, Google Wallet, and Banking apps strictly require GREEN or OEM LOCKED state.",
                                    recommendation = "Flash stock firmware images if you wish to restore GREEN state.",
                                    isSafe = bootloader.verifiedBootState == VerifiedBootState.GREEN
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "vbmeta Device State",
                        value = bootloader.vbmetaDeviceState,
                        isSafe = bootloader.vbmetaDeviceState.equals("locked", ignoreCase = true),
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "vbmeta Device State",
                                    value = bootloader.vbmetaDeviceState,
                                    subtitle = "AVB 2.0 Metadata Partition State",
                                    explanation = "Controls whether the vbmeta hash tree and public keys are verified against the bootloader's hardware root of trust.",
                                    impact = "Unlocked vbmeta allows booting custom kernels, recovery images, and system GSIs.",
                                    recommendation = "Maintain locked vbmeta for device protection.",
                                    isSafe = bootloader.vbmetaDeviceState.equals("locked", ignoreCase = true)
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "A/B Partition Active Slot",
                        value = bootloader.slotSuffix,
                        isSafe = true,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "Seamless A/B Partition Slot",
                                    value = bootloader.slotSuffix,
                                    subtitle = "Seamless Update Architecture",
                                    explanation = "Android devices use dual partition slots (_a and _b) to apply seamless system updates in the background without downtime.",
                                    impact = "If an OTA update fails verification, the bootloader automatically rolls back to the other slot.",
                                    recommendation = "Normal operating state.",
                                    isSafe = true
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "OEM Unlock Allowed (sys.oem_unlock_allowed)",
                        value = when (bootloader.oemUnlockAllowed) {
                            true -> "Enabled (1)"
                            false -> "Disabled (0)"
                            null -> "Not exposed / Protected"
                        },
                        isSafe = bootloader.oemUnlockAllowed != true,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "OEM Unlock Allowed Switch",
                                    value = bootloader.oemUnlockAllowed?.toString() ?: "N/A",
                                    subtitle = "Developer Options Toggle",
                                    explanation = "Controls whether the user is permitted to run 'fastboot flashing unlock' from a connected computer.",
                                    impact = "When enabled, someone with physical access to your phone can wipe it and flash custom firmware.",
                                    recommendation = "Turn off 'OEM unlocking' in Developer Options when not modifying firmware.",
                                    isSafe = bootloader.oemUnlockAllowed != true
                                )
                            )
                        }
                    )
                }
            }
        }

        // Kernel & Treble Architecture
        item {
            Text(
                text = "Kernel & Treble Architecture",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(
                        title = "Linux Kernel Version",
                        value = bootloader.kernelVersion,
                        isSafe = true
                    )

                    PropertyRow(
                        title = "Project Treble Compliance",
                        value = if (bootloader.trebleCompliant) "Compliant (Decoupled HAL/Framework)" else "Legacy Monolithic",
                        isSafe = bootloader.trebleCompliant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "KERNEL BOOT COMMANDLINE (/proc/cmdline):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextTertiary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1420))
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bootloader.kernelCmdline,
                            fontSize = 11.sp,
                            color = CyberCyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Kernel Cmdline", bootloader.kernelCmdline)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Kernel cmdline copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Cmdline",
                                tint = CyberCyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hardware & Platform Info
        item {
            Text(
                text = "Hardware & Platform Identity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(title = "Bootloader Version", value = bootloader.bootloaderVersion)
                    PropertyRow(title = "Board / Platform", value = bootloader.board)
                    PropertyRow(title = "Hardware / SoC", value = bootloader.socModel)
                    PropertyRow(title = "Device Model", value = bootloader.device)
                    PropertyRow(title = "Baseband / Radio", value = bootloader.radioVersion)
                }
            }
        }

        // Raw Properties Inspector
        item {
            Column {
                Text(
                    text = "Raw System Properties Inspector",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search property name or value...", color = CyberTextTertiary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CyberCyanPrimary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("props_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyanPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        items(filteredProps) { (key, value) ->
            CyberCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = key,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberCyanPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BootChainPipeline(bootloader: BootloaderInfo) {
    val vbState = bootloader.verifiedBootState
    val stages = listOf(
        Pair("1. Boot ROM", "Primary Trust (eFuse)"),
        Pair("2. Bootloader", if (bootloader.lockStatus == BootloaderLockStatus.LOCKED) "Locked" else "Unlocked"),
        Pair("3. AVB Check", vbState.label),
        Pair("4. Linux Kernel", "dm-verity active")
    )

    CyberCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stages.forEachIndexed { index, (stage, status) ->
                val stageColor = when {
                    stage.contains("Bootloader") && bootloader.lockStatus != BootloaderLockStatus.LOCKED -> CyberOrangeWarning
                    stage.contains("AVB") && vbState != VerifiedBootState.GREEN -> Color(vbState.colorHex)
                    else -> CyberGreenSuccess
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(stageColor.copy(alpha = 0.2f))
                            .border(1.dp, stageColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = stageColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stage.substringAfter(". "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = status,
                        fontSize = 9.sp,
                        color = stageColor
                    )
                }

                if (index < stages.size - 1) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = CyberBorder,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
