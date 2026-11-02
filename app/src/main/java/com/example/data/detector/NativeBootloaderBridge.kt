package com.example.data.detector

/**
 * Native Rust evaluator for boot-property signals.
 * Returns null if the native library is unavailable so Kotlin can use its conservative fallback.
 */
internal object NativeBootloaderBridge {
    private val available: Boolean = try {
        System.loadLibrary("bootcheck_native")
        true
    } catch (_: UnsatisfiedLinkError) {
        false
    } catch (_: SecurityException) {
        false
    }

    fun evaluate(
        flashLocked: String,
        bootLocked: String,
        verifiedBootState: String,
        vbmetaState: String
    ): String? {
        if (!available) return null
        return try {
            nativeEvaluateLockStatus(flashLocked, bootLocked, verifiedBootState, vbmetaState)
        } catch (_: UnsatisfiedLinkError) {
            null
        }
    }

    @JvmStatic
    private external fun nativeEvaluateLockStatus(
        flashLocked: String,
        bootLocked: String,
        verifiedBootState: String,
        vbmetaState: String
    ): String
}
