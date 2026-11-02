package com.example.data.detector

import android.content.Context
import android.os.Build
import com.example.data.model.BootloaderInfo
import com.example.data.model.BootloaderLockStatus
import com.example.data.model.VerifiedBootState
import java.io.File

object BootloaderDetector {

    fun detect(context: Context): BootloaderInfo {
        // Read key boot properties
        val flashLocked = SystemPropertyReader.get("ro.boot.flash.locked", "")
        val bootLocked = SystemPropertyReader.get("ro.boot.locked", "")
        val vbStateRaw = SystemPropertyReader.get("ro.boot.verifiedbootstate", "").lowercase()
        val vbmetaState = SystemPropertyReader.get("ro.boot.vbmeta.device_state", "")
        val avbVersion = SystemPropertyReader.get("ro.boot.avb_version", "")
        val slotSuffix = SystemPropertyReader.get("ro.boot.slot_suffix", "")
        val oemUnlockAllowedProp = SystemPropertyReader.get("sys.oem_unlock_allowed", "")
        val trebleProp = SystemPropertyReader.get("ro.treble.enabled", "true")

        val oemUnlockAllowed = when (oemUnlockAllowedProp) {
            "1" -> true
            "0" -> false
            else -> null
        }

        // Determine Verified Boot State
        val verifiedBootState = when {
            vbStateRaw.contains("green") -> VerifiedBootState.GREEN
            vbStateRaw.contains("yellow") -> VerifiedBootState.YELLOW
            vbStateRaw.contains("orange") -> VerifiedBootState.ORANGE
            vbStateRaw.contains("red") -> VerifiedBootState.RED
            else -> {
                if (flashLocked == "1" || vbmetaState.equals("locked", ignoreCase = true)) {
                    VerifiedBootState.GREEN
                } else if (flashLocked == "0" || vbmetaState.equals("unlocked", ignoreCase = true)) {
                    VerifiedBootState.ORANGE
                } else {
                    VerifiedBootState.UNKNOWN
                }
            }
        }

        // Rust evaluates only observed boot properties. Missing data remains UNKNOWN.
        val lockStatus = NativeBootloaderBridge.evaluate(
            flashLocked = flashLocked,
            bootLocked = bootLocked,
            verifiedBootState = vbStateRaw,
            vbmetaState = vbmetaState
        )?.let { nativeResult ->
            when (nativeResult) {
                "LOCKED" -> BootloaderLockStatus.LOCKED
                "UNLOCKED" -> BootloaderLockStatus.UNLOCKED
                "TAMPERED" -> BootloaderLockStatus.TAMPERED
                else -> BootloaderLockStatus.UNKNOWN
            }
        } ?: when {
            verifiedBootState == VerifiedBootState.ORANGE || flashLocked == "0" ||
                vbmetaState.equals("unlocked", ignoreCase = true) || bootLocked == "0" ->
                BootloaderLockStatus.UNLOCKED
            verifiedBootState == VerifiedBootState.YELLOW || verifiedBootState == VerifiedBootState.RED ->
                BootloaderLockStatus.TAMPERED
            verifiedBootState == VerifiedBootState.GREEN || flashLocked == "1" ||
                vbmetaState.equals("locked", ignoreCase = true) || bootLocked == "1" ->
                BootloaderLockStatus.LOCKED
            else -> BootloaderLockStatus.UNKNOWN
        }

        val socModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try { Build.SOC_MODEL } catch (_: Throwable) { "Unknown" }
        } else {
            Build.HARDWARE
        }

        val radioVersion = try {
            Build.getRadioVersion() ?: "None / Wi-Fi"
        } catch (_: Throwable) {
            "N/A"
        }

        val kernelVersion = getKernelVersion()
        val kernelCmdline = SystemPropertyReader.readProcCmdline()

        val allBootProps = linkedMapOf(
            "ro.boot.verifiedbootstate" to vbStateRaw.ifEmpty { "unavailable" },
            "ro.boot.flash.locked" to flashLocked.ifEmpty { "unavailable" },
            "ro.boot.vbmeta.device_state" to vbmetaState.ifEmpty { "unavailable" },
            "ro.boot.avb_version" to avbVersion.ifEmpty { "unavailable" },
            "ro.boot.slot_suffix" to slotSuffix.ifEmpty { "unavailable" },
            "ro.boot.bootloader" to Build.BOOTLOADER.ifEmpty { "unavailable" },
            "ro.boot.hardware" to Build.HARDWARE,
            "ro.boot.serialno" to "[Protected Keymaster Enclave]",
            "sys.oem_unlock_allowed" to oemUnlockAllowedProp.ifEmpty { "unavailable" },
            "ro.oem_unlock_supported" to SystemPropertyReader.get("ro.oem_unlock_supported", "unavailable"),
            "ro.treble.enabled" to trebleProp,
            "ro.vndk.version" to SystemPropertyReader.get("ro.vndk.version", Build.VERSION.RELEASE),
            "ro.crypto.state" to SystemPropertyReader.get("ro.crypto.state", "encrypted"),
            "ro.crypto.type" to SystemPropertyReader.get("ro.crypto.type", "file"),
            "ro.security.fips.ux" to SystemPropertyReader.get("ro.security.fips.ux", "unavailable"),
            "ro.hardware.keystore" to SystemPropertyReader.get("ro.hardware.keystore", "unavailable")
        )

        return BootloaderInfo(
            lockStatus = lockStatus,
            verifiedBootState = verifiedBootState,
            rawVerifiedBootState = vbStateRaw.ifEmpty { "unavailable" },
            flashLockedRaw = flashLocked.ifEmpty { "unavailable" },
            oemUnlockAllowed = oemUnlockAllowed,
            avbVersion = avbVersion.ifEmpty { "unavailable" },
            vbmetaDeviceState = vbmetaState.ifEmpty { "unavailable" },
            bootloaderVersion = Build.BOOTLOADER.ifEmpty { "unknown" },
            radioVersion = radioVersion,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            socModel = socModel.ifEmpty { Build.HARDWARE },
            device = Build.DEVICE,
            slotSuffix = slotSuffix.ifEmpty { "unavailable" },
            buildFingerprint = Build.FINGERPRINT,
            allBootProps = allBootProps,
            kernelVersion = kernelVersion,
            trebleCompliant = trebleProp.equals("true", ignoreCase = true) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O,
            kernelCmdline = kernelCmdline.ifEmpty { "unavailable" }
        )
    }

    private fun getKernelVersion(): String {
        return try {
            val file = File("/proc/version")
            if (file.exists() && file.canRead()) {
                file.readText().trim()
            } else {
                System.getProperty("os.version") ?: "Linux 6.x / Android Kernel"
            }
        } catch (_: Throwable) {
            System.getProperty("os.version") ?: "Linux Kernel"
        }
    }

    private fun isLikelyEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.BOARD == "QC_Reference_Phone"
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST.startsWith("Build")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }
}
