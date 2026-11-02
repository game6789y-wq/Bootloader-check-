<!-- BootCheck project README -->
<div align="center">

# 🛡️ BootCheck

### Android bootloader & device-state checker

A lightweight Android app project for exploring device and bootloader status.

[![Android APK Build](https://github.com/game6789y-wq/Bootloader-check-/actions/workflows/android-apk.yml/badge.svg?branch=main)](https://github.com/game6789y-wq/Bootloader-check-/actions/workflows/android-apk.yml)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](https://www.android.com/)
[![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Build](https://img.shields.io/badge/build-Gradle-02303A?logo=gradle&logoColor=white)](https://gradle.org/)

[⬇️ Get the latest test APK](https://github.com/game6789y-wq/Bootloader-check-/releases/latest) · [⚙️ Build workflow](https://github.com/game6789y-wq/Bootloader-check-/actions) · [🐛 Report an issue](https://github.com/game6789y-wq/Bootloader-check-/issues)

</div>

---

## ✨ About

**BootCheck** is an Android application project focused on checking and presenting device bootloader-related information. It is built with Android tooling and automated GitHub Actions so a test APK can be generated and its signature verified consistently.

> **Please note:** Device-state checks can be limited by Android version, device manufacturer, permissions, and system restrictions. Treat results as informational indicators—not as a substitute for a trusted device-integrity or security attestation service.

## 📲 Download

The latest available build is currently a **Debug Pre-release** intended for testing.

1. Open the [latest release page](https://github.com/game6789y-wq/Bootloader-check-/releases/latest).
2. Download `app-debug.apk`.
3. Review the release notes before installing.

Debug builds are test builds and are **not production-signed releases**. Android may show a warning when installing an APK downloaded outside the Play Store. Install only if you trust the source.

## 🔍 Build verification

The automated workflow:

- Builds the Android debug APK.
- Verifies the APK signature with Android's `apksigner`.
- Exports the signing-certificate details.
- Generates a SHA-256 checksum.
- Uploads the APK and verification files to the GitHub Release.

Available verification files are listed alongside the APK on the [Releases page](https://github.com/game6789y-wq/Bootloader-check-/releases).

## 🧰 Technology

| Component | Details |
| --- | --- |
| Platform | Android |
| Language | Kotlin |
| Build system | Gradle |
| Automation | GitHub Actions |
| Test package | Debug APK |
| Minimum Android version | Android 7.0 (API 24), as configured by the project |

## 🏗️ Build from source

You can build the debug APK in an Android development environment with a compatible JDK, Android SDK, and Gradle installation.

```bash
gradle assembleDebug
```

The APK is normally generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

You can also view the latest automated build under [GitHub Actions](https://github.com/game6789y-wq/Bootloader-check-/actions).

## 🔐 Signing & security

- CI uses a generated **Android debug keystore** for test builds.
- Debug signing is suitable for development and testing, not as a stable production signing identity.
- Do not share keystores, passwords, access tokens, or signing secrets in issues or chat.
- Verify the release checksum and certificate information when validating a downloaded APK.

## 🤝 Contributing

Suggestions, bug reports, and improvements are welcome. Please include your Android version, device model, and relevant logs when reporting a problem. **Never include personal data, credentials, or private signing material.**

- [Open an issue](https://github.com/game6789y-wq/Bootloader-check-/issues)
- [Browse source code](https://github.com/game6789y-wq/Bootloader-check-)

## 📄 License

No license file is currently provided in this repository. Unless a license is added, reuse and redistribution permissions are not explicitly granted.

---

<div align="center">

**BootCheck** · Android project · Built with GitHub Actions

<sub>If this project is useful, consider starring the repository ⭐</sub>

</div>
