# tg-mini-app

[![tg-mini-app](https://img.shields.io/badge/tg--mini--app-2.0.0-2ea44f?logo=kotlin&logoColor=white)](https://central.sonatype.com/search?q=io.github.kirillNay%3Atg-mini-app)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose%20Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.12.1-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![Telegram%20Mini%20Apps%20API](https://img.shields.io/badge/Telegram%20Mini%20Apps%20API-10.1-26A5E4?logo=telegram&logoColor=white)](https://core.telegram.org/bots/webapps)

Kotlin Multiplatform library for building [Telegram Mini Apps](https://core.telegram.org/bots/webapps) with Compose Multiplatform for the web (`js` and `wasmJs`).

It provides:

- a simple `telegramWebApp { ... }` entry point for Compose UI
- a Kotlin-friendly wrapper over `window.Telegram.WebApp`
- reactive access to Telegram theme colors, viewport and safe area insets
- typed events, buttons, popups, haptics, storages, biometrics, sensors, location, init data and other WebApp APIs

## Compatibility

- Kotlin: `2.4.20`
- Compose Multiplatform: `1.12.1`
- Targets: `js` and `wasmJs` (browser)
- Telegram Mini Apps coverage: up to Bot API `10.1`, every member is marked with the Bot API version it needs

Telegram clients may support an older Bot API version than the one your code uses. Check the runtime version with `webApp.isVersionAtLeast(...)` before using newer features.

## Requirements

- Kotlin Multiplatform project with a browser `js` and/or `wasmJs` target
- Compose Multiplatform UI rendered from `webMain` (or `jsMain` / `wasmJsMain`)
- Telegram WebApp script included in your HTML host page

## Installation

Add the Telegram runtime script to your page:

```html
<script src="https://telegram.org/js/telegram-web-app.js"></script>
```

Add the library dependency:

```kotlin
dependencies {
    implementation("io.github.kirillNay:tg-mini-app:2.0.0")
}
```

## Quick Start

Use `telegramWebApp` as the entry point of your Telegram-hosted web app:

```kotlin
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.kirillNay.telegram.miniapp.compose.telegramWebApp
import com.kirillNay.telegram.miniapp.webApp.webApp

fun main() {
    telegramWebApp { style ->
        LaunchedEffect(Unit) {
            webApp.ready()
            webApp.expand()
        }

        Surface(
            color = style.colors.backgroundColor,
        ) {
            Text("Hello, ${webApp.initDataUnsafe.user?.firstName ?: "Telegram"}")
        }
    }
}
```

`telegramWebApp` gives your content a `TelegramStyle` instance, also available as `LocalTelegramStyle`:

- `style.colors` - current Telegram theme colors mapped to Compose `Color`
- `style.colorScheme` / `style.isDark` - light or dark Telegram theme
- `style.viewPort.height` - current visible Mini App height
- `style.viewPort.stableHeight` - stable viewport height for bottom-pinned UI
- `style.safeAreaInset` / `style.contentSafeAreaInset` - Bot API 8.0+ insets as `PaddingValues`

These values are refreshed when Telegram emits the corresponding WebApp events.

If the page can also be opened in a regular browser, pass a `fallback`. It is shown instead of the Mini App when the page was not launched by Telegram:

```kotlin
telegramWebApp(fallback = { OpenInTelegramScreen() }) { style ->
    App(style)
}
```

## Compose Integration

The library adds only a thin Compose integration layer:

```kotlin
telegramWebApp { style ->
    App(
        background = style.colors.backgroundColor,
        primary = style.colors.buttonColor,
        viewportHeight = style.viewPort.viewportStableHeight,
    )
}
```

This makes it easy to keep Telegram-specific code near your web (`webMain`) entry point while the rest of your UI stays platform-agnostic.

## WebApp API

Use the global `webApp` instance to access Telegram Mini App features in Kotlin style.

### Basic lifecycle

```kotlin
import com.kirillNay.telegram.miniapp.webApp.webApp

webApp.ready()
webApp.expand()
webApp.enableClosingConfirmation()
webApp.setBackgroundColor("bg_color")
webApp.setHeaderColor("secondary_bg_color")
```

Accessing `webApp` throws if telegram-web-app.js is not loaded. Use `WebApp.isAvailable`, `WebApp.isRunningInTelegram` or `WebApp.getOrNull()` to check first.

### Native popups

Methods with a callback have `await*` suspend counterparts:

```kotlin
webApp.showConfirm("Exit checkout?") { isConfirmed ->
    if (isConfirmed) webApp.close()
}

suspend fun confirmExit() {
    if (webApp.awaitConfirm("Exit checkout?")) webApp.close()
}
```

### Events

```kotlin
val subscription = webApp.onEvent(WebAppEvent.ViewportChanged) { isStateStable ->
    if (isStateStable) saveLayout()
}
subscription.unsubscribe()
```

### Main and back buttons

```kotlin
webApp.backButton.onClick { navigateBack() }
webApp.backButton.show()

webApp.mainButton
    .setText("Pay")
    .onClick { submitOrder() }
    .show()

// Bot API 7.10+
webApp.secondaryButton
    .setParams(BottomButtonParams(text = "Cancel", position = BottomButtonPosition.LEFT, isVisible = true))
```

`offClick` accepts the same lambda instance that was passed to `onClick`.

### Storages

`cloudStorage` (Bot API 6.9+), `deviceStorage` and `secureStorage` (Bot API 9.0+) are suspend APIs returning `Result`. Telegram errors are reported as `WebAppException`.

```kotlin
suspend fun saveDraft(note: String) {
    webApp.cloudStorage.setItem("draft_note", note)
}

suspend fun loadDraft(): String {
    return webApp.cloudStorage.getItem("draft_note").getOrDefault("")
}
```

### Haptic feedback

```kotlin
webApp.hapticFeedback.impactOccurred(HapticFeedback.ImpactStyle.LIGHT)
webApp.hapticFeedback.notificationOccurred(HapticFeedback.NotificationType.SUCCESS)
```

## Runtime Notes

- This library targets Telegram Mini App runtime in the browser (`js` and `wasmJs`). It is not a general-purpose browser wrapper.
- `window.Telegram.WebApp` must be available before you use `telegramWebApp` or `webApp`.
- If you also run your web bundle outside Telegram, pass `fallback` to `telegramWebApp` or check `WebApp.isRunningInTelegram`.
- For the `js` target the library waits for the Skiko runtime before rendering; for `wasmJs` it renders right away. To serve both, build the sample-style `composeCompatibilityBrowserDistribution`, which picks Wasm and falls back to JS in browsers without WasmGC.
- Treat `initDataUnsafe` as untrusted client data. Validate `rawInitData` on your server as described in the [Telegram docs](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app).

## Demo Project

The repository includes a complete sample in [`samples/coffee-order-demo`](samples/coffee-order-demo):

- shared screens and business logic in `commonMain`
- Telegram-specific host code isolated in `webMain` (built for `js` and `wasmJs`)
- Android (`androidApp` module) and iOS demo hosts reusing the same Compose UI

You can also try the Telegram demo bot at `@tgminiapp_demo_bot` or open it directly: [t.me/tgminiapp_demo_bot/demo](https://t.me/tgminiapp_demo_bot/demo).

Run the sample from the repository root:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:wasmJsBrowserDevelopmentRun
./gradlew -p samples/coffee-order-demo :composeApp:jsBrowserDevelopmentRun
./gradlew -p samples/coffee-order-demo :androidApp:assembleDebug
./gradlew -p samples/coffee-order-demo :composeApp:linkDebugFrameworkIosSimulatorArm64
```

More details are available in [`samples/coffee-order-demo/README.md`](samples/coffee-order-demo/README.md).

## API Coverage

The wrapper covers the Telegram WebApp API up to Bot API 10.1:

- init data (including `signature` and `chat_join_request_query_id`) and runtime metadata
- viewport, color scheme, theme colors, safe area insets, fullscreen, orientation lock, vertical swipes
- main, secondary, back and settings buttons
- alerts, confirms, custom popups, QR scanner, hiding the keyboard
- links, invoices, stories, message sharing, file downloads, chat requests, emoji status, home screen shortcuts
- contact and write-access requests
- haptic feedback
- cloud, device and secure storage
- biometrics, accelerometer, gyroscope, device orientation, location
- typed events for all of the above

The API is designed to stay close to the official [Telegram Mini Apps documentation](https://core.telegram.org/bots/webapps), but exposed with Kotlin naming and suspend-friendly wrappers where it improves ergonomics.

## Migrating from 1.x to 2.0

2.0 changes the public API to be type-safe and to work on both `js` and `wasmJs`:

| 1.x | 2.0 |
|---|---|
| `webApp.addEventHandler(EventType.X) { any -> }` / `removeEventHandler` | `webApp.onEvent(WebAppEvent.X) { payload -> }` returns an `EventSubscription` |
| `webApp.enableClosingConfirmation(Boolean)` | `enableClosingConfirmation()` / `disableClosingConfirmation()` |
| `webApp.mainButton: MainButton` | `webApp.mainButton: BottomButton`, plus `secondaryButton` |
| `ButtonParams(textColor = ..., isActive = ...)` | `BottomButtonParams(...)` |
| `hapticFeedback.impactOccurred("light")` | `impactOccurred(HapticFeedback.ImpactStyle.LIGHT)` |
| `BackButton.show()` returns `Unit` | returns `BackButton` for chaining |
| `ColorScheme.getValue(...)` / `InvoiceStatus.getValue(...)` throw on unknown values | unknown values map to a fallback (`LIGHT`, `InvoiceStatus.UNKNOWN`) |
| `ChatType.CHANNEL` | `ChatType.CHANNELS` |
| `switchInlineQuery(query, vararg chatType)` | `switchInlineQuery(query, chatTypes: List<ChatType>)` |
| `openLink(url, tryInstantView: Boolean?)` | `openLink(url, tryInstantView: Boolean = false)` |
| `PopupParams(title, message, buttons: Array)` / `PopupButton(id, text, buttonType)` | `PopupParams(message, title, buttons: List)` / `PopupButton(id, type, text)` |
| `readTextFromClipboard()` suspend overload | `awaitClipboardText()`; other popups also have `await*` variants |
| `webApp.viewportHeight: Float` | `Double` |
| `WebAppUser`, `WebAppChat`, `WebAppInitData`, `ThemeParams` external classes | Kotlin data classes; `WebAppChat.userName` is now `username`, `authDate` is `Long?` |
| `ViewPort.viewPortHeight` / `viewportStableHeight` | `ViewPort.height` / `stableHeight` |
| `TelegramColors.fromWebApp()` | `TelegramColors.from(webApp.themeParams)` |
| `CloudStorage.removeItems(vararg)` etc. | same, plus `List` overloads; errors are `WebAppException` |

Publishing moved from the Sonatype staging plugin to `com.vanniktech.maven.publish`. CI passes the existing `SONATYPE_*` and `SIGNING_*` secrets as `ORG_GRADLE_PROJECT_mavenCentral*` and `ORG_GRADLE_PROJECT_signingInMemory*` properties.

## Changelog

See [CHANGELOG.md](CHANGELOG.md).

## License

[MIT](LICENSE)
