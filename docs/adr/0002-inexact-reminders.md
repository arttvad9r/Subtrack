# ADR 0002: Inexact day-level reminders

Status: accepted

Subscription reminders are scheduled with `AlarmManager.setAndAllowWhileIdle` for approximately 09:00 local time.

The product is date-based, so minute-level exactness provides little value and would require stronger Android alarm privileges. The app therefore avoids exact-alarm permission in the MVP.
