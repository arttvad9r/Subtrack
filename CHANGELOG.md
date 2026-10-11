# Changelog

## Unreleased

### Changed
- Refreshed the Material 3 visual system with denser typography and rounded surfaces.
- Reworked the subscription list into summary and subscription cards with a contextual FAB.
- Reorganized add/edit into compact Basic, Payment, Reminder and Additional sections.
- Reworked subscription details into a price/date hero card and compact settings rows.
- Added reminder preset chips while keeping a custom day value available.

### Added
- Initial Android application and repository structure.
- Local Room storage for multiple subscriptions.
- Subscription list, detail, add/edit and delete flows.
- Monthly/yearly recurrence and non-renewing expiration semantics.
- Local day-level reminders with reboot restoration.
- Android 13+ notification permission flow.
- Optional local note and website URL.
- Per-currency monthly cost summary.
- Unit, Room and full Compose CRUD instrumentation tests.
- Debug-only notification smoke trigger, excluded from release builds.
- Product, architecture, ADR, roadmap and verification documentation.
- GitHub Actions verification workflow.

### Changed
- Subscription form now uses a reliable clickable DatePicker control and a collapsed optional section.
- Website input is validated before save.
- Reminder alarms are rescheduled after timezone/time changes.

### Fixed
- Month-end recurrence now preserves month-end semantics (`31 Jan -> 28 Feb -> 31 Mar`).
- Yearly leap-day recurrence returns to the last day of February in leap years.
- Missing a reminder window no longer leaves recurring subscriptions without a future alarm.
- Fixed the `Следующий платёд` typo in the UI.
- Russian date labels now consistently use Russian month names regardless of the device locale.

### Privacy
- No backend, account, analytics, advertising SDK or network permission.
- Subscription database/files excluded from Android backup and device transfer.
