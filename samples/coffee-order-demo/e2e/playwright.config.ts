import { defineConfig, devices } from '@playwright/test';

// Serves the built sample. Build it first:
//   ./gradlew -p samples/coffee-order-demo :composeApp:composeCompatibilityBrowserDistribution :composeApp:jsBrowserDistribution
const dist = '../composeApp/build/dist';

export default defineConfig({
  testDir: './tests',
  timeout: 60_000,
  expect: { timeout: 20_000 },
  fullyParallel: true,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [['list'], ['html', { open: 'never' }]] : 'list',
  use: {
    ...devices['Desktop Chrome'],
    viewport: { width: 420, height: 860 },
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
  },
  projects: [
    // The compatibility bundle picks the Wasm build in browsers with WasmGC.
    { name: 'wasm', use: { baseURL: 'http://127.0.0.1:4173' } },
    // Plain Kotlin/JS build, used as the fallback for browsers without WasmGC.
    { name: 'js', use: { baseURL: 'http://127.0.0.1:4174' } },
  ],
  webServer: [
    {
      command: `python3 -m http.server 4173 --bind 127.0.0.1 --directory ${dist}/composeWebCompatibility/productionExecutable`,
      url: 'http://127.0.0.1:4173/',
      reuseExistingServer: !process.env.CI,
    },
    {
      command: `python3 -m http.server 4174 --bind 127.0.0.1 --directory ${dist}/js/productionExecutable`,
      url: 'http://127.0.0.1:4174/',
      reuseExistingServer: !process.env.CI,
    },
  ],
});
