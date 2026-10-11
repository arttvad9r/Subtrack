# Subtrack

Minimal offline Android subscription tracker built with Kotlin, Jetpack Compose and Material 3.

## What it does

- stores multiple subscriptions locally;
- tracks price, currency and renewal/expiration date;
- supports monthly, yearly and non-recurring entries;
- distinguishes auto-renewal from fixed expiration;
- keeps an optional note and service URL;
- sends a local reminder before the relevant date;
- restores reminders after device reboot and timezone/time changes.

No account, backend, analytics or advertising SDK is required.

## Status

`0.1.0` — functional MVP with hardening and end-to-end Android verification. See [`docs/verification/2026-10-11-mvp.md`](docs/verification/2026-10-11-mvp.md).

Product requirements: [`docs/SPEC.md`](docs/SPEC.md)

Architecture: [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)

Roadmap: [`docs/ROADMAP.md`](docs/ROADMAP.md)

## Build

Requirements:

- JDK 17;
- Android SDK 36;
- Android build-tools for API 36.

On the current development machine:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=$HOME/Android/Sdk
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:connectedDebugAndroidTest  # with an emulator/device connected
./gradlew :app:assembleRelease
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

Release APK is intentionally unsigned until a private signing key is configured outside git: `app/build/outputs/apk/release/app-release-unsigned.apk`.

`local.properties` is machine-local and intentionally ignored by git.

## Project structure

```text
app/src/main/java/com/arttvad9r/subtrack/
├── data/       # Room + repository
├── domain/     # models and deterministic rules
├── reminder/   # AlarmManager + notification receivers
└── ui/         # Compose UI + ViewModel
```

## Privacy

All subscription data is stored on-device in Room. The application has no network permission and does not transmit subscription names, prices, notes or URLs.

## License

MIT. See [`LICENSE`](LICENSE).
