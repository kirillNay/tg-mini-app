import { expect, test } from '@playwright/test';
import { button, openInTelegram, pressBack, openScreen, postedEvents, receiveEvent, scrollAndTap, scrollTo, waitForHome, waitForPostedEvent } from './telegram';

test('lists the catalog outside Telegram and explains that features need Telegram', async ({ page }) => {
  await page.goto('/');
  await waitForHome(page);

  await expect(page.getByText(/0 of \d+ features are available on Web browser/)).toBeVisible();
  await openScreen(page, 'Popups and input');
  await expect(page.getByText(/Available only inside Telegram/).first()).toBeVisible();
  await scrollAndTap(page, button(page, 'Show alert'));
  await expect(page.getByText('Waiting for Telegram…')).toHaveCount(0);
});

test('initializes the Mini App', async ({ page }) => {
  await openInTelegram(page);

  await expect(page.getByText(/features are available on Telegram \(android\)/)).toBeVisible();
  await waitForPostedEvent(page, 'web_app_ready');
  await waitForPostedEvent(page, 'web_app_expand');
});

test('uses the Telegram back button for navigation', async ({ page }) => {
  await openInTelegram(page);

  await openScreen(page, 'Haptic feedback');
  await waitForPostedEvent(page, 'web_app_setup_back_button', (d) => d.is_visible === true);
  await expect(button(page, 'Play impact')).toBeVisible();

  await receiveEvent(page, 'back_button_pressed');
  await expect(page.getByText(/features are available on Telegram/)).toBeVisible();
  await waitForPostedEvent(page, 'web_app_setup_back_button', (d) => d.is_visible === false);
});

test('plays haptic feedback', async ({ page }) => {
  await openInTelegram(page);
  await openScreen(page, 'Haptic feedback');

  await scrollAndTap(page, button(page, 'Play impact'));
  await waitForPostedEvent(page, 'web_app_trigger_haptic_feedback', (d) => d.type === 'impact' && d.impact_style === 'medium');
  await scrollAndTap(page, button(page, 'Play notification'));
  await waitForPostedEvent(page, 'web_app_trigger_haptic_feedback', (d) => d.type === 'notification' && d.notification_type === 'success');
});

test('shows a confirmation popup and reports the answer', async ({ page }) => {
  await openInTelegram(page);
  await openScreen(page, 'Popups and input');

  await scrollAndTap(page, button(page, 'Show confirm'));
  const popup = await waitForPostedEvent(page, 'web_app_open_popup');
  expect(popup.data.message).toBe('Do you like this showcase?');
  const ok = popup.data.buttons.find((b: any) => b.type === 'ok');
  await receiveEvent(page, 'popup_closed', { button_id: ok.id });

  await expect(page.getByText('Confirmed: OK')).toBeVisible();
});

test('controls the main button and logs its presses', async ({ page }) => {
  await openInTelegram(page);
  await openScreen(page, 'Buttons');

  await scrollAndTap(page, button(page, 'Apply main button'));
  await waitForPostedEvent(page, 'web_app_setup_main_button', (d) => d.is_visible === true && d.text === 'Main action');
  await receiveEvent(page, 'main_button_pressed');

  await pressBack(page);
  await openScreen(page, 'Event log');
  await expect(page.getByText(/mainButtonClicked/)).toBeVisible();
});

test('reads a value from CloudStorage', async ({ page }) => {
  await openInTelegram(page);
  await openScreen(page, 'Cloud storage');

  await scrollAndTap(page, button(page, 'Cloud: get item'));
  const request = await waitForPostedEvent(page, 'web_app_invoke_custom_method', (d) => d.method === 'getStorageValues');
  expect(request.data.params.keys).toEqual(['showcase_key']);
  await receiveEvent(page, 'custom_method_invoked', { req_id: request.data.req_id, result: { showcase_key: 'Hello' } });

  await expect(page.getByText('Value: "Hello"')).toBeVisible();
});

test('marks features newer than the Telegram app as unavailable', async ({ page }) => {
  await openInTelegram(page, { version: '6.0' });

  await expect(page.getByText(/features are available on Telegram/)).toBeVisible();
  await openScreen(page, 'App behavior');
  await scrollTo(page, page.getByText(/Requires Bot API 7.7, this Telegram app supports 6.0/));

  await scrollAndTap(page, button(page, 'Apply vertical swipes'));
  await scrollAndTap(page, button(page, 'Expand app'));
  await waitForPostedEvent(page, 'web_app_expand', () => true);
  const types = (await postedEvents(page)).map((e) => e.type);
  expect(types).not.toContain('web_app_setup_swipe_behavior');
});

test('shows launch data and follows theme changes', async ({ page }) => {
  await openInTelegram(page);
  await openScreen(page, 'Launch data');
  await scrollTo(page, page.getByText('@kirill_test'));

  await receiveEvent(page, 'theme_changed', { theme_params: { bg_color: '#fafafa', text_color: '#000000', secondary_bg_color: '#f1f1f1' } });

  await scrollTo(page, page.getByText('#fafafa'));
});

test('does not post errors to the console', async ({ page }) => {
  const errors: string[] = [];
  page.on('pageerror', (e) => errors.push(e.message));
  page.on('console', (m) => { if (m.type() === 'error') errors.push(m.text()); });

  await openInTelegram(page);
  await expect(page.getByText(/features are available on Telegram/)).toBeVisible();

  expect(errors).toEqual([]);
  expect((await postedEvents(page)).length).toBeGreaterThan(0);
});
