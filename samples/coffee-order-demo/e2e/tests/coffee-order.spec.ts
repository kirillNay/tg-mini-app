import { expect, test } from '@playwright/test';
import { button, openInTelegram, postedEvents, receiveEvent, tap, waitForPostedEvent } from './telegram';

test('shows a placeholder outside Telegram', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByText('Telegram runtime not found')).toBeVisible();
  const events = await page.evaluate(() => (window as any).__tgEvents);
  expect(events).toBeUndefined();
});

test('initializes the Mini App and applies the Telegram theme', async ({ page }) => {
  await openInTelegram(page);

  await expect(page.getByText('Telegram dark theme')).toBeVisible();
  await waitForPostedEvent(page, 'web_app_ready');
  await waitForPostedEvent(page, 'web_app_expand');
  await waitForPostedEvent(page, 'web_app_setup_closing_behavior', (d) => d.need_confirmation === true);
  await waitForPostedEvent(page, 'web_app_set_header_color', (d) => d.color_key === 'secondary_bg_color');
  await waitForPostedEvent(page, 'web_app_setup_main_button', (d) => d.is_visible === false);
});

test('shows user data from initData on the settings screen', async ({ page }) => {
  await openInTelegram(page);

  await tap(page, button(page, 'Settings'));

  await expect(page.getByText('Kirill', { exact: true })).toBeVisible();
  await expect(page.getByText('@kirill_test')).toBeVisible();
  await expect(page.getByText('Telegram android / Bot API 8.0')).toBeVisible();
});

test('drives back and main buttons from the detail screen', async ({ page }) => {
  await openInTelegram(page);

  await tap(page, button(page, 'Details').first());
  await waitForPostedEvent(page, 'web_app_setup_back_button', (d) => d.is_visible === true);
  await waitForPostedEvent(page, 'web_app_setup_main_button', (d) => d.is_visible === true && d.text === 'Add $4.70');

  await receiveEvent(page, 'main_button_pressed');
  await waitForPostedEvent(page, 'web_app_trigger_haptic_feedback', (d) => d.type === 'impact' && d.impact_style === 'light');
  await expect(page.getByText('1 items, $4.70')).toBeVisible();

  await receiveEvent(page, 'back_button_pressed');
  await expect(button(page, 'Quick add').first()).toBeVisible();
  await waitForPostedEvent(page, 'web_app_setup_back_button', (d) => d.is_visible === false);
});

test('confirms an order through the native popup', async ({ page }) => {
  await openInTelegram(page);

  await tap(page, button(page, 'Quick add').first());
  await expect(page.getByText('1 items, $4.70')).toBeVisible();
  await tap(page, page.getByRole('button', { name: /^Cart/ }));
  await waitForPostedEvent(page, 'web_app_setup_main_button', (d) => d.is_visible === true && d.text === 'Checkout 1');
  await receiveEvent(page, 'main_button_pressed');
  await waitForPostedEvent(page, 'web_app_setup_main_button', (d) => d.is_visible === true && d.text === 'Confirm $4.70');
  await receiveEvent(page, 'main_button_pressed');

  const popup = await waitForPostedEvent(page, 'web_app_open_popup');
  expect(popup.data.message).toContain('Confirm coffee order?');
  const okButton = popup.data.buttons.find((b: any) => b.type === 'ok');
  await receiveEvent(page, 'popup_closed', { button_id: okButton.id });

  await waitForPostedEvent(page, 'web_app_trigger_haptic_feedback', (d) => d.type === 'notification' && d.notification_type === 'success');
  await expect(page.getByText('0 items, $0.00')).toBeVisible();
});

test('follows theme changes at runtime', async ({ page }) => {
  await openInTelegram(page);
  await expect(page.getByText('Telegram dark theme')).toBeVisible();

  await receiveEvent(page, 'theme_changed', {
    theme_params: { bg_color: '#ffffff', text_color: '#000000', button_color: '#2481cc', secondary_bg_color: '#f1f1f1' },
  });

  await expect(page.getByText('Telegram light theme')).toBeVisible();
});

test('requests the saved note from CloudStorage', async ({ page }) => {
  await openInTelegram(page);

  const request = await waitForPostedEvent(page, 'web_app_invoke_custom_method', (d) => d.method === 'getStorageValues');
  await receiveEvent(page, 'custom_method_invoked', {
    req_id: request.data.req_id,
    result: { coffee_order_demo_note: 'Extra hot please' },
  });
  await tap(page, button(page, 'Settings'));

  await expect(page.getByText('Extra hot please')).toBeVisible();
});

test('does not post unexpected errors to the console', async ({ page }) => {
  const errors: string[] = [];
  page.on('pageerror', (e) => errors.push(e.message));
  page.on('console', (m) => { if (m.type() === 'error') errors.push(m.text()); });

  await openInTelegram(page);
  await expect(page.getByText('Telegram dark theme')).toBeVisible();

  expect(errors).toEqual([]);
  expect((await postedEvents(page)).length).toBeGreaterThan(0);
});
