# Hello RGB

A tiny native Android app by **Buggy Games YT** that says "Hello World" in a glowing, animated RGB rainbow.

- Target: Android 17 (API 37), runs on Android 7.0+ (API 24)
- Simple GUI: rainbow "Hello World", speed slider, Pause/Play button
- Pure Java, no Gradle or AndroidX: built with `aapt2`, `javac`, `d8`, `zipalign`, `apksigner`

## Install
Download `HelloRGB.apk` from [Releases](../../releases) and open it on your phone (allow installs from unknown apps).

## Build
Edit the paths at the top of `build.sh` to point at a JDK 17, Android platform `android-37.0`, and build-tools `37.0.0`, then run `bash build.sh`. It creates a debug keystore on first run.
