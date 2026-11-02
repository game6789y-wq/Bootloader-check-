package com.example.data.detector

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.model.RootIntegrityInfo
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket

object RootDetector {

    private val SU_PATHS = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/system/usr/we-need-root/su",
        "/system/xbin/daemonsu",
        "/magisk/.core/bin/su"
    )

    private val ROOT_PACKAGES = listOf(
        "com.topjohnwu.magisk",
        "me.weishu.kernelsu",
        "io.github.vvb2060.magisk",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.noshufou.android.su",
        "com.thirdparty.superuser",
        "com.kingroot.kinguser",
        "com.kingouser.com",
        "bmax.apatch"
    )

    fun detect(context: Context): RootIntegrityInfo {
        val foundSuPaths = mutableListOf<String>()
        for (path in SU_PATHS) {
            try {
                val f = File(path)
                if (f.exists()) {
                    foundSuPaths.add(path)
                }
            } catch (_: Throwable) {
                // permission or restriction
            }
        }

        val whichSuPath = checkWhichSu()
        if (whichSuPath != null && !foundSuPaths.contains(whichSuPath)) {
            foundSuPaths.add(whichSuPath)
        }

        val testKeysPresent = Build.TAGS != null && Build.TAGS.contains("test-keys")

        val dangerousProps = mutableListOf<String>()
        val debuggable = SystemPropertyReader.get("ro.debuggable", "")
        if (debuggable == "1") dangerousProps.add("ro.debuggable=1")
        val secure = SystemPropertyReader.get("ro.secure", "")
        if (secure == "0") dangerousProps.add("ro.secure=0")

        val (rwSystemDetected, dangerousMounts) = checkRwMounts()
        val busyboxFound = checkBusybox()
        val installedRootApps = checkRootPackages(context)
        val xposedDetected = checkXposed()
        val zygiskDetected = checkZygiskOrMagiskMaps()
        val fridaDetected = checkFridaInstrumentation()

        val selinuxMode = SystemPropertyReader.readSelinuxEnforce()
        val isSelinuxEnforcing = selinuxMode.startsWith("Enforcing")

        val cryptoState = SystemPropertyReader.get("ro.crypto.state", "encrypted")
        val cryptoType = SystemPropertyReader.get("ro.crypto.type", "file")
        val storageEncryptionState = if (cryptoState.equals("encrypted", ignoreCase = true)) {
            "Hardware Encrypted ($cryptoType)"
        } else {
            "Unencrypted / Disabled"
        }

        val isRooted = foundSuPaths.isNotEmpty() ||
                installedRootApps.isNotEmpty() ||
                xposedDetected ||
                zygiskDetected ||
                fridaDetected ||
                (dangerousProps.isNotEmpty() && testKeysPresent)

        return RootIntegrityInfo(
            isRooted = isRooted,
            suBinaryFound = foundSuPaths.isNotEmpty(),
            suPathsDetected = foundSuPaths,
            testKeysPresent = testKeysPresent,
            rwSystemDetected = rwSystemDetected,
            busyboxFound = busyboxFound,
            rootAppsFound = installedRootApps,
            xposedFrameworkDetected = xposedDetected,
            zygiskDetected = zygiskDetected,
            fridaDetected = fridaDetected,
            selinuxMode = selinuxMode,
            isSelinuxEnforcing = isSelinuxEnforcing,
            storageEncryptionState = storageEncryptionState,
            dangerousPropsFound = dangerousProps,
            dangerousMounts = dangerousMounts
        )
    }

    private fun checkWhichSu(): String? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                val line = reader.readLine()
                if (!line.isNullOrBlank() && line.startsWith("/")) line.trim() else null
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun checkRwMounts(): Pair<Boolean, List<String>> {
        val dangerousList = mutableListOf<String>()
        return try {
            val file = File("/proc/mounts")
            if (file.exists() && file.canRead()) {
                file.useLines { lines ->
                    lines.forEach { line ->
                        val parts = line.split("\\s+".toRegex())
                        if (parts.size >= 4) {
                            val mountPoint = parts[1]
                            val flags = parts[3]
                            if ((mountPoint == "/system" || mountPoint == "/" || mountPoint == "/vendor" || mountPoint == "/product") &&
                                flags.split(",").contains("rw")
                            ) {
                                dangerousList.add("$mountPoint ($flags)")
                            }
                        }
                    }
                }
            }
            Pair(dangerousList.isNotEmpty(), dangerousList)
        } catch (_: Throwable) {
            Pair(false, emptyList())
        }
    }

    private fun checkBusybox(): Boolean {
        val paths = listOf("/system/bin/busybox", "/system/xbin/busybox", "/sbin/busybox", "/vendor/bin/busybox")
        return paths.any {
            try { File(it).exists() } catch (_: Throwable) { false }
        }
    }

    private fun checkRootPackages(context: Context): List<String> {
        val pm = context.packageManager
        val detected = mutableListOf<String>()
        for (pkg in ROOT_PACKAGES) {
            try {
                pm.getPackageInfo(pkg, 0)
                detected.add(pkg)
            } catch (_: PackageManager.NameNotFoundException) {
                // Not installed
            } catch (_: Throwable) {
                // Query denied
            }
        }
        return detected
    }

    private fun checkXposed(): Boolean {
        return try {
            Class.forName("de.robv.android.xposed.XposedBridge")
            true
        } catch (_: Throwable) {
            false
        }
    }

    private fun checkZygiskOrMagiskMaps(): Boolean {
        return try {
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists() && mapsFile.canRead()) {
                mapsFile.useLines { lines ->
                    lines.any { line ->
                        line.contains("zygisk", ignoreCase = true) ||
                                line.contains("magisk", ignoreCase = true) ||
                                line.contains("libriruloader.so", ignoreCase = true)
                    }
                }
            } else false
        } catch (_: Throwable) {
            false
        }
    }

    private fun checkFridaInstrumentation(): Boolean {
        // Method 1: Check thread names in current process
        try {
            val taskDir = File("/proc/self/task")
            if (taskDir.exists() && taskDir.isDirectory) {
                val threadDirs = taskDir.listFiles()
                if (threadDirs != null) {
                    for (dir in threadDirs) {
                        val comm = File(dir, "comm")
                        if (comm.exists() && comm.canRead()) {
                            val name = comm.readText().trim()
                            if (name.contains("frida", ignoreCase = true) || name.contains("gum-js-loop", ignoreCase = true)) {
                                return true
                            }
                        }
                    }
                }
            }
        } catch (_: Throwable) {
            // ignore
        }

        // Method 2: Check standard Frida ports non-blocking
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("127.0.0.1", 27042), 50)
            socket.close()
            true
        } catch (_: Throwable) {
            false
        }
    }
}
