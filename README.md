<!-- BootCheck README -->

<div align="center">

# 🛡️ BootCheck

### Android Bootloader & Verified Boot Status Checker

A lightweight Android project that inspects available boot-state signals and reports an informational assessment.

<p>
  <a href="https://github.com/game6789y-wq/Bootloader-check-/actions/workflows/android-apk.yml">
    <img src="https://github.com/game6789y-wq/Bootloader-check-/actions/workflows/android-apk.yml/badge.svg?branch=main" alt="Android APK CI">
  </a>
  <img src="https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white" alt="Android API 24+">
  <img src="https://img.shields.io/badge/Kotlin-Android%20app-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Rust-Native%20module-000000?logo=rust&logoColor=white" alt="Rust">
  <img src="https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white" alt="GitHub Actions">
</p>

[**⬇️ Releases & APKs**](https://github.com/game6789y-wq/Bootloader-check-/releases)
· [**Build status**](https://github.com/game6789y-wq/Bootloader-check-/actions)
· [**Report a bug**](https://github.com/game6789y-wq/Bootloader-check-/issues)

</div>

---

## 📌 Overview

BootCheck is an Android application project for inspecting selected system properties associated with the device bootloader and Android Verified Boot. It evaluates the signals available to the app and presents an indicative status.

The project uses **Kotlin** for Android integration and **Rust** for native evaluation, with GitHub Actions automating tests, APK assembly, and signature checks.

> [!IMPORTANT]
> **This is a diagnostic aid, not a security certification.** Android versions, manufacturer implementations, permissions, and missing or conflicting properties can affect the result. A build passing CI does not prove that detection is correct on every device. Physical-device testing is still required.

## ✨ Project highlights

- **Boot-state assessment** — evaluates available bootloader and Verified Boot indicators.
- **Kotlin + Rust** — Android-side integration with a native Rust module.
- **Automated checks** — runs Rust tests and Android unit tests in CI.
- **APK verification** — checks the APK signature and provides certificate/checksum information when produced by the workflow.
- **Open development workflow** — build history and issue reporting are available through GitHub.

These are project capabilities and configured checks; they should not be interpreted as proof that every device or detection path has been validated.

## 📲 Download and install

1. Open the [**GitHub Releases page**](https://github.com/game6789y-wq/Bootloader-check-/releases).
2. Choose a build and download `app-debug.apk` from its assets.
3. Read the release notes and compare any published SHA-256 checksum or certificate details before installing.
4. Install only if you trust the source and understand that this is a test build.

**Build type:** The current workflow produces a **debug-signed APK** for testing. It is not a production-signed release, and installing it may require allowing app installation from that source in Android settings.

## 🔬 How to interpret results

BootCheck relies on system signals that may vary between devices and Android versions.

- Treat the displayed status as an **indicator**, not definitive proof that a bootloader is locked, unlocked, or that a device is uncompromised.
- An unavailable or conflicting signal may prevent a confident assessment.
- Do not use the app result as the only basis for sensitive security decisions.
- Confirm results on the actual device and compare them with trustworthy manufacturer or Android platform information when possible.

## ⚙️ Continuous integration

The workflow in [`.github/workflows/android-apk.yml`](https://github.com/game6789y-wq/Bootloader-check-/blob/main/.github/workflows/android-apk.yml) is configured to:

1. Set up the Java, Android, and Rust build toolchains.
2. Run Rust tests.
3. Build the Rust Android native library.
4. Run Android unit tests and assemble the debug APK.
5. Verify the APK signature.
6. Publish the APK and available verification information to a GitHub test release after the required checks pass.

View [**GitHub Actions runs**](https://github.com/game6789y-wq/Bootloader-check-/actions) for the outcome of an individual run. CI success validates the automated build steps only; it does not replace installation, compatibility, or detection testing on physical hardware.

## 🧰 Technology stack

| Component | Technology |
| --- | --- |
| Android app and integration | Kotlin |
| Native evaluation | Rust |
| Build system | Gradle |
| Automation | GitHub Actions |
| Distribution build | Debug APK |
| Minimum Android version | Android 7.0 (API 24), according to project configuration |

## 🏗️ Build from source

Use a development environment with a compatible JDK, Android SDK, Rust toolchain, and the Android Rust targets/tooling used by CI. Because the app includes a native module, build the native library before assembling the APK.

The workflow is the best reference for the complete build setup:

[**Open the Android APK workflow →**](https://github.com/game6789y-wq/Bootloader-check-/blob/main/.github/workflows/android-apk.yml)

Once the required native library and Android build environment are ready, the standard Gradle debug build command is:

```bash
./gradlew assembleDebug
```

Expected APK output path:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 🔐 Signing and integrity

- The CI workflow currently builds a debug-signed APK.
- Debug signing is intended for development and testing; it is not a stable production signing identity.
- If a release provides a SHA-256 checksum and certificate fingerprints, compare them with the downloaded artifact before installation.
- A valid APK signature confirms artifact integrity relative to its signing certificate; it does **not** prove that the app is safe or that its detection results are accurate.
- Never publish keystores, signing passwords, personal access tokens, or other secrets in the repository, issues, or chat.

## 🐛 Bug reports and contributions

Please [**open an issue**](https://github.com/game6789y-wq/Bootloader-check-/issues) if you find a bug or want to suggest an improvement. Helpful details include:

- Device manufacturer and model
- Android version
- The result shown by BootCheck
- Steps to reproduce the issue
- Relevant logs with personal information and secrets removed

Please do not include passwords, signing keys, access tokens, or other private data in reports.

## 📄 License

A license file is not currently present in this repository. Until a license is added, permission to reuse, modify, or redistribute this project is not explicitly granted.

---

<div align="center">

**BootCheck** · Android · Kotlin + Rust · GitHub Actions

If this project is useful to you, consider giving the repository a ⭐

</div>
