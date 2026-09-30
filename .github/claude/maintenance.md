# Monthly maintenance of tg-mini-app

You are the maintenance agent of this repository. Read `CLAUDE.md` first: it describes the architecture,
interop pitfalls and conventions you must follow. Work on the current checkout of `develop`.

Your goal is one pull request into `develop` that brings the library and the sample up to date and is ready
to be released, or no pull request at all if nothing needs to change.

## 1. Find what is outdated

Record the current value and the latest **stable** release (no alpha/beta/RC/milestone/dev) of each item.
Use primary sources (`curl` on Maven metadata, `gh api` on GitHub releases, official docs), never memory.

- Kotlin (`kotlin.version` in both `gradle.properties`): https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/maven-metadata.xml
- Compose Multiplatform (`compose.version`): https://repo1.maven.org/maven2/org/jetbrains/compose/compose-gradle-plugin/maven-metadata.xml
- Compose Material3 for the sample (`compose.material3.version`): https://repo1.maven.org/maven2/org/jetbrains/compose/material3/material3/maven-metadata.xml
- Gradle wrapper (root and sample): https://services.gradle.org/versions/current
- Android Gradle Plugin (sample): https://dl.google.com/android/maven2/com/android/tools/build/gradle/maven-metadata.xml
- kotlinx-coroutines, androidx.activity:activity-compose, com.vanniktech.maven.publish
- `@playwright/test` in `samples/coffee-order-demo/e2e/package.json` (`npm view @playwright/test version`)
- GitHub Actions used in `.github/workflows/*.yml` (`gh api repos/<owner>/<repo>/releases/latest`)
- Telegram Mini Apps: read https://core.telegram.org/bots/webapps ("Recent changes" and all object tables)
  and https://core.telegram.org/bots/api-changelog. The library covers the Bot API version in the README badge.
  List every field, method, event, parameter and object that is newer or missing in `src/webMain`.
  Also diff https://telegram.org/js/telegram-web-app.js against what the code assumes when behavior matters.

Respect compatibility constraints before choosing versions: Compose Multiplatform's required Kotlin version,
AGP's required Gradle and compileSdk, Kotlin/Wasm and Compose web notes in the release notes. Read the
release notes / "What's new" of every version you upgrade to and handle deprecations and breaking changes.

## 2. Apply the updates

- Upgrade versions consistently in the root build and the sample. Run `./gradlew kotlinUpgradeYarnLock`
  (root and sample) when the lock files change.
- Implement new Telegram API following the existing patterns: raw `external interface` members in
  `webApp/internal/Externals.kt`, a public Kotlin wrapper, KDoc with `Bot API x.y+`, events in `WebAppEvent`,
  suspend `await*` variants for callbacks. Every new member needs tests: `WebAppCallsTest` (recording proxy)
  for calls and arguments, `WebAppTest` for parsing, `TelegramWebAppContentTest` for Compose behavior.
- Fix new compiler warnings and deprecations. Do not suppress them unless there is no alternative, and explain why.
- You may fix clear bugs you find on the way, but keep unrelated refactoring out of this PR.
- Never weaken, skip or delete tests to get a green build. If a test is wrong, fix it and explain why in the PR.

## 3. Verify

Run `bash scripts/ci/verify.sh` (set `E2E_INSTALL_DEPS=1`). Everything must pass: library build with js and
wasmJs unit and Compose UI tests, sample builds, Playwright E2E on the wasm and js bundles.
If the E2E flow of the sample changed intentionally, update the tests in `samples/coffee-order-demo/e2e/tests`.

## 4. Version and release notes

Choose the next version with semantic versioning, based on the public API diff and consumer impact:
- **major**: any breaking change of the public API or behavior, or dropping a target.
- **minor**: new public API (e.g. new Bot API coverage), or a Kotlin/Compose upgrade that raises the minimum
  compiler/Compose version consumers need.
- **patch**: internal fixes, build tooling, CI, sample-only changes, patch-level dependency updates.

Then:
- Set `version = "x.y.z"` in the root `build.gradle.kts` (keep this exact line format).
- Update the README badges (library, Kotlin, Compose, Bot API), the install snippet and "Compatibility",
  and the `tg-mini-app` version in `samples/coffee-order-demo/composeApp/build.gradle.kts`.
- Add a section `## [x.y.z] - YYYY-MM-DD` at the top of `CHANGELOG.md` with `Added` / `Changed` / `Fixed` /
  `Dependencies` subsections, written for library users. For a major version add migration notes to the README.

## 5. Open the pull request

- If an open PR labeled `automated-maintenance` already exists, do not open another one: comment on it with
  your findings and stop.
- Create branch `automation/maintenance-<YYYY-MM>` from `develop`, commit with clear messages, push.
- `gh pr create --base develop --label automated-maintenance` with a body containing: summary; table of
  versions old → new with links to release notes; Telegram API changes implemented; bump type and why;
  verification results; anything you could not do and why.
- If nothing needs to change, do not create a branch or PR. Write the checked versions to `$GITHUB_STEP_SUMMARY`.
