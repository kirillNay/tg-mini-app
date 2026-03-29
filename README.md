# tg-mini-app

[![tg-mini-app](https://img.shields.io/badge/tg--mini--app-1.2.0-2ea44f?logo=kotlin&logoColor=white)](https://central.sonatype.com/search?q=io.github.kirillNay%3Atg-mini-app)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose%20Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.0-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![Telegram%20Mini%20Apps%20API](https://img.shields.io/badge/Telegram%20Mini%20Apps%20API-6.9-26A5E4?logo=telegram&logoColor=white)](https://core.telegram.org/bots/webapps)

Kotlin Multiplatform library for building [Telegram Mini Apps](https://core.telegram.org/bots/webapps) with Compose Multiplatform on `jsMain`.

It provides:

- a simple `telegramWebApp { ... }` entry point for Compose UI
- a Kotlin-friendly wrapper over `window.Telegram.WebApp`
- reactive access to Telegram theme colors and viewport values
- typed access to buttons, popups, haptics, cloud storage, init data, and other WebApp APIs

## Compatibility

- Kotlin: `2.3.10`
- Compose Multiplatform plugin: `1.10.0`
- Telegram Mini Apps / Bot API coverage: wrapper includes APIs marked in the source up to Bot API `6.9`

For Telegram features added in later Bot API versions, check the runtime version with `webApp.isVersionAtLeast(...)` before using them.

## Requirements

- Kotlin Multiplatform project with a browser `js` target
- Compose Multiplatform UI rendered from `jsMain`
- Telegram WebApp script included in your HTML host page

## Installation

Add the Telegram runtime script to your page:

```html
<script src="https://telegram.org/js/telegram-web-app.js"></script>
```

Add the library dependency:

```kotlin
dependencies {
    implementation("io.github.kirillNay:tg-mini-app:1.2.0")
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

`telegramWebApp` gives your content a `TelegramStyle` instance with:

- `style.colors` - current Telegram theme colors mapped to Compose `Color`
- `style.viewPort.viewPortHeight` - current visible Mini App height
- `style.viewPort.viewportStableHeight` - stable viewport height for bottom-pinned UI

Both viewport and theme values are refreshed when Telegram emits corresponding WebApp events.

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

This makes it easy to keep Telegram-specific code near your `jsMain` entry point while the rest of your UI stays platform-agnostic.

## WebApp API

Use the global `webApp` instance to access Telegram Mini App features in Kotlin style.

### Basic lifecycle

```kotlin
import com.kirillNay.telegram.miniapp.webApp.webApp

webApp.ready()
webApp.expand()
webApp.enableClosingConfirmation(true)
webApp.setBackgroundColor("bg_color")
webApp.setHeaderColor("secondary_bg_color")
```

### Native confirmation popup

```kotlin
webApp.showConfirm("Exit checkout?") { isConfirmed ->
    if (isConfirmed) {
        webApp.close()
    }
}
```

### Main and back buttons

```kotlin
webApp.backButton.onClick { navigateBack() }
webApp.backButton.show()

webApp.mainButton
    .setText("Pay")
    .onClick { submitOrder() }
    .show()
```

### Cloud storage

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
webApp.hapticFeedback.impactOccurred("light")
webApp.hapticFeedback.notificationOccurred("success")
```

## Runtime Notes

- This library targets Telegram Mini App runtime on `jsMain`. It is not a general-purpose browser wrapper.
- `window.Telegram.WebApp` must be available before you use `telegramWebApp` or `webApp`.
- If you also run your web bundle outside Telegram, guard the Telegram-specific startup and show a browser placeholder or demo host instead.
- Treat `initDataUnsafe` as untrusted client data. Validate `rawInitData` on your server as described in the [Telegram docs](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app).

## Demo Project

The repository includes a complete sample in [`samples/coffee-order-demo`](samples/coffee-order-demo):

- shared screens and business logic in `commonMain`
- Telegram-specific host code isolated in `jsMain`
- Android and iOS demo hosts reusing the same Compose UI

You can also try the Telegram demo bot at `@tgminiapp_demo_bot` or open it directly: [t.me/tgminiapp_demo_bot/demo](https://t.me/tgminiapp_demo_bot/demo).

Run the sample from the repository root:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:jsBrowserDevelopmentRun
./gradlew -p samples/coffee-order-demo :composeApp:assembleDebug
./gradlew -p samples/coffee-order-demo :composeApp:linkDebugFrameworkIosSimulatorArm64
```

More details are available in [`samples/coffee-order-demo/README.md`](samples/coffee-order-demo/README.md).

## API Coverage

The wrapper includes typed access to commonly used Telegram WebApp features, including:

- init data and runtime metadata
- viewport and color scheme
- main button and back button
- alerts, confirms, custom popups, QR scanner
- links, invoices, clipboard access
- contact and write-access requests
- haptic feedback
- cloud storage

The API is designed to stay close to the official [Telegram Mini Apps documentation](https://core.telegram.org/bots/webapps), but exposed with Kotlin naming and suspend-friendly wrappers where it improves ergonomics.

## License

[MIT](LICENSE)
