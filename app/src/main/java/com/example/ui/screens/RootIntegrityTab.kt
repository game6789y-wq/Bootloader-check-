package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RootIntegrityInfo
import com.example.ui.components.CyberCard
import com.example.ui.components.PropertyRow
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberGreenSuccess
import com.example.ui.theme.CyberOrangeWarning
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import com.example.ui.viewmodel.PropertyDetail

@Composable
fun RootIntegrityTab(
    root: RootIntegrityInfo,
    onShowDetail: (PropertyDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Status Card
            val isClean = !root.isRooted
            val heroColor = if (isClean) CyberGreenSuccess else CyberRedAlert

            CyberCard(
                borderColor = heroColor.copy(alpha = 0.5f),
                modifier = Modifier.testTag("root_hero_card")
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
                                    .background(heroColor.copy(alpha = 0.15f))
                                    .border(1.dp, heroColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isClean) Icons.Default.CheckCircle else Icons.Default.Dangerous,
                                    contentDescription = null,
                                    tint = heroColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isClean) "NO ROOT PRIVILEGES DETECTED" else "ROOT PRIVILEGES ACTIVE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = "System Sandbox & Privilege Isolation",
                                    fontSize = 12.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        StatusBadge(
                            text = if (isClean) "CLEAN" else "ROOTED",
                            color = heroColor
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isClean) {
                            "Android application sandbox is strictly enforced. No unauthorized superuser binaries, Zygisk injection modules, or method hooking frameworks were detected in system paths."
                        } else {
                            "Device security is compromised by elevated root privileges or memory hooking. Malware or malicious apps can bypass permission controls and access private app data."
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // Privilege & Binary Inspection
        item {
            Text(
                text = "Privilege Escalation & Binaries",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(
                        title = "SU Binary Detection",
                        value = if (root.suBinaryFound) "FOUND (${root.suPathsDetected.size} locations)" else "Clean (Not found)",
                        isSafe = !root.suBinaryFound,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "Superuser (su) Binary",
                                    value = if (root.suBinaryFound) root.suPathsDetected.joinToString() else "Not Found",
                                    subtitle = "Binary Privilege Checker",
                                    explanation = "Scans 13 common UNIX locations for the 'su' executable (/system/bin, /system/xbin, /sbin, /su/bin, etc.).",
                                    impact = "The su binary allows processes to run as UID 0 (root), granting unrestricted read/write access to all device storage and hardware.",
                                    recommendation = if (root.suBinaryFound) "Uninstall root binaries to restore security." else "System binaries are intact.",
                                    isSafe = !root.suBinaryFound
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "Zygisk / Magisk In-Memory Injection",
                        value = if (root.zygiskDetected) "DETECTED in /proc/self/maps" else "Clean (No injection)",
                        isSafe = !root.zygiskDetected
                    )

                    PropertyRow(
                        title = "Frida Dynamic Instrumentation Hook",
                        value = if (root.fridaDetected) "FRIDA ACTIVE / PORT OPEN" else "Clean (No Frida server)",
                        isSafe = !root.fridaDetected
                    )

                    PropertyRow(
                        title = "Busybox Toolset",
                        value = if (root.busyboxFound) "Installed" else "Not detected",
                        isSafe = !root.busyboxFound
                    )

                    PropertyRow(
                        title = "Root Management Packages",
                        value = if (root.rootAppsFound.isNotEmpty()) root.rootAppsFound.joinToString() else "None (Magisk/KSU/APatch clean)",
                        isSafe = root.rootAppsFound.isEmpty()
                    )

                    PropertyRow(
                        title = "Hooking Frameworks (Xposed / LSPosed)",
                        value = if (root.xposedFrameworkDetected) "HOOK DETECTED" else "Clean (No hooks)",
                        isSafe = !root.xposedFrameworkDetected
                    )
                }
            }
        }

        // Kernel & Storage Security
        item {
            Text(
                text = "Kernel & OS Protection",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(
                        title = "SELinux Mode",
                        value = root.selinuxMode,
                        isSafe = root.isSelinuxEnforcing,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "Security-Enhanced Linux (SELinux)",
                                    value = root.selinuxMode,
                                    subtitle = "Kernel Mandatory Access Control",
                                    explanation = "Enforcing means the Linux kernel strictly blocks unauthorized process actions. Permissive only logs violations without enforcing blocks.",
                                    impact = "Permissive SELinux allows zero-day privilege escalations to compromise the entire system.",
                                    recommendation = "Ensure SELinux is set to Enforcing in release firmware.",
                                    isSafe = root.isSelinuxEnforcing
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "Storage Encryption",
                        value = root.storageEncryptionState,
                        isSafe = root.storageEncryptionState.contains("Encrypted")
                    )

                    PropertyRow(
                        title = "Build Signing Keys (Build.TAGS)",
                        value = if (root.testKeysPresent) "test-keys (Insecure)" else "release-keys (Official OEM)",
                        isSafe = !root.testKeysPresent
                    )

                    PropertyRow(
                        title = "System Partition Mount Status",
                        value = if (root.rwSystemDetected) "Read-Write (${root.dangerousMounts.size} rw mounts)" else "Read-Only (ro) Secured",
                        isSafe = !root.rwSystemDetected
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
