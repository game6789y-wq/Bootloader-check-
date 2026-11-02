package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BootloaderLockStatus
import com.example.data.model.IssueSeverity
import com.example.data.model.SecurityIssue
import com.example.data.model.SecurityReport
import com.example.data.model.VerifiedBootState
import com.example.ui.components.CyberCard
import com.example.ui.components.HeroVaultHeader
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
import com.example.ui.theme.CyberYellowWarning
import com.example.ui.viewmodel.PropertyDetail

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OverviewTab(
    report: SecurityReport,
    onShowDetail: (PropertyDetail) -> Unit,
    onNavigateTab: (Int) -> Unit,
    exportText: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf<IssueSeverity?>(null) }

    val filteredIssues = remember(report.issues, selectedFilter) {
        if (selectedFilter == null) report.issues else report.issues.filter { it.severity == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Vault Header with Cyber Visualizer
            HeroVaultHeader(
                score = report.overallScore,
                gradeTitle = report.grade.title,
                gradeColor = Color(report.grade.colorHex),
                modifier = Modifier.testTag("hero_vault_header")
            )
        }

        // 4-Card Quick Status Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Bootloader
                    val isBootloaderLocked = report.bootloader.lockStatus == BootloaderLockStatus.LOCKED
                    QuickStatusCard(
                        title = "Bootloader",
                        value = if (isBootloaderLocked) "LOCKED" else report.bootloader.lockStatus.label.substringBefore(" ").uppercase(),
                        isSuccess = isBootloaderLocked,
                        icon = if (isBootloaderLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        accentColor = if (isBootloaderLocked) CyberGreenSuccess else CyberOrangeWarning,
                        onClick = { onNavigateTab(1) },
                        modifier = Modifier.weight(1f).testTag("quick_card_bootloader")
                    )

                    // Card 2: Verified Boot
                    val isVbGreen = report.bootloader.verifiedBootState == VerifiedBootState.GREEN
                    QuickStatusCard(
                        title = "AVB 2.0 State",
                        value = report.bootloader.verifiedBootState.label,
                        isSuccess = isVbGreen,
                        icon = Icons.Default.Shield,
                        accentColor = Color(report.bootloader.verifiedBootState.colorHex),
                        onClick = { onNavigateTab(1) },
                        modifier = Modifier.weight(1f).testTag("quick_card_avb")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 3: Root Integrity
                    val isClean = !report.root.isRooted
                    QuickStatusCard(
                        title = "Root & SU Binaries",
                        value = if (isClean) "SECURE" else "DETECTED",
                        isSuccess = isClean,
                        icon = Icons.Default.Security,
                        accentColor = if (isClean) CyberGreenSuccess else CyberRedAlert,
                        onClick = { onNavigateTab(2) },
                        modifier = Modifier.weight(1f).testTag("quick_card_root")
                    )

                    // Card 4: App Check / Hardware Keystore
                    val isKeystoreStrong = report.appCheck.hardwareKeystore.hasStrongBox || report.appCheck.playIntegrityDevice
                    QuickStatusCard(
                        title = "App Check & Keymaster",
                        value = if (report.appCheck.hardwareKeystore.hasStrongBox) "STRONGBOX" else "PLAY READY",
                        isSuccess = isKeystoreStrong,
                        icon = Icons.Default.VpnKey,
                        accentColor = if (isKeystoreStrong) CyberCyanPrimary else CyberYellowWarning,
                        onClick = { onNavigateTab(3) },
                        modifier = Modifier.weight(1f).testTag("quick_card_appcheck")
                    )
                }
            }
        }

        // Action Buttons: Copy & Share Report
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("BootCheck Audit Report", exportText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Full report copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(48.dp).testTag("copy_report_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyanPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Report", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, exportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Security Report"))
                    },
                    modifier = Modifier.weight(1f).height(48.dp).testTag("share_report_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyanPrimary,
                        contentColor = Color(0xFF00363D)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Audit", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Findings Header & Filter Chips
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Deep Audit Findings (${report.issues.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Tap finding for guide",
                        fontSize = 12.sp,
                        color = CyberTextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All (${report.issues.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyanPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyanPrimary,
                            labelColor = CyberTextSecondary
                        )
                    )
                    val criticalCount = report.issues.count { it.severity == IssueSeverity.CRITICAL }
                    if (criticalCount > 0) {
                        FilterChip(
                            selected = selectedFilter == IssueSeverity.CRITICAL,
                            onClick = { selectedFilter = if (selectedFilter == IssueSeverity.CRITICAL) null else IssueSeverity.CRITICAL },
                            label = { Text("Critical ($criticalCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberRedAlert.copy(alpha = 0.2f),
                                selectedLabelColor = CyberRedAlert,
                                labelColor = CyberTextSecondary
                            )
                        )
                    }
                    val warningCount = report.issues.count { it.severity == IssueSeverity.WARNING }
                    if (warningCount > 0) {
                        FilterChip(
                            selected = selectedFilter == IssueSeverity.WARNING,
                            onClick = { selectedFilter = if (selectedFilter == IssueSeverity.WARNING) null else IssueSeverity.WARNING },
                            label = { Text("Warning ($warningCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberOrangeWarning.copy(alpha = 0.2f),
                                selectedLabelColor = CyberOrangeWarning,
                                labelColor = CyberTextSecondary
                            )
                        )
                    }
                    val passedCount = report.issues.count { it.severity == IssueSeverity.PASSED }
                    FilterChip(
                        selected = selectedFilter == IssueSeverity.PASSED,
                        onClick = { selectedFilter = if (selectedFilter == IssueSeverity.PASSED) null else IssueSeverity.PASSED },
                        label = { Text("Passed ($passedCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberGreenSuccess.copy(alpha = 0.2f),
                            selectedLabelColor = CyberGreenSuccess,
                            labelColor = CyberTextSecondary
                        )
                    )
                }
            }
        }

        // Findings List
        items(filteredIssues) { issue ->
            FindingCard(
                issue = issue,
                onClick = {
                    onShowDetail(
                        PropertyDetail(
                            title = issue.title,
                            value = issue.severity.name,
                            subtitle = issue.category,
                            explanation = issue.description,
                            impact = if (issue.severity == IssueSeverity.PASSED) {
                                "Maintains security guarantees, cryptographic attestation, and data isolation."
                            } else {
                                "Compromises hardware root of trust, allows unauthorized memory access, or breaks anti-tamper protocols."
                            },
                            recommendation = issue.recommendation,
                            isSafe = issue.severity == IssueSeverity.PASSED
                        )
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickStatusCard(
    title: String,
    value: String,
    isSuccess: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurfaceCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSuccess) CyberGreenSuccess else CyberOrangeWarning)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                color = CyberTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
        }
    }
}

@Composable
private fun FindingCard(
    issue: SecurityIssue,
    onClick: () -> Unit
) {
    val (color, icon) = when (issue.severity) {
        IssueSeverity.CRITICAL -> Pair(CyberRedAlert, Icons.Default.Error)
        IssueSeverity.WARNING -> Pair(CyberOrangeWarning, Icons.Default.Warning)
        IssueSeverity.INFO -> Pair(CyberCyanPrimary, Icons.Default.Security)
        IssueSeverity.PASSED -> Pair(CyberGreenSuccess, Icons.Default.CheckCircle)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurfaceCard)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = issue.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    StatusBadge(
                        text = issue.category,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = issue.description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = CyberTextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tip: ${issue.recommendation}",
                    fontSize = 11.sp,
                    color = CyberTextTertiary
                )
            }
        }
    }
}
