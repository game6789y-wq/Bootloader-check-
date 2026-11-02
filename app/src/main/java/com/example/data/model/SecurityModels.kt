package com.example.data.model

enum class VerifiedBootState(val label: String, val colorHex: Long, val summary: String) {
    GREEN("GREEN", 0xFF00E676, "Full verification. Device boots using OEM root of trust."),
    YELLOW("YELLOW", 0xFFFFD600, "Custom key verification. Boot image signed by user-installed custom key."),
    ORANGE("ORANGE", 0xFFFF9100, "Unlocked bootloader. Verification is disabled; untrusted boot image."),
    RED("RED", 0xFFFF1744, "Verification failed or corrupted. Device could not be verified."),
    UNKNOWN("UNKNOWN", 0xFF9CA3AF, "AVB state could not be read via system properties.")
}

enum class BootloaderLockStatus(val label: String, val isSafe: Boolean) {
    LOCKED("Locked (Secure)", true),
    UNLOCKED("Unlocked (Vulnerable)", false),
    TAMPERED("Tampered / Custom", false),
    UNKNOWN("Unknown / Undetected", false)
}

enum class SecurityGrade(val title: String, val colorHex: Long) {
    SECURE("SECURE ENVIRONMENT", 0xFF00E676),
    CAUTION("MODERATE INTEGRITY", 0xFFFFD600),
    AT_RISK("ELEVATED RISK", 0xFFFF9100),
    COMPROMISED("INTEGRITY COMPROMISED", 0xFFFF1744)
}

enum class IssueSeverity {
    CRITICAL,
    WARNING,
    INFO,
    PASSED
}

data class SecurityIssue(
    val title: String,
    val description: String,
    val severity: IssueSeverity,
    val recommendation: String,
    val category: String
)

data class BootloaderInfo(
    val lockStatus: BootloaderLockStatus,
    val verifiedBootState: VerifiedBootState,
    val rawVerifiedBootState: String,
    val flashLockedRaw: String,
    val oemUnlockAllowed: Boolean?,
    val avbVersion: String,
    val vbmetaDeviceState: String,
    val bootloaderVersion: String,
    val radioVersion: String,
    val board: String,
    val hardware: String,
    val socModel: String,
    val device: String,
    val slotSuffix: String,
    val buildFingerprint: String,
    val allBootProps: Map<String, String>,
    val kernelVersion: String,
    val trebleCompliant: Boolean,
    val kernelCmdline: String
)

data class RootIntegrityInfo(
    val isRooted: Boolean,
    val suBinaryFound: Boolean,
    val suPathsDetected: List<String>,
    val testKeysPresent: Boolean,
    val rwSystemDetected: Boolean,
    val busyboxFound: Boolean,
    val rootAppsFound: List<String>,
    val xposedFrameworkDetected: Boolean,
    val zygiskDetected: Boolean,
    val fridaDetected: Boolean,
    val selinuxMode: String,
    val isSelinuxEnforcing: Boolean,
    val storageEncryptionState: String,
    val dangerousPropsFound: List<String>,
    val dangerousMounts: List<String>
)

data class HardwareKeystoreInfo(
    val hasStrongBox: Boolean,
    val keymasterVersion: String,
    val hardwareIsolationLevel: String,
    val supportsAppAttestKey: Boolean,
    val hardwareCryptoAccelerators: List<String>,
    val securityLevel: String
)

data class DeepAppCheckVerdict(
    val tokenStatus: String,
    val tokenSample: String,
    val tokenClaims: Map<String, String>,
    val tokenLatencyMs: Long,
    val tokenTtlSeconds: Long,
    val isEnforced: Boolean,
    val protectedEndpoints: List<String>
)

data class AppCheckInfo(
    val appCheckProviderName: String,
    val debugTokenSample: String,
    val sha256Fingerprint: String,
    val sha1Fingerprint: String,
    val packageName: String,
    val playIntegrityBasic: Boolean,
    val playIntegrityDevice: Boolean,
    val playIntegrityStrong: Boolean,
    val playIntegrityVirtual: Boolean,
    val isAdbDebuggingEnabled: Boolean,
    val isDeveloperOptionsEnabled: Boolean,
    val isMockLocationEnabled: Boolean,
    val isRunningInEmulator: Boolean,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val daysSinceSecurityPatch: Long,
    val hardwareKeystore: HardwareKeystoreInfo,
    val deepVerdict: DeepAppCheckVerdict
)

data class SecurityReport(
    val timestamp: Long,
    val overallScore: Int,
    val grade: SecurityGrade,
    val bootloader: BootloaderInfo,
    val root: RootIntegrityInfo,
    val appCheck: AppCheckInfo,
    val issues: List<SecurityIssue>
)

data class ImpactMatrixItem(
    val title: String,
    val category: String,
    val statusLocked: String,
    val statusUnlocked: String,
    val isBrokenWhenUnlocked: Boolean,
    val technicalReason: String
)
