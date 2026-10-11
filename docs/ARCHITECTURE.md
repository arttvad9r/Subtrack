# Architecture

## Principles

Subtrack is local-first and intentionally single-module. The architecture should stay understandable without a DI framework or unnecessary abstraction layers.

## Package layout

```text
com.arttvad9r.subtrack/
  data/       Room entities, DAO, database, repository
  domain/     App model and date/price rules
  reminder/   AlarmManager scheduling and receivers
  ui/         Compose screens, theme and ViewModel
```

## Data flow

```text
Compose screen
    ↓ events
SubtrackViewModel
    ↓
SubscriptionRepository
    ↓
Room DAO
    ↓
SQLite
```

The repository also coordinates reminder scheduling when a subscription is created, changed or removed.

## Dependency injection

`SubtrackApplication` owns one `AppContainer`. The container creates the database, repository and reminder scheduler. ViewModels receive the repository through a small factory.

No Hilt/Koin is required for the current size of the app.

## Persistence

Room is the source of truth.

Money is stored as integer minor units (`Long`) plus a three-letter currency code. This avoids floating-point rounding errors.

Dates are stored as ISO local-date strings (`YYYY-MM-DD`). A subscription is date-based rather than instant-based, because the product asks users for a billing day, not a payment timestamp.

## Recurrence

Supported MVP billing periods:
- none;
- monthly;
- yearly.

For an auto-renewing recurring subscription whose stored date is in the past, repository reconciliation rolls the date forward until it is today or in the future. Month-end entries preserve month-end semantics, so `31 Jan -> 28 Feb -> 31 Mar`; yearly leap-day entries similarly return to the final day of February in leap years.

## Reminder delivery

The app uses `AlarmManager.setAndAllowWhileIdle` rather than exact alarms. Reminders are day-level and do not justify the privileged exact-alarm permission.

Each alarm is scheduled for about 09:00 local time on `date - reminderDays`.

The PendingIntent identity is derived from the subscription id, so saving a subscription replaces its previous alarm and deleting it can cancel the alarm.

`BootReceiver` reloads active subscriptions after `BOOT_COMPLETED` and recreates alarms. `TimeChangeReceiver` does the same after system time/timezone changes so the intended reminder remains near 09:00 local time.

## Recurring reminders while the app stays closed

The alarm payload contains enough recurrence information to schedule the following reminder after the current reminder fires. This keeps monthly/yearly reminders alive even if the user does not open the app between billing cycles. The database date itself is only reconciled independently, so firing an early reminder does not prematurely move the visible next-payment date.

## UI state

A single activity hosts Compose. Navigation is deliberately lightweight: the app state switches between list, detail and edit destinations without adding Navigation Compose for three simple screens.

The ViewModel exposes the Room list as `StateFlow` and performs mutations in `viewModelScope`.

## Error strategy

- Invalid form input is blocked before persistence.
- Optional website URLs are validated before persistence; bare hosts such as `example.com` are normalized to HTTPS when opened.
- Scheduling failure must not lose subscription data.
- Receiver work uses `goAsync()` and a bounded coroutine instead of blocking the main thread.

## Testing strategy

Unit tests cover deterministic domain rules first:
- recurrence/date roll-forward;
- reminder date calculation;
- price parsing/formatting.

Android instrumentation covers Room CRUD, the full Compose add/edit/delete flow, DatePicker interaction and activity recreation. Runtime adb smoke tests cover notification permission behavior, force-stop persistence, reboot restoration and timezone rescheduling.
