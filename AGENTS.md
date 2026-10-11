# Subtrack agent notes

Subtrack is a small native Android app for personal subscription tracking.

## Product constraints

- Keep it offline-first. Do not add a backend or account system without an explicit product decision.
- Keep the main flow short: list -> details -> add/edit.
- No analytics, advertising, bank/email scanning or exchange-rate APIs in the MVP.
- Notes and URLs are local user data and must not be transmitted.

## Architecture

- Kotlin + Jetpack Compose + Material 3.
- Single `app` module.
- Room is the source of truth.
- Manual dependency injection through `AppContainer`; no Hilt/Koin.
- `AlarmManager` handles day-level reminders. Exact-alarm permission is intentionally avoided.
- Money is integer minor units, never `Double` persistence.
- Dates are `LocalDate` semantics.

## Build checks

Before considering a normal change complete, run:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=$HOME/Android/Sdk
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Repository hygiene

Do not commit `local.properties`, `.gradle`, `.idea`, build outputs, APK/AAB files or signing material.

Prefer small focused dependencies and small files. Add architecture layers only when current complexity actually requires them.
