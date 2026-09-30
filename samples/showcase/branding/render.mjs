// Renders the SVG sources to PNG with the Playwright Chromium used by the E2E tests.
// Run from this directory: node render.mjs
import { createRequire } from 'node:module';
import { readFileSync } from 'node:fs';

const require = createRequire(new URL('../e2e/package.json', import.meta.url));
const { chromium } = require('@playwright/test');

const outputs = [
  { source: 'avatar.svg', file: 'avatar-640.png', width: 640, height: 640, scale: 1 },
  { source: 'banner.svg', file: 'banner-640x360.png', width: 1280, height: 720, scale: 0.5 },
  { source: 'banner.svg', file: 'banner-1280x720.png', width: 1280, height: 720, scale: 1 },
];

const browser = await chromium.launch();
for (const { source, file, width, height, scale } of outputs) {
  const page = await browser.newPage({ viewport: { width, height }, deviceScaleFactor: scale });
  const svg = readFileSync(new URL(source, import.meta.url), 'utf8');
  await page.setContent(`<html><body style="margin:0">${svg}</body></html>`);
  await page.evaluate(() => document.fonts.ready);
  await page.waitForTimeout(300);
  await page.screenshot({ path: file, clip: { x: 0, y: 0, width, height } });
  await page.close();
  console.log(`${file}: ${Math.round(width * scale)}x${Math.round(height * scale)}`);
}
await browser.close();
