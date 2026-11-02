<!-- BootCheck project README -->
<div align="center">

# 🛡️ BootCheck

### Android Bootloader & Device-State Checker

**Understand your device's reported boot state — right from Android.**

<p>
  <a href="https://github.com/game6789y-wq/Bootloader-check-/actions/workflows/android-apk.yml">
    <img src="https://github.com/game6789y-wq/Bootloader-check-/actions/workflows/android-apk.yml/badge.svg?branch=main" alt="Android APK Build">
  </a>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Rust-Native%20module-000000?logo=rust&logoColor=white" alt="Rust native module">
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle&logoColor=white" alt="Gradle">
</p>

[**⬇️ Download test APK**](https://github.com/game6789y-wq/Bootloader-check-/releases/latest)
· [Build history](https://github.com/game6789y-wq/Bootloader-check-/actions)
· [Report a bug](https://github.com/game6789y-wq/Bootloader-check-/issues)

</div>

---

## ✨ About BootCheck

BootCheck is an Android app project that checks selected system properties related to bootloader and verified-boot state, then evaluates the available signals to present an informational result.

The project combines **Kotlin** for Android-side logic with a **Rust native module** and uses GitHub Actions to automate testing, APK building, and signature verification.

> [!IMPORTANT]
> **Results are indicators, not a security guarantee.** Android versions, device manufacturers, access restrictions, and unavailable or conflicting system properties can affect what the app can determine. A successful build does not by itself prove that detection is accurate on every physical device.

## 📥 Try the latest test build

The latest published build is a **Debug Pre-release** for testing.

1. Open the [latest release](https://github.com/game6789y-wq/Bootloader-check-/releases/latest).
2. Download `app-debug.apk`.
3. Review the release notes and verification files before installing.

Debug APKs are intended for development and testing, not as production-signed releases. Android may warn you when installing apps from outside the Play Store; install only if you trust the source.

## 🔎 What CI checks

The GitHub Actions workflow is configured to:

- Run Rust tests.
- Build the Rust Android native library.
- Run Android unit tests and assemble the debug APK.
- Verify the APK signature.
- Publish certificate information and a SHA-256 checksum with the test release.

Check the [workflow runs](https://github.com/game6789y-wq/Bootloader-check-/actions) for the result of a specific build. These checks validate the build pipeline; **real-device installation and detection still need device-level testing**.

## 🧩 Technology stack

| Area | Technology |
| --- | --- |
| Android app | Kotlin |
| Native evaluation module | Rust |
| Android build | Gradle |
| Automation | GitHub Actions |
| Test distribution | Debug APK |
| Minimum Android version | Android 7.0 (API 24), as configured in the project |

## 🛠️ Build from source

Build in an environment with a compatible JDK, Android SDK, Rust toolchain, and the Android Rust target/tooling required by the workflow.

The repository's GitHub Actions workflow is the reference for the complete build sequence:

[**View the Android APK workflow →**](https://github.com/game6789y-wq/Bootloader-check-/blob/main/.github/workflows/android-apk.yml)

For a standard Gradle debug build after the required native library is available:

```bash
./gradlew assembleDebug
```

The APK is normally generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 🔐 Signing & download verification

- CI creates a debug-signed APK for testing.
- Debug signing is not a stable production signing identity.
- The release includes certificate details and a SHA-256 checksum when those assets are available.
- Compare the checksum and certificate information when validating a downloaded APK.
- Never post keystores, signing passwords, access tokens, or other secrets in issues or chat.

## 🐞 Feedback & contributions

Found a problem or have an idea? Please [open an issue](https://github.com/game6789y-wq/Bootloader-check-/issues) and include, when relevant:

- Device model and Android version
- The result shown by the app
- Steps to reproduce and relevant, sanitized logs

Please remove personal information and credentials from logs before sharing.

## 📄 License

No license file is currently provided. Until a license is added, reuse and redistribution permissions are not explicitly granted.

---

<div align="center">

**BootCheck** · Android · Kotlin + Rust · GitHub Actions

If you find this project useful, consider giving the repository a ⭐

</div>
