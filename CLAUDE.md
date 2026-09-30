# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`tg-mini-app` (Maven: `io.github.kirillNay:tg-mini-app`) is a Kotlin Multiplatform library for building Telegram Mini Apps with Compose Multiplatform. It targets the browser only, with two targets, **`js` and `wasmJs`**, that share one source set, `src/webMain`. Kotlin and Compose versions are pinned in `gradle.properties` (`kotlin.version`, `compose.version`) and read by `settings.gradle.kts`. The wrapper covers the Telegram WebApp API up to Bot API 10.1.

## Commands

Library (from repo root):

```bash
./gradlew build                      # compile js + wasmJs and run browser tests
./gradlew jsBrowserTest              # tests on the js target only
./gradlew wasmJsBrowserTest          # tests on the wasmJs target only
./gradlew publishToMavenLocal        # try the library in another local project
```

Tests live in `src/webTest` and run in headless Chrome through Karma. They use a hand-written `window.Telegram.WebApp` mock (`TelegramMock.kt`), not the real script. To run a single test, add `--tests "com.kirillNay.telegram.miniapp.WebAppTest.<name>"`.

If a build fails with "Lock file was changed", run `./gradlew kotlinUpgradeYarnLock`. Maven Central sometimes answers 403 from this machine; rerunning the build fixes it.

Sample app (`samples/coffee-order-demo`, a separate Gradle build; run from repo root):

```bash
./gradlew -p samples/coffee-order-demo :composeApp:wasmJsBrowserDevelopmentRun
./gradlew -p samples/coffee-order-demo :composeApp:composeCompatibilityBrowserDistribution   # what CI deploys
./gradlew -p samples/coffee-order-demo :androidApp:assembleDebug
./gradlew -p samples/coffee-order-demo :composeApp:linkDebugFrameworkIosSimulatorArm64
```

The sample's `settings.gradle.kts` does `includeBuild("../..")`, so its `io.github.kirillNay:tg-mini-app` dependency is **substituted with the local library source**.

To check the web sample in a Mini App context without Telegram:
1. Serve `build/dist/composeWebCompatibility/productionExecutable`.
2. Open it with a URL hash of `tgWebAppData=...&tgWebAppVersion=8.0&tgWebAppThemeParams=...`, then reload. `telegram-web-app.js` reads its launch parameters from the hash.
3. To fire client events, call `Telegram.WebView.receiveEvent('back_button_pressed')` or `receiveEvent('theme_changed', {...})`.

## Library architecture

Packages live under `src/webMain/kotlin/com/kirillNay/telegram/miniapp/`.

**`webApp/internal`** holds the raw interop and is the only place that touches JS. Kotlin/Wasm has no `dynamic` and limits `js()` calls, so all interop goes through a few patterns:
- **`Externals.kt`**: `internal external interface …Js : JsAny`, with member names that match telegram-web-app.js exactly (snake_case included).
- **`Interop.kt`**: top-level `js("...")` helpers (`jsGet`, `jsTypeOf`, `jsFunction0/1`) and the `jsObject { put(...) }` builder, which skips `null` values.
- **`Callbacks.kt`**: `awaitResult` / `awaitValue` turn JS callbacks into suspend calls.
- **`CallbackRegistry`**: keeps the JS function created for each Kotlin lambda so that `offClick(lambda)` removes the same function `onClick` added.

Pitfalls here:
- The helpers are named `asStringOrNull` / `asDoubleOrNull` and so on, never `toXxxOrNull`. On the js target `JsString` *is* `String`, so a `toDoubleOrNull` extension on `JsAny?` shadows the stdlib one and recurses forever.
- Declare optional JS booleans as `JsAny?` and read them with `asBooleanOrNull()`. On wasm an undefined `Boolean?` external property comes back as `false`, not `null`.
- `initDataUnsafe` fields that come from the query string (`auth_date`, `can_send_after`) are strings at runtime. JSON fields such as `user.id` are numbers.

**`webApp`** is the public API. It is made of plain Kotlin classes and data classes that wrap the internal externals; no external types leak out.
- **Entry points:** `val webApp` is a lazy global that throws if the script is missing. `WebApp.getOrNull()`, `WebApp.isAvailable` and `WebApp.isRunningInTelegram` let callers check first.
- **Events:** the sealed `WebAppEvent<T>` has one object per JS event and parses that event's payload. `webApp.onEvent(event) { payload -> }` returns an `EventSubscription`.
- **Callbacks vs. suspend:** a method that takes an optional callback mirrors the JS API and has an `await*` suspend twin, e.g. `showConfirm` / `awaitConfirm`. Storages, `BiometricManager`, the sensors and `LocationManager` are suspend-only. Storage methods return `Result` and fail with `WebAppException`.
- **Documentation:** every member carries KDoc marking its minimum version (`Bot API x.y+`), taken from https://core.telegram.org/bots/webapps. Keep that convention.

**`compose`** is a thin Compose layer.
- `telegramWebApp(fallback) { style -> }` renders through `ComposeViewport` and keeps a `TelegramStyle` (viewport, colors, color scheme, safe area `PaddingValues`) up to date through a `DisposableEffect` subscription. The style is also exposed as `LocalTelegramStyle`.
- `runWhenComposeReady` is `expect`/`actual`: the js target waits for `onWasmReady` (Skiko), while wasmJs renders immediately.
- The js target keeps `binaries.executable()`. Compose requires it (CMP-4906).

## Sample architecture

`samples/coffee-order-demo` shows how consumers should structure an app.
- **`composeApp`** is a KMP *library* (`com.android.kotlin.multiplatform.library`, as required by AGP 9) for Android, iOS, js and wasmJs. All UI and state live in `commonMain` behind the `PlatformAppBridge` interface.
- **`webMain`** is the only place that depends on `tg-mini-app`: `Main.kt` and `TelegramPlatformBridge`.
- **`androidApp`** holds the Android application (`MainActivity`). The sample's root `build.gradle.kts` declares all plugins with `apply false` so both modules share build services.

## Versioning and CI

- **Library version** is the literal `version = "x.y.z"` line in the root `build.gradle.kts`. `scripts/ci/read-version.sh` parses that exact format. When bumping, also update the README badge and install snippet, and the sample's dependency version.
- **Publishing** uses `com.vanniktech.maven.publish` (Central Portal).
  - Credentials come from the Gradle properties `mavenCentralUsername`, `mavenCentralPassword`, `signingInMemoryKey`, `signingInMemoryKeyId` and `signingInMemoryKeyPassword`.
  - CI maps the `SONATYPE_*` and `SIGNING_*` GitHub secrets onto them as `ORG_GRADLE_PROJECT_*` environment variables.
  - Signing is enabled only when `signingInMemoryKey` is set.
- **A push to `master`** publishes (`publish-library.yml`) only if the version increased over the previous commit (`scripts/ci/validate-version-bump.sh`). `manual-publish-library.yml` publishes unconditionally.
- **A push to `develop`** builds the sample's `composeCompatibilityBrowserDistribution`, smoke-tests it, and deploys it to GitHub Pages.
- **Branches:** development happens on `develop`; PRs target `master`.
