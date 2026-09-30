import { expect, Locator, Page } from '@playwright/test';

export type PostedEvent = { type: string; data: any };

export const defaultUser = { id: 123456789012, first_name: 'Kirill', username: 'kirill_test', is_premium: true };

export const darkTheme = {
  bg_color: '#17212b',
  text_color: '#f5f5f5',
  hint_color: '#708499',
  link_color: '#6ab3f3',
  button_color: '#5288c1',
  button_text_color: '#ffffff',
  secondary_bg_color: '#232e3c',
};

/**
 * Opens the sample the way a Telegram client does: launch parameters in the URL hash and a
 * TelegramWebviewProxy that receives every `postEvent` of telegram-web-app.js.
 */
export async function openInTelegram(
  page: Page,
  options: { version?: string; theme?: Record<string, string>; user?: object } = {},
) {
  await page.addInitScript(() => {
    (window as any).__tgEvents = [];
    (window as any).TelegramWebviewProxy = {
      postEvent(type: string, data: string) {
        (window as any).__tgEvents.push({ type, data: data ? JSON.parse(data) : null });
      },
    };
  });
  const initData = new URLSearchParams({
    query_id: 'AAH-e2e',
    user: JSON.stringify(options.user ?? defaultUser),
    auth_date: '1700000000',
    hash: 'e2e',
  }).toString();
  const hash = new URLSearchParams({
    tgWebAppData: initData,
    tgWebAppVersion: options.version ?? '8.0',
    tgWebAppPlatform: 'android',
    tgWebAppThemeParams: JSON.stringify(options.theme ?? darkTheme),
  }).toString();
  await page.goto(`/#${hash}`);
  await waitForHome(page);
}

/** Waits until the showcase has rendered its home screen. */
export async function waitForHome(page: Page) {
  await expect(page.getByText(/features are available on/)).toBeVisible();
}

export async function postedEvents(page: Page): Promise<PostedEvent[]> {
  return page.evaluate(() => (window as any).__tgEvents as PostedEvent[]);
}

/** Waits until the Mini App posts an event of [type] whose data matches [predicate], and returns it. */
export async function waitForPostedEvent(
  page: Page,
  type: string,
  predicate: (data: any) => boolean = () => true,
): Promise<PostedEvent> {
  let found: PostedEvent | undefined;
  await expect
    .poll(async () => {
      found = (await postedEvents(page)).filter((e) => e.type === type && predicate(e.data)).pop();
      return found !== undefined;
    }, { message: `Mini App should post ${type}` })
    .toBe(true);
  return found!;
}

/** Delivers an event from the "Telegram client" to the Mini App. */
export async function receiveEvent(page: Page, type: string, data?: object) {
  await page.evaluate(([t, d]) => (window as any).Telegram.WebView.receiveEvent(t, d), [type, data] as const);
}

export function button(page: Page, name: string) {
  return page.getByRole('button', { name, exact: true });
}

/**
 * Clicks the center of an element from the Compose accessibility tree. The tree only mirrors the canvas,
 * so the click must reach the canvas as a real pointer event. Playwright's visibility checks are unreliable
 * for the mirror while the canvas re-renders, so this waits for a stable on-screen bounding box instead.
 */
export async function tap(page: Page, locator: Locator) {
  const viewport = page.viewportSize()!;
  let previous: { x: number; y: number } | null = null;
  for (let attempt = 0; attempt < 100; attempt++) {
    const box = (await locator.count()) > 0 ? await locator.first().boundingBox() : null;
    const onScreen = box !== null && box.height > 0 && box.y >= 0 && box.y + box.height <= viewport.height;
    if (onScreen && previous && previous.x === box!.x && previous.y === box!.y) {
      await page.mouse.click(box!.x + box!.width / 2, box!.y + box!.height / 2);
      return;
    }
    previous = onScreen ? { x: box!.x, y: box!.y } : null;
    await page.waitForTimeout(100);
  }
  throw new Error(`Element to tap is not on screen: ${locator}`);
}

/**
 * Scrolls the Compose canvas until [locator] is in the accessibility tree and inside the viewport.
 * Lazy lists only compose visible items, so off-screen items don't exist in the tree yet.
 */
export async function scrollTo(page: Page, locator: Locator) {
  const viewport = page.viewportSize()!;
  let direction = 1;
  for (let attempt = 0; attempt < 60; attempt++) {
    if ((await locator.count()) > 0) {
      const box = await locator.first().boundingBox();
      if (box && box.y >= 0 && box.y + box.height <= viewport.height) return;
      if (box) direction = box.y < 0 ? -1 : 1;
    } else if (attempt === 30) {
      // Not composed while scrolling down: the item may be above.
      direction = -1;
    }
    await page.mouse.move(viewport.width / 2, viewport.height / 2);
    await page.mouse.wheel(0, 250 * direction);
    await page.waitForTimeout(80);
  }
  await expect(locator.first()).toBeInViewport();
}

/** Scrolls to [locator] and taps it. */
export async function scrollAndTap(page: Page, locator: Locator) {
  await scrollTo(page, locator);
  await tap(page, locator.first());
}

/** Opens a section or screen from the home list by its title. */
export async function openScreen(page: Page, title: string) {
  await scrollAndTap(page, page.getByRole('button', { name: new RegExp(`^${title}`) }));
}

/** Presses Telegram's back button once the Mini App has shown it. */
export async function pressBack(page: Page) {
  await waitForPostedEvent(page, 'web_app_setup_back_button', (d) => d.is_visible === true);
  await receiveEvent(page, 'back_button_pressed');
  await waitForHome(page);
}
