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

Tests live in `src/webTest` and run in headless Chrome through Karma, once per target. To run a single test, add `--tests "com.kirillNay.telegram.miniapp.WebAppTest.<name>"`. There are three kinds:
- **`WebAppCallsTest`** uses `RecordingMock`, a JS Proxy that records every call as `Path.method([json args])` and can answer callbacks (`respondWith`). Every public method should have an assertion here.
- **`WebAppTest`** covers parsing and state on the hand-written `window.Telegram.WebApp` mock in `TelegramMock.kt`.
- **`TelegramWebAppContentTest`** is a set of Compose UI tests of `TelegramWebAppContent`. Use `composeUiTest { }` instead of `runComposeUiTest`: on js it waits for Skiko to load.

`bash scripts/ci/verify.sh` runs everything CI runs: the library build, the sample builds and the Playwright E2E.

If a build fails with "Lock file was changed", run `./gradlew kotlinUpgradeYarnLock`. Maven Central sometimes answers 403 from this machine; rerunning the build fixes it.

Sample app (`samples/showcase`, a separate Gradle build; run from repo root):

```bash
./gradlew -p samples/showcase :composeApp:wasmJsBrowserDevelopmentRun
./gradlew -p samples/showcase :composeApp:composeCompatibilityBrowserDistribution   # what CI deploys
./gradlew -p samples/showcase :androidApp:assembleDebug
./gradlew -p samples/showcase :composeApp:linkDebugFrameworkIosSimulatorArm64
```

The sample's `settings.gradle.kts` does `includeBuild("../..")`, so its `io.github.kirillNay:tg-mini-app` dependency is **substituted with the local library source**.

Playwright E2E tests of the web sample live in `samples/showcase/e2e`. Build the dists first (`:composeApp:composeCompatibilityBrowserDistribution :composeApp:jsBrowserDistribution`), then run `npm ci && npx playwright test`. Each scenario runs against both the wasm and js bundles.
- **Launch:** `openInTelegram` passes launch parameters in the URL hash and installs a `TelegramWebviewProxy` that records every `postEvent` of telegram-web-app.js.
- **Assertions:** tests assert on those posted events and simulate the client with `Telegram.WebView.receiveEvent`.
- **Clicks:** UI elements are found through Compose's accessibility tree (`getByRole`). Click them with `tap()`, which sends a real pointer event after the element stops moving: the canvas intercepts Playwright's normal clicks.
- **Scrolling:** lazy lists compose only visible items, so use `scrollTo` / `scrollAndTap` / `openScreen`. The accessibility tree does not expose disabled buttons; assert unavailability through the note text and the absence of posted events.

To check the web sample by hand in a Mini App context without Telegram:
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

`samples/showcase` is a catalog of every Telegram Mini Apps capability wrapped by the library, and an example of how to structure a multiplatform app around it.
- **`commonMain`** has no Telegram dependency:
  - `Catalog.kt` lists sections and `Feature`s: id, unique action label, minimum Bot API version, inputs.
  - `ShowcaseApp` is the shared UI.
  - `ShowcasePlatform` is the host contract; `OutsideTelegramPlatform` is the host with nothing available.
- **`webMain`** is the only place that depends on `tg-mini-app`. `TelegramShowcasePlatform` runs each feature id with the real API, logs every `WebAppEvent`, collects launch data and gates features by `isVersionAtLeast`. Outside Telegram, `Main.kt` renders the catalog with `OutsideTelegramPlatform` through `telegramWebApp(fallback = ...)`.
- **Android and iOS** (`androidApp`, `iosMain`) show the same catalog with every feature unavailable.
- When the library gains an API, add a `Feature` to `Catalog.kt`, its branch in `TelegramShowcasePlatform.run` and, for key flows, a Playwright scenario.
- **`androidApp`** is a separate module because of AGP 9: `composeApp` is a KMP library (`com.android.kotlin.multiplatform.library`). The sample's root `build.gradle.kts` declares all plugins with `apply false` so both modules share build services.

## Automation (Claude agents in GitHub Actions)

- **`claude-maintenance.yml`** runs monthly or on dispatch. The agent follows `.github/claude/maintenance.md`: it updates dependencies and Telegram API coverage, runs `verify.sh`, bumps the version by semver, writes the `CHANGELOG.md` section and opens a PR into `develop` labeled `automated-maintenance`.
- **`claude-review.yml`** runs on every PR into `develop`. It reviews following `.github/claude/review.md` and returns a structured verdict. For `automated-maintenance` PRs, a fix job follows `.github/claude/fix.md` and pushes `review-fix:` commits, at most 3 rounds. The `Claude review verdict` job fails until a revision is approved.
- **`release-flow.yml`** opens the PRs between branches:
  - when `develop` has a version newer than `master`, it opens "Release x.y.z" `develop → master`;
  - after a release, it opens a sync PR `master → develop`.

  It opens them through claude[bot], because PRs opened with `GITHUB_TOKEN` don't trigger CI.
- **`ci.yml`** is the gate for PRs. It runs the library tests, the web E2E and the Android build; PRs into `master` also get the iOS build.
- **`claude.yml`** answers `@claude` mentions.

The agent workflows need the Claude GitHub App installed and an `ANTHROPIC_API_KEY` or `CLAUDE_CODE_OAUTH_TOKEN` secret. When you change conventions, update the prompts in `.github/claude/`.

## Versioning and CI

- **Library version** is the literal `version = "x.y.z"` line in the root `build.gradle.kts`. `scripts/ci/read-version.sh` parses that exact format. When bumping, also update the README badge and install snippet, the sample's dependency version, and add a `## [x.y.z] - YYYY-MM-DD` section to `CHANGELOG.md`. `scripts/release/changelog-section.sh` extracts that section as the GitHub Release notes.
- **Publishing** uses `com.vanniktech.maven.publish` (Central Portal).
  - Credentials come from the Gradle properties `mavenCentralUsername`, `mavenCentralPassword`, `signingInMemoryKey`, `signingInMemoryKeyId` and `signingInMemoryKeyPassword`.
  - CI maps the `SONATYPE_*` and `SIGNING_*` GitHub secrets onto them as `ORG_GRADLE_PROJECT_*` environment variables.
  - Signing is enabled only when `signingInMemoryKey` is set.
- **A push to `master`** publishes (`publish-library.yml`, environment `maven-central`) only if the version increased over the previous commit (`scripts/ci/validate-version-bump.sh`), then creates the GitHub Release `vX.Y.Z`. `manual-publish-library.yml` publishes unconditionally.
- **A push to `develop`** builds the sample's `composeCompatibilityBrowserDistribution`, smoke-tests it, and deploys it to GitHub Pages.
- **Branches:** development happens on `develop`; releases are squash-merged into `master`, and `master` is then merged back into `develop`.
