#!/usr/bin/env bash
# Full verification used by CI and by the maintenance agents:
# library build with js + wasmJs unit and Compose UI tests, sample builds, Playwright E2E of the web sample.
# Set SKIP_ANDROID=1 to skip the Android sample build, E2E_INSTALL_DEPS=1 to install Playwright system deps (Linux CI).
set -euo pipefail

cd "$(dirname "$0")/../.."

echo "::group::Library build and browser tests"
./gradlew --no-daemon build
echo "::endgroup::"

sample_tasks=(:composeApp:composeCompatibilityBrowserDistribution :composeApp:jsBrowserDistribution)
if [[ "${SKIP_ANDROID:-0}" != "1" ]]; then
    sample_tasks+=(:androidApp:assembleDebug)
fi

echo "::group::Sample builds"
./gradlew --no-daemon -p samples/showcase "${sample_tasks[@]}"
echo "::endgroup::"

echo "::group::Web sample E2E"
(
    cd samples/showcase/e2e
    npm ci
    if [[ "${E2E_INSTALL_DEPS:-0}" == "1" ]]; then
        npx playwright install --with-deps chromium
    else
        npx playwright install chromium
    fi
    npx playwright test
)
echo "::endgroup::"
