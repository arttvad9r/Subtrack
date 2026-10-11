# Contributing

## Local setup

Use JDK 17 and Android SDK 36.

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=$HOME/Android/Sdk
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Project rules

- Keep the app local-first unless a product requirement explicitly changes that.
- Avoid adding architecture layers or dependencies without a concrete need.
- Store money in integer minor units and use `LocalDate` semantics for billing dates.
- Changes to recurrence or reminders should include deterministic unit tests.
- Do not commit signing keys, `local.properties`, IDE state or build outputs.

## Pull requests

Keep changes focused and describe:

1. the user-visible behavior being changed;
2. any data-model or reminder implications;
3. tests or manual verification performed.
