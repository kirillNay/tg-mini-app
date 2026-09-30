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
 * so the click must reach the canvas as a real pointer event.
 */
export async function tap(page: Page, locator: Locator) {
  await expect(locator).toBeVisible();
  const box = (await locator.boundingBox())!;
  await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
}
