# Roadmap

## 0.1 — MVP

- [x] Product and architecture specification.
- [x] Room persistence.
- [x] Subscription list.
- [x] Add/edit flow.
- [x] Detail/delete flow.
- [x] Local reminders.
- [x] Reboot reminder restore.
- [x] Notification permission flow.
- [x] Domain unit tests.
- [x] Android Room instrumentation test.
- [x] Full Compose add/edit/delete instrumentation flow.
- [x] URL validation and real DatePicker interaction.
- [x] Month-end and leap-year recurrence hardening.
- [x] Process death / force-stop persistence smoke.
- [x] Device reboot reminder restore smoke.
- [x] Timezone-change reminder rescheduling.
- [x] Notification denied/granted runtime smoke.
- [x] Dark mode + 200% font-scale instrumentation pass.
- [x] Debug + R8 release build and lint verification.

## Next candidates

Only after the MVP is stable:

- multiple reminders per subscription;
- custom billing interval;
- local backup/export;
- home-screen widget;
- categories/search when list size justifies them;
- optional encrypted cloud backup.

## Explicit non-goals for now

- transaction scraping;
- email receipt parsing;
- service-logo catalog;
- spending charts/history;
- shared subscriptions;
- currency conversion.
