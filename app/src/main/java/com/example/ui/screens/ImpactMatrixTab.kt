package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ImpactMatrixItem
import com.example.ui.components.CyberCard
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
fun ImpactMatrixTab(
    matrix: List<ImpactMatrixItem>,
    onShowDetail: (PropertyDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    var simulateUnlocked by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Interactive Simulator Controller
            CyberCard(
                borderColor = if (simulateUnlocked) CyberOrangeWarning.copy(alpha = 0.5f) else CyberCyanPrimary.copy(alpha = 0.5f),
                modifier = Modifier.testTag("simulator_card")
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
                                    .background((if (simulateUnlocked) CyberOrangeWarning else CyberCyanPrimary).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (simulateUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (simulateUnlocked) CyberOrangeWarning else CyberCyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "BOOTLOADER SIMULATOR",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = if (simulateUnlocked) "Simulating Unlocked State" else "Simulating Stock Locked State",
                                    fontSize = 12.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = simulateUnlocked,
                            onCheckedChange = { simulateUnlocked = it },
                            modifier = Modifier.testTag("simulate_unlocked_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberOrangeWarning,
                                checkedTrackColor = CyberOrangeWarning.copy(alpha = 0.3f),
                                uncheckedThumbColor = CyberCyanPrimary,
                                uncheckedTrackColor = CyberCyanPrimary.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (simulateUnlocked) {
                            "⚠️ Notice: The table below shows the catastrophic impact on banking, DRM, Knox, and Play Integrity when bootloader is unlocked."
                        } else {
                            "✅ Standard: Devices with a locked bootloader maintain verified boot chains, hardware encryption, and pass all Play Integrity tiers."
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = if (simulateUnlocked) CyberOrangeWarning else CyberTextSecondary
                    )
                }
            }
        }

        item {
            Text(
                text = "Services & Features Impact Matrix",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap any service to view the exact cryptographic failure point:",
                fontSize = 12.sp,
                color = CyberTextSecondary
            )
        }

        items(matrix) { item ->
            val isBroken = simulateUnlocked && item.isBrokenWhenUnlocked
            val currentStatus = if (simulateUnlocked) item.statusUnlocked else item.statusLocked
            val statusColor = if (isBroken) CyberRedAlert else CyberGreenSuccess

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberSurfaceCard)
                    .border(1.dp, if (isBroken) CyberRedAlert.copy(alpha = 0.4f) else CyberBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        onShowDetail(
                            PropertyDetail(
                                title = item.title,
                                value = if (simulateUnlocked) "FAIL / RESTRICTED" else "PASS / OPTIMAL",
                                subtitle = item.category,
                                explanation = item.technicalReason,
                                impact = if (item.isBrokenWhenUnlocked) {
                                    "When bootloader is unlocked, key attestation tokens are rejected by Google servers and OEM hardware eFuses are tripped."
                                } else {
                                    "Safe under normal locked boot conditions."
                                },
                                recommendation = "Keep bootloader locked unless building custom AOSP kernels.",
                                isSafe = !isBroken
                            )
                        )
                    }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        StatusBadge(
                            text = item.category,
                            color = CyberCyanPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isBroken) Icons.Default.Dangerous else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Status: $currentStatus",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Details",
                            tint = CyberTextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.technicalReason,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        item {
            CyberCard {
                Column {
                    Text(
                        text = "Why Do Services Block Unlocked Bootloaders?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyanPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "An unlocked bootloader breaks the chain of trust established in hardware. An attacker with physical access or malicious root apps can replace the kernel, read decrypted user data, or inject hooks into process memory to steal session cookies, payment tokens, and biometrics. Play Integrity and Widevine L1 ensure code is executing in a verified OEM environment.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
