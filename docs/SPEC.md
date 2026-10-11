# Product specification

## Goal

Subtrack is a small offline-first Android application for keeping personal subscriptions in one place and receiving a reminder before a renewal or expiration date.

The product deliberately avoids budgeting, account aggregation, analytics dashboards and cloud accounts in the first version.

## Core user story

1. Add a subscription.
2. Enter its price and next renewal/expiration date.
3. Optionally add billing period, reminder lead time, URL and note about where it was purchased.
4. See all subscriptions ordered by the nearest date.
5. Receive a local notification before the date.
6. Edit, renew or delete a subscription later.

## Subscription fields

Required:
- Name.
- Price.
- Currency.
- Next renewal or expiration date.

Optional/configurable:
- Billing period: none, monthly, yearly.
- Auto-renewal flag.
- Reminder lead time in days.
- Note.
- URL.

## Semantics

### Auto-renewing subscription

The primary date means **next payment**. For monthly/yearly subscriptions, dates that are already in the past are rolled forward when the app reconciles local data.

### Non-renewing subscription

The primary date means **expiration date**. After that date the item is shown as expired until the user edits or renews it.

## Main screen

- App title and add action.
- Compact monthly spend estimate for active recurring subscriptions.
- Subscription list sorted by nearest relevant date.
- Each row shows name, price/period, date and relative status.
- Expired items remain visible.
- Empty state has a single primary action: add subscription.

## Detail screen

Shows all stored data for one subscription with actions to edit, open its URL and delete it.

## Add/edit screen

Required fields are visually primary. Advanced fields are kept compact. Validation must prevent blank names, invalid prices and invalid dates.

## Notifications

- Local Android notifications only.
- Default reminder: 3 days before the date.
- Notification delivery around 09:00 local time is sufficient; minute-level exactness is not a product requirement.
- Notification permission is requested on Android 13+.
- Reminders are restored after device reboot.

## Privacy

- No account.
- No backend.
- No analytics or advertising SDK.
- Subscription data stays in the local Room database.
- URLs and notes are never transmitted by the app.

## MVP acceptance criteria

- Multiple subscriptions can be created, edited and deleted.
- Data survives process death and device restart.
- Main list is sorted by relevant date.
- Monthly and yearly auto-renewing subscriptions can roll forward.
- Reminder alarms are created/cancelled when data changes.
- Reboot restores alarms.
- Android 13+ notification permission is handled.
- Debug build, unit tests and Android lint pass.

## Explicitly out of scope for MVP

- Bank/email transaction scanning.
- Cloud sync and accounts.
- Shared/family subscriptions.
- Exchange-rate conversion.
- Charts and spending history.
- Catalog of service logos.
- Import/export.
- Widgets.
- More than one reminder per subscription.
