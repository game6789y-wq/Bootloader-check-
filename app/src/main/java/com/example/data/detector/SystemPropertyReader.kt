package com.example.data.detector

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object SystemPropertyReader {

    private val systemPropertiesClass by lazy {
        try {
            Class.forName("android.os.SystemProperties")
        } catch (_: Throwable) {
            null
        }
    }

    private val getMethod by lazy {
        systemPropertiesClass?.getMethod("get", String::class.java, String::class.java)
    }

    fun get(key: String, defaultVal: String = ""): String {
        // Method 1: Reflection on android.os.SystemProperties
        try {
            val result = getMethod?.invoke(null, key, defaultVal) as? String
            if (!result.isNullOrEmpty() && result != defaultVal) {
                return result
            }
        } catch (_: Throwable) {
            // fallback
        }

        // Method 2: Check /proc/cmdline for androidboot props
        try {
            val cmdline = readProcCmdline()
            val prefix = if (key.startsWith("ro.boot.")) {
                "androidboot." + key.removePrefix("ro.boot.")
            } else {
                key
            }
            val match = Regex("""(?:^|\s)$prefix=([^\s]+)""").find(cmdline)
            if (match != null) {
                return match.groupValues[1]
            }
        } catch (_: Throwable) {
            // fallback
        }

        // Method 3: Shell getprop
        try {
            val process = Runtime.getRuntime().exec(arrayOf("getprop", key))
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                val line = reader.readLine()
                if (!line.isNullOrBlank()) {
                    return line.trim()
                }
            }
        } catch (_: Throwable) {
            // fallback
        }

        return defaultVal
    }

    fun readProcCmdline(): String {
        return try {
            val file = File("/proc/cmdline")
            if (file.exists() && file.canRead()) {
                file.readText().trim()
            } else {
                ""
            }
        } catch (_: Throwable) {
            ""
        }
    }

    fun readSelinuxEnforce(): String {
        return try {
            val file = File("/sys/fs/selinux/enforce")
            if (file.exists() && file.canRead()) {
                val value = file.readText().trim()
                if (value == "1") "Enforcing" else if (value == "0") "Permissive" else "Unknown ($value)"
            } else {
                val prop = get("ro.boot.selinux", "")
                if (prop.equals("permissive", ignoreCase = true)) "Permissive" else "Enforcing (Default)"
            }
        } catch (_: Throwable) {
            "Enforcing"
        }
    }
}
