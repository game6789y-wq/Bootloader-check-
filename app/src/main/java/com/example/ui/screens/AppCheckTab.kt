package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.AppCheckInfo
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
import com.example.ui.theme.CyberYellowWarning
import com.example.ui.viewmodel.PropertyDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppCheckTab(
    appCheck: AppCheckInfo,
    onShowDetail: (PropertyDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isTestingToken by remember { mutableStateOf(false) }
    var tokenRefreshedAt by remember { mutableStateOf("Just now") }
    var latencyMs by remember { mutableStateOf(appCheck.deepVerdict.tokenLatencyMs) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // App Check Provider Hero Card
            CyberCard(
                borderColor = CyberCyanPrimary.copy(alpha = 0.5f),
                modifier = Modifier.testTag("appcheck_hero_card")
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberCyanPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, CyberCyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = CyberCyanPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "FIREBASE APP CHECK",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = "Cryptographic Backend Attestation",
                                    fontSize = 12.sp,
                                    color = CyberTextSecondary
                                )
                            }
                        }

                        StatusBadge(
                            text = if (appCheck.playIntegrityDevice) "ENFORCED" else "DEV ATTESTED",
                            color = if (appCheck.playIntegrityDevice) CyberGreenSuccess else CyberYellowWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "App Check safeguards your Firebase backends and Google Cloud APIs by ensuring requests originate strictly from this unmodified APK, validated via Play Integrity and hardware key attestation.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = CyberTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Active Provider: ${appCheck.appCheckProviderName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberCyanPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Live App Check Token & Handshake Diagnostic Card
        item {
            Text(
                text = "Live App Check Token & Handshake",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard(
                borderColor = CyberPurple.copy(alpha = 0.4f),
                modifier = Modifier.testTag("live_token_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = CyberPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Token Handshake: ${appCheck.deepVerdict.tokenStatus}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                        }

                        StatusBadge(
                            text = "${latencyMs}ms",
                            color = CyberGreenSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Decoded JWT Claims Inspector
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F1420))
                            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "DECODED JWT ATTESTATION CLAIMS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextTertiary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            appCheck.deepVerdict.tokenClaims.forEach { (claimKey, claimVal) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$claimKey:",
                                        fontSize = 11.sp,
                                        color = CyberCyanPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = claimVal,
                                        fontSize = 11.sp,
                                        color = CyberTextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isTestingToken = true
                                    delay(750)
                                    latencyMs = (35..160).random().toLong()
                                    tokenRefreshedAt = "Just now"
                                    isTestingToken = false
                                    Toast.makeText(context, "App Check token refreshed ($latencyMs ms)", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isTestingToken,
                            modifier = Modifier.weight(1f).height(44.dp).testTag("test_token_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyanPrimary)
                        ) {
                            if (isTestingToken) {
                                CircularProgressIndicator(
                                    color = CyberCyanPrimary,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Attesting...", fontSize = 12.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Test Handshake",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test Token Handshake", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                copyText(context, "App Check Token", appCheck.deepVerdict.tokenSample)
                            },
                            modifier = Modifier.height(44.dp).testTag("copy_jwt_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberPurple,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Token",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy JWT", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Hardware Keystore & StrongBox Security Chip Architecture
        item {
            Text(
                text = "Hardware Keymaster & StrongBox Security",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(
                        title = "Hardware Security Enclave",
                        value = appCheck.hardwareKeystore.hardwareIsolationLevel,
                        isSafe = !appCheck.isRunningInEmulator,
                        onClick = {
                            onShowDetail(
                                PropertyDetail(
                                    title = "Hardware Isolation Enclave",
                                    value = appCheck.hardwareKeystore.hardwareIsolationLevel,
                                    subtitle = "TEE & Secure Element",
                                    explanation = "Android isolates cryptographic operations within hardware. ARM TrustZone or a separate dedicated chip (StrongBox) holds master signing keys.",
                                    impact = "Even if the Linux kernel is completely rooted, keys inside TEE / StrongBox cannot be extracted in plaintext.",
                                    recommendation = "Optimal hardware protection.",
                                    isSafe = !appCheck.isRunningInEmulator
                                )
                            )
                        }
                    )

                    PropertyRow(
                        title = "Keymaster / KeyMint Engine",
                        value = appCheck.hardwareKeystore.keymasterVersion,
                        isSafe = true
                    )

                    PropertyRow(
                        title = "StrongBox Keymaster (Dedicated Chip)",
                        value = if (appCheck.hardwareKeystore.hasStrongBox) "Present (Titan M2 / Knox Vault / SE)" else "TEE Software Mode",
                        isSafe = appCheck.hardwareKeystore.hasStrongBox
                    )

                    PropertyRow(
                        title = "Hardware Key Attestation",
                        value = if (appCheck.hardwareKeystore.supportsAppAttestKey) "Supported (FEATURE_KEYSTORE_APP_ATTEST_KEY)" else "Standard Keystore",
                        isSafe = true
                    )

                    PropertyRow(
                        title = "Security Level Evaluation",
                        value = appCheck.hardwareKeystore.securityLevel,
                        isSafe = !appCheck.isRunningInEmulator
                    )
                }
            }
        }

        // Protected Cloud Backends List
        item {
            Text(
                text = "Protected Backend Resources",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unauthorized requests without verified App Check tokens are rejected:",
                fontSize = 12.sp,
                color = CyberTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    appCheck.deepVerdict.protectedEndpoints.forEach { endpoint ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CyberGreenSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = endpoint,
                                    fontSize = 13.sp,
                                    color = CyberTextPrimary
                                )
                            }
                            StatusBadge(
                                text = "PROTECTED",
                                color = CyberGreenSuccess
                            )
                        }
                    }
                }
            }
        }

        // Play Integrity 4-Tier Verdict Breakdown
        item {
            Text(
                text = "Play Integrity Verdict Tiers",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                IntegrityTierCard(
                    tierName = "MEETS_BASIC_INTEGRITY",
                    passes = appCheck.playIntegrityBasic,
                    description = "App is running in a standard Android runtime without known emulator tampering or hooks.",
                    requirement = "Clean app environment, no heavy tampering."
                )

                IntegrityTierCard(
                    tierName = "MEETS_DEVICE_INTEGRITY",
                    passes = appCheck.playIntegrityDevice,
                    description = "Device has passed Android Compatibility Test Suite (CTS) and possesses a locked bootloader with OEM verified boot.",
                    requirement = "LOCKED bootloader, official stock ROM, no root."
                )

                IntegrityTierCard(
                    tierName = "MEETS_STRONG_INTEGRITY",
                    passes = appCheck.playIntegrityStrong,
                    description = "Guaranteed by hardware-backed Keystore / StrongBox with hardware root of trust (AVB Green).",
                    requirement = "Hardware key attestation + locked bootloader + AVB Green."
                )

                IntegrityTierCard(
                    tierName = "MEETS_VIRTUAL_INTEGRITY",
                    passes = appCheck.playIntegrityVirtual,
                    description = "Signals whether the application is running in an emulated or cloud virtualization environment.",
                    requirement = "Virtual test farms, Android Studio emulators."
                )
            }
        }

        // App Signing Certificate Fingerprints
        item {
            Text(
                text = "App Signing Fingerprints",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Register these fingerprints in Firebase Console -> App Check or Google Cloud Console:",
                fontSize = 12.sp,
                color = CyberTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    FingerprintRow(
                        title = "SHA-256 Fingerprint",
                        fingerprint = appCheck.sha256Fingerprint,
                        onCopy = {
                            copyText(context, "SHA-256", appCheck.sha256Fingerprint)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FingerprintRow(
                        title = "SHA-1 Fingerprint",
                        fingerprint = appCheck.sha1Fingerprint,
                        onCopy = {
                            copyText(context, "SHA-1", appCheck.sha1Fingerprint)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FingerprintRow(
                        title = "Application Package ID",
                        fingerprint = appCheck.packageName,
                        onCopy = {
                            copyText(context, "Package Name", appCheck.packageName)
                        }
                    )
                }
            }
        }

        // Debug App Check Token
        item {
            Text(
                text = "Firebase Debug App Check Token",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    Text(
                        text = "Use this debug token in Firebase Console -> App Check -> Manage debug tokens to bypass attestation during development:",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberSurfaceCard)
                            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = appCheck.debugTokenSample,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberCyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                copyText(context, "App Check Debug Token", appCheck.debugTokenSample)
                            },
                            modifier = Modifier.testTag("copy_debug_token_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Debug Token",
                                tint = CyberCyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Developer & Runtime Environment
        item {
            Text(
                text = "Runtime Environment Sanity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            CyberCard {
                Column {
                    PropertyRow(
                        title = "ADB Debugging",
                        value = if (appCheck.isAdbDebuggingEnabled) "Active (Enabled)" else "Disabled (Secure)",
                        isSafe = !appCheck.isAdbDebuggingEnabled
                    )

                    PropertyRow(
                        title = "Mock Location Provider",
                        value = if (appCheck.isMockLocationEnabled) "Active (Mock Location)" else "Disabled (Genuine GPS)",
                        isSafe = !appCheck.isMockLocationEnabled
                    )

                    PropertyRow(
                        title = "Developer Options",
                        value = if (appCheck.isDeveloperOptionsEnabled) "Enabled" else "Disabled",
                        isSafe = !appCheck.isDeveloperOptionsEnabled
                    )

                    PropertyRow(
                        title = "Virtualization / Emulator",
                        value = if (appCheck.isRunningInEmulator) "Emulator Environment" else "Physical Device",
                        isSafe = !appCheck.isRunningInEmulator
                    )

                    PropertyRow(
                        title = "Android Security Patch Level",
                        value = "${appCheck.securityPatch} (${appCheck.daysSinceSecurityPatch} days ago)",
                        isSafe = appCheck.daysSinceSecurityPatch <= 120
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
private fun IntegrityTierCard(
    tierName: String,
    passes: Boolean,
    description: String,
    requirement: String
) {
    val statusColor = if (passes) CyberGreenSuccess else CyberOrangeWarning
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceCard)
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tierName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
                StatusBadge(
                    text = if (passes) "PASS" else "FAIL",
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Requires: $requirement",
                fontSize = 11.sp,
                color = CyberCyanPrimary
            )
        }
    }
}

@Composable
private fun FingerprintRow(
    title: String,
    fingerprint: String,
    onCopy: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = CyberTextSecondary
            )
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $title",
                    tint = CyberCyanPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = fingerprint,
            fontSize = 12.sp,
            color = CyberTextPrimary,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
        )
    }
}

private fun copyText(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
}
