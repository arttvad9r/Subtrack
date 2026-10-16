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
- `AlarmManager` handles day-level reminders. Exact-alarm permission is intentionally avoided. Reconcile alarms after reboot and time/timezone changes.
- Money is integer minor units, never `Double` persistence.
- Dates are `LocalDate` semantics.

## Build checks

Before considering a normal change complete, run:

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleRelease
ANDROID_SERIAL=<serial> ./gradlew :app:connectedDebugAndroidTest
```

Get the emulator serial with the project-local `android-emu` skill; never use the owner's physical phone.

## Git
- `main` is the working branch and must always build and pass checks.
- Work in `feat/<name>` / `fix/<name>` from `main`; several branches may run in parallel. Merge verified work into `main`, then delete the branch.
