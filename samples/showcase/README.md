# tg-mini-app Showcase

A catalog of every Telegram Mini Apps capability wrapped by `tg-mini-app`, without any theme or business logic.
Each feature card shows the Bot API version it needs, lets you set its parameters and run it, and prints the result.
Telegram events appear in the event log, and the "Launch data" screen shows init data, theme parameters,
viewport, safe area and live sensor values.

Sections: app behavior, appearance, buttons, popups and input, haptic feedback, links and payments,
sharing and home screen, permissions and chats, cloud / device / secure storage, biometrics, location, motion sensors.

Some features need data that only a bot can create. Paste it into the feature's fields:
- invoice links: Bot API `createInvoiceLink`;
- prepared messages for "Share message": `savePreparedInlineMessage`;
- request ids for "Request chat": `savePreparedKeyboardButton`;
- custom emoji ids for "Set emoji status".

## Structure

- `composeApp/src/commonMain`: the shared catalog (`Catalog.kt`), UI (`ShowcaseApp.kt`) and the host contract (`ShowcasePlatform`). No Telegram dependency.
- `composeApp/src/webMain`: the Telegram host. `TelegramShowcasePlatform` runs every feature with `tg-mini-app` and logs every `WebAppEvent`. Opened outside Telegram, the page shows the catalog with every feature unavailable.
- `composeApp/src/iosMain` and `androidApp`: native hosts that show the same catalog, with every feature marked "Available only inside Telegram".
- `e2e`: Playwright tests that run the web build in a Telegram-like environment.

## Run

All commands are run from the repository root.

### Web

```bash
./gradlew -p samples/showcase :composeApp:wasmJsBrowserDevelopmentRun
```

```bash
./gradlew -p samples/showcase :composeApp:jsBrowserDevelopmentRun
```

Production bundle with Wasm and an automatic JS fallback, written to `composeApp/build/dist/composeWebCompatibility/productionExecutable`:

```bash
./gradlew -p samples/showcase :composeApp:composeCompatibilityBrowserDistribution
```

Point the bot's Mini App to the deployed bundle and open it from Telegram.

### E2E tests

```bash
./gradlew -p samples/showcase :composeApp:composeCompatibilityBrowserDistribution :composeApp:jsBrowserDistribution
```

```bash
cd samples/showcase/e2e && npm ci && npx playwright install chromium && npx playwright test
```

### Android

Set `ANDROID_HOME` or create `samples/showcase/local.properties` with `sdk.dir=/absolute/path/to/Android/sdk`. The sample compiles against Android API 37.

```bash
./gradlew -p samples/showcase :androidApp:assembleDebug
```

### iOS

```bash
./gradlew -p samples/showcase :composeApp:linkDebugFrameworkIosSimulatorArm64
```

The framework is called `Showcase`; its entry point is `MainViewControllerKt.MainViewController()` in
`composeApp/src/iosMain/kotlin/com/kirillnay/tgminiapp/samples/showcase/MainViewController.kt`.
Use `:composeApp:embedAndSignAppleFrameworkForXcode` from an Xcode host app.
