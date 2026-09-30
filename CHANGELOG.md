# Changelog

All notable changes to `io.github.kirillNay:tg-mini-app` are documented here.
The project follows [Semantic Versioning](https://semver.org/). Every released version has a section
`## [x.y.z] - YYYY-MM-DD`; its content is used as the GitHub Release notes.

## [2.0.0] - 2026-09-30

Major update: Kotlin/Wasm support, the Telegram Mini Apps API up to Bot API 10.1 and a type-safe public API.
See "Migrating from 1.x to 2.0" in the README.

### Added
- `wasmJs` target next to `js`; the library code is shared in `webMain`.
- Telegram Mini Apps API from Bot API 7.0 to 10.1: `SettingsButton`, `secondaryButton`, new theme colors,
  vertical swipes, fullscreen, safe area insets, orientation lock, home screen shortcuts, emoji status,
  `shareMessage`, `shareToStory`, `downloadFile`, `hideKeyboard`, `requestChat`, `BiometricManager`,
  `Accelerometer`, `Gyroscope`, `DeviceOrientation`, `LocationManager`, `DeviceStorage`, `SecureStorage`,
  `initDataUnsafe.signature` and `chatJoinRequestQueryId`.
- Typed `WebAppEvent`s with parsed payloads; `onEvent` returns an `EventSubscription`.
- `await*` suspend variants of popups, invoices, sharing and permission requests.
- `telegramWebApp(fallback = ...)` for pages opened outside Telegram, `LocalTelegramStyle`,
  safe area insets and color scheme in `TelegramStyle`.
- `WebApp.isAvailable`, `WebApp.isRunningInTelegram`, `WebApp.getOrNull()`.

### Changed
- Public data types (`WebAppUser`, `WebAppChat`, `WebAppInitData`, `ThemeParams`) are Kotlin data classes.
- `MainButton` became `BottomButton`, `ButtonParams` became `BottomButtonParams`.
- Haptic feedback takes enums; `ChatType.CHANNEL` is `ChatType.CHANNELS`.
- Kotlin 2.4.20, Compose Multiplatform 1.12.1; publishing through the Central Portal.

### Fixed
- `openLink` passed the `WebApp` object instead of an options object.
- `telegramWebApp` never removed its event handlers.
- `offClick` did not remove handlers registered with `onClick`.
- `null` fields in popup parameters crashed telegram-web-app.js.
- User and chat ids were declared as Kotlin `Long` instead of JS numbers; `auth_date` is parsed from its string form.
- Unknown color schemes and invoice statuses threw exceptions.

## [1.2.0] - 2026-03-29

- Telegram Mini Apps API up to Bot API 6.9 for the Kotlin/JS target.
