package com.example.data.detector

import android.content.Context
import com.example.data.model.BootloaderInfo
import com.example.data.model.BootloaderLockStatus
import com.example.data.model.ImpactMatrixItem
import com.example.data.model.IssueSeverity
import com.example.data.model.RootIntegrityInfo
import com.example.data.model.SecurityGrade
import com.example.data.model.SecurityIssue
import com.example.data.model.SecurityReport
import com.example.data.model.VerifiedBootState

object SecurityAuditEngine {

    fun runAudit(context: Context): SecurityReport {
        val bootloader = BootloaderDetector.detect(context)
        val root = RootDetector.detect(context)
        val appCheck = AppCheckDetector.detect(
            context = context,
            bootloaderStatus = bootloader.lockStatus,
            verifiedBootState = bootloader.verifiedBootState,
            isRooted = root.isRooted
        )

        val issues = mutableListOf<SecurityIssue>()
        var score = 100

        // 1. Bootloader check
        when (bootloader.lockStatus) {
            BootloaderLockStatus.UNLOCKED -> {
                score -= 35
                issues.add(
                    SecurityIssue(
                        title = "Bootloader Unlocked",
                        description = "The bootloader allows execution of unauthorized recovery, kernel, or partition images without signature validation.",
                        severity = IssueSeverity.CRITICAL,
                        recommendation = "Lock bootloader via fastboot ('fastboot flashing lock') if you are not developing custom ROMs.",
                        category = "Boot Security"
                    )
                )
            }
            BootloaderLockStatus.TAMPERED -> {
                score -= 25
                issues.add(
                    SecurityIssue(
                        title = "Custom Key / Yellow Boot State",
                        description = "Device has custom root of trust keys enrolled. Stock OEM signature verification is bypassed.",
                        severity = IssueSeverity.WARNING,
                        recommendation = "Revert to OEM root of trust if this device is intended for production enterprise use.",
                        category = "Boot Security"
                    )
                )
            }
            BootloaderLockStatus.LOCKED -> {
                issues.add(
                    SecurityIssue(
                        title = "Bootloader Locked & Secured",
                        description = "Boot chain is cryptographically locked with hardware-enforced root of trust.",
                        severity = IssueSeverity.PASSED,
                        recommendation = "Maintain current locked state to preserve Play Integrity and hardware DRM.",
                        category = "Boot Security"
                    )
                )
            }
            BootloaderLockStatus.UNKNOWN -> {
                issues.add(
                    SecurityIssue(
                        title = "Bootloader Status Hidden",
                        description = "OEM or virtualization layer does not expose ro.boot.flash.locked directly to user-space.",
                        severity = IssueSeverity.INFO,
                        recommendation = "Verify bootloader status in Bootloader/Fastboot mode during boot.",
                        category = "Boot Security"
                    )
                )
            }
        }

        // 2. Verified Boot State
        when (bootloader.verifiedBootState) {
            VerifiedBootState.GREEN -> {
                issues.add(
                    SecurityIssue(
                        title = "Android Verified Boot: GREEN",
                        description = "Device boot partitions strictly match the OEM public key stored in hardware eFuses.",
                        severity = IssueSeverity.PASSED,
                        recommendation = "Device is in maximum integrity mode.",
                        category = "Verified Boot"
                    )
                )
            }
            VerifiedBootState.YELLOW -> {}
            VerifiedBootState.ORANGE -> {
                score -= 10
                issues.add(
                    SecurityIssue(
                        title = "AVB State: ORANGE (Tamper Warning)",
                        description = "Kernel and system images are loaded without verified cryptographic proof.",
                        severity = IssueSeverity.CRITICAL,
                        recommendation = "SafetyNet, Play Integrity Strong, and Google Wallet are blocked.",
                        category = "Verified Boot"
                    )
                )
            }
            VerifiedBootState.RED -> {
                score -= 50
                issues.add(
                    SecurityIssue(
                        title = "AVB State: RED (Corrupted Device)",
                        description = "Partition verification failed or dm-verity tree corrupted. Device is unbootable or compromised.",
                        severity = IssueSeverity.CRITICAL,
                        recommendation = "Reflash stock factory images immediately.",
                        category = "Verified Boot"
                    )
                )
            }
            VerifiedBootState.UNKNOWN -> {}
        }

        // 3. Hardware Keymaster & StrongBox
        if (appCheck.hardwareKeystore.hasStrongBox) {
            issues.add(
                SecurityIssue(
                    title = "Dedicated StrongBox Keymaster Active",
                    description = "Cryptographic keys are physically isolated in dedicated hardware (Titan M / Knox Vault / Secure Element).",
                    severity = IssueSeverity.PASSED,
                    recommendation = "Protected against side-channel and physical bus snooping attacks.",
                    category = "Hardware Security"
                )
            )
        } else {
            issues.add(
                SecurityIssue(
                    title = "Hardware Keystore: ${appCheck.hardwareKeystore.securityLevel}",
                    description = "Keys managed via ${appCheck.hardwareKeystore.hardwareIsolationLevel}.",
                    severity = if (appCheck.isRunningInEmulator) IssueSeverity.WARNING else IssueSeverity.PASSED,
                    recommendation = if (appCheck.isRunningInEmulator) "Test on physical device for hardware attestation." else "Standard TEE security level.",
                    category = "Hardware Security"
                )
            )
        }

        // 4. Root & Tamper
        if (root.suBinaryFound) {
            score -= 35
            issues.add(
                SecurityIssue(
                    title = "Root Binary (su) Detected",
                    description = "Superuser binaries detected in system execution paths: ${root.suPathsDetected.joinToString()}",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Uninstall root management tools (Magisk/KernelSU) to restore app sandbox isolation.",
                    category = "Root & Privilege"
                )
            )
        } else {
            issues.add(
                SecurityIssue(
                    title = "No SU Binaries Found",
                    description = "No superuser binaries found in common execution directories.",
                    severity = IssueSeverity.PASSED,
                    recommendation = "Application sandboxing is intact.",
                    category = "Root & Privilege"
                )
            )
        }

        if (root.zygiskDetected) {
            score -= 25
            issues.add(
                SecurityIssue(
                    title = "Zygisk / Magisk Memory Artifacts Detected",
                    description = "Active code injection module found in /proc/self/maps process memory segments.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Disable Zygisk injection modules.",
                    category = "Tamper Detection"
                )
            )
        }

        if (root.fridaDetected) {
            score -= 30
            issues.add(
                SecurityIssue(
                    title = "Frida Dynamic Instrumentation Hook Detected",
                    description = "Frida debugger server thread or telemetry socket detected in runtime memory.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Terminate active Frida server / gadget processes.",
                    category = "Tamper Detection"
                )
            )
        }

        if (root.xposedFrameworkDetected) {
            score -= 25
            issues.add(
                SecurityIssue(
                    title = "Hooking Framework (Xposed/LSPosed) Present",
                    description = "Runtime method interception framework detected in process memory.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Disable active modules or LSPosed framework.",
                    category = "Tamper Detection"
                )
            )
        }

        // 5. SELinux
        if (!root.isSelinuxEnforcing) {
            score -= 20
            issues.add(
                SecurityIssue(
                    title = "SELinux Permissive / Disabled",
                    description = "Mandatory Access Control is inactive. Vulnerabilities can access unauthorized system contexts.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Set SELinux mode back to Enforcing in kernel command line.",
                    category = "Kernel Security"
                )
            )
        } else {
            issues.add(
                SecurityIssue(
                    title = "SELinux Enforcing",
                    description = "Kernel Mandatory Access Control is enforcing strict process containment.",
                    severity = IssueSeverity.PASSED,
                    recommendation = "Optimal kernel security configuration.",
                    category = "Kernel Security"
                )
            )
        }

        // 6. Security Patch Age
        if (appCheck.daysSinceSecurityPatch > 120) {
            score -= 10
            issues.add(
                SecurityIssue(
                    title = "Android Security Patch Outdated",
                    description = "Current patch level is ${appCheck.securityPatch} (${appCheck.daysSinceSecurityPatch} days old).",
                    severity = IssueSeverity.WARNING,
                    recommendation = "Install latest system OTA update to patch known CVE vulnerabilities.",
                    category = "OS Integrity"
                )
            )
        } else {
            issues.add(
                SecurityIssue(
                    title = "Recent Android Security Patch",
                    description = "Patch level: ${appCheck.securityPatch} (${appCheck.daysSinceSecurityPatch} days ago).",
                    severity = IssueSeverity.PASSED,
                    recommendation = "Firmware is up-to-date with Google security bulletins.",
                    category = "OS Integrity"
                )
            )
        }

        // 7. Developer options & ADB
        if (appCheck.isAdbDebuggingEnabled) {
            score -= 5
            issues.add(
                SecurityIssue(
                    title = "USB / ADB Debugging Enabled",
                    description = "Device accepts remote debug commands when plugged into an authorized computer.",
                    severity = IssueSeverity.INFO,
                    recommendation = "Turn off USB Debugging in Developer Options when not testing apps.",
                    category = "Environment"
                )
            )
        }

        val finalScore = score.coerceIn(0, 100)
        val grade = when {
            finalScore >= 85 -> SecurityGrade.SECURE
            finalScore >= 65 -> SecurityGrade.CAUTION
            finalScore >= 40 -> SecurityGrade.AT_RISK
            else -> SecurityGrade.COMPROMISED
        }

        return SecurityReport(
            timestamp = System.currentTimeMillis(),
            overallScore = finalScore,
            grade = grade,
            bootloader = bootloader,
            root = root,
            appCheck = appCheck,
            issues = issues
        )
    }

    fun getImpactMatrix(): List<ImpactMatrixItem> {
        return listOf(
            ImpactMatrixItem(
                title = "Google Wallet & NFC Tap-to-Pay",
                category = "Payments & Financial",
                statusLocked = "Functional",
                statusUnlocked = "Blocked / Fails",
                isBrokenWhenUnlocked = true,
                technicalReason = "Google Wallet verifies Play Integrity (MEETS_DEVICE_INTEGRITY). Unlocked bootloader fails hardware attestation."
            ),
            ImpactMatrixItem(
                title = "Banking & Fintech Apps",
                category = "Security Sensitive",
                statusLocked = "Full Access",
                statusUnlocked = "Restricted or Blocked",
                isBrokenWhenUnlocked = true,
                technicalReason = "Leading banking apps (Chase, Barclays, Revolut) query Verified Boot and block login on orange boot state."
            ),
            ImpactMatrixItem(
                title = "Widevine L1 DRM (Netflix, Disney+)",
                category = "Media Streaming",
                statusLocked = "Full HD / 4K HDR",
                statusUnlocked = "Downgraded to L3 (SD 480p)",
                isBrokenWhenUnlocked = true,
                technicalReason = "Qualcomm/MediaTek TEE revokes hardware key provisioning when bootloader lock flags are cleared."
            ),
            ImpactMatrixItem(
                title = "Samsung Knox (eFuse 0x1)",
                category = "OEM Hardware Security",
                statusLocked = "Warranty Valid (0x0)",
                statusUnlocked = "Permanently Blown (0x1)",
                isBrokenWhenUnlocked = true,
                technicalReason = "On Samsung devices, unlocking the bootloader triggers physical eFuse burnout, permanently disabling Knox, Secure Folder, and Samsung Pay."
            ),
            ImpactMatrixItem(
                title = "Enterprise MDM & Work Profile",
                category = "Enterprise & Corporate",
                statusLocked = "Compliant",
                statusUnlocked = "Non-Compliant / Wiped",
                isBrokenWhenUnlocked = true,
                technicalReason = "Microsoft Intune, Google Workspace, and MobileIron enforce device health attestation before granting corporate access."
            ),
            ImpactMatrixItem(
                title = "Multiplayer Mobile Games (Niantic, Hoyoverse)",
                category = "Anti-Cheat Protection",
                statusLocked = "Full Playability",
                statusUnlocked = "Error Code / Crash",
                isBrokenWhenUnlocked = true,
                technicalReason = "Anti-cheat engines audit /proc/cmdline and AVB state to prevent memory injection and botting."
            ),
            ImpactMatrixItem(
                title = "Firebase App Check Backend Protection",
                category = "Cloud & API Security",
                statusLocked = "Valid Tokens Issued",
                statusUnlocked = "App Check Quota Denied",
                isBrokenWhenUnlocked = true,
                technicalReason = "Play Integrity attestation provider fails to mint valid app check JWTs on untrusted boot states."
            )
        )
    }

    fun formatReportMarkdown(report: SecurityReport): String {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date(report.timestamp))
        return buildString {
            appendLine("# BootCheck Security & Bootloader Audit Report")
            appendLine("Generated: $date")
            appendLine("Score: ${report.overallScore}/100 (${report.grade.title})")
            appendLine()
            appendLine("## 1. Bootloader & AVB Status")
            appendLine("- Lock Status: ${report.bootloader.lockStatus.label}")
            appendLine("- Verified Boot State: ${report.bootloader.verifiedBootState.label} (${report.bootloader.verifiedBootState.summary})")
            appendLine("- Flash Locked Prop: ${report.bootloader.flashLockedRaw}")
            appendLine("- AVB Version: ${report.bootloader.avbVersion}")
            appendLine("- vbmeta Device State: ${report.bootloader.vbmetaDeviceState}")
            appendLine("- Bootloader Version: ${report.bootloader.bootloaderVersion}")
            appendLine("- Kernel Version: ${report.bootloader.kernelVersion}")
            appendLine("- Hardware Board: ${report.bootloader.board} / ${report.bootloader.hardware}")
            appendLine("- Slot Suffix: ${report.bootloader.slotSuffix}")
            appendLine("- Treble Compliant: ${report.bootloader.trebleCompliant}")
            appendLine()
            appendLine("## 2. Root & Tamper Integrity")
            appendLine("- Root Detected: ${if (report.root.isRooted) "YES (CRITICAL)" else "NO (Clean)"}")
            appendLine("- SU Binary: ${if (report.root.suBinaryFound) "FOUND in ${report.root.suPathsDetected.joinToString()}" else "Clean"}")
            appendLine("- Zygisk / Magisk: ${if (report.root.zygiskDetected) "FOUND" else "Clean"}")
            appendLine("- Frida Hooks: ${if (report.root.fridaDetected) "FOUND" else "Clean"}")
            appendLine("- SELinux Mode: ${report.root.selinuxMode}")
            appendLine("- Storage Encryption: ${report.root.storageEncryptionState}")
            appendLine("- Test-Keys: ${if (report.root.testKeysPresent) "Detected" else "Clean (Release-Keys)"}")
            appendLine("- Hooking Frameworks: ${if (report.root.xposedFrameworkDetected) "Detected" else "None"}")
            appendLine()
            appendLine("## 3. App Check & Play Integrity Attestation")
            appendLine("- App Check Provider: ${report.appCheck.appCheckProviderName}")
            appendLine("- Package Name: ${report.appCheck.packageName}")
            appendLine("- SHA-256 Fingerprint: ${report.appCheck.sha256Fingerprint}")
            appendLine("- SHA-1 Fingerprint: ${report.appCheck.sha1Fingerprint}")
            appendLine("- StrongBox Keymaster: ${if (report.appCheck.hardwareKeystore.hasStrongBox) "YES (Dedicated Chip)" else "NO (TEE)"}")
            appendLine("- Keymaster Version: ${report.appCheck.hardwareKeystore.keymasterVersion}")
            appendLine("- MEETS_BASIC_INTEGRITY: ${if (report.appCheck.playIntegrityBasic) "PASS" else "FAIL"}")
            appendLine("- MEETS_DEVICE_INTEGRITY: ${if (report.appCheck.playIntegrityDevice) "PASS" else "FAIL"}")
            appendLine("- MEETS_STRONG_INTEGRITY: ${if (report.appCheck.playIntegrityStrong) "PASS" else "FAIL"}")
            appendLine("- ADB Debugging: ${if (report.appCheck.isAdbDebuggingEnabled) "Active" else "Disabled"}")
            appendLine("- Android Version: ${report.appCheck.androidVersion} (API ${report.appCheck.apiLevel})")
            appendLine("- Security Patch: ${report.appCheck.securityPatch} (${report.appCheck.daysSinceSecurityPatch} days ago)")
            appendLine()
            appendLine("## 4. Key Findings & Recommendations")
            for (issue in report.issues) {
                appendLine("- [${issue.severity}] ${issue.title}: ${issue.description}")
                appendLine("  Recommendation: ${issue.recommendation}")
            }
        }
    }
}
