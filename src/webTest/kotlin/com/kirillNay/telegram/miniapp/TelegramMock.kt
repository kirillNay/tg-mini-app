package com.kirillNay.telegram.miniapp

import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.internal.WebAppJs

/**
 * A minimal stand-in for window.Telegram.WebApp. `_emit(type, payload)` fires event handlers,
 * `_count(type)` returns the number of registered handlers, `_last` keeps the last arguments of some methods.
 */
internal fun createTelegramMock(): JsAny = js(
    """(function () {
        var handlers = {};
        function list(t) { return handlers[t] || (handlers[t] = []); }
        function button(eventType) {
            return {
                isVisible: false,
                onClick: function (f) { list(eventType).push(f); },
                offClick: function (f) { var l = list(eventType); var i = l.indexOf(f); if (i >= 0) l.splice(i, 1); },
                show: function () { this.isVisible = true; },
                hide: function () { this.isVisible = false; }
            };
        }
        var storage = { v: 'stored' };
        var mock = {
            initData: 'query_id=AAA&auth_date=1700000000',
            initDataUnsafe: {
                query_id: 'AAA',
                auth_date: '1700000000',
                can_send_after: '5',
                user: { id: 123456789012, first_name: 'Ann', username: 'ann', is_premium: true }
            },
            version: '8.0',
            platform: 'tdesktop',
            colorScheme: 'dark',
            themeParams: { bg_color: '#112233', button_color: '#445566' },
            viewportHeight: 600,
            viewportStableHeight: 580,
            isExpanded: true,
            isClosingConfirmationEnabled: false,
            safeAreaInset: { top: 10, bottom: 20, left: 0, right: 0 },
            _last: {},
            _emit: function (t, p) { list(t).slice().forEach(function (h) { h(p); }); },
            _count: function (t) { return list(t).length; },
            onEvent: function (t, h) { list(t).push(h); },
            offEvent: function (t, h) { var l = list(t); var i = l.indexOf(h); if (i >= 0) l.splice(i, 1); },
            openLink: function (url, options) { mock._last.openLink = options; },
            showPopup: function (params, cb) { mock._last.showPopup = params; cb('ok_id'); },
            showConfirm: function (message, cb) { cb(true); },
            BackButton: button('backButtonClicked'),
            CloudStorage: {
                getItem: function (key, cb) { if (key === 'bad') { cb('STORAGE_KEY_INVALID'); } else { cb(null, storage[key] || ''); } },
                getItems: function (keys, cb) { var r = {}; keys.forEach(function (k) { r[k] = storage[k] || ''; }); cb(null, r); },
                getKeys: function (cb) { cb(null, Object.keys(storage)); }
            }
        };
        return mock;
    })()"""
)

internal fun JsAny.toWebApp(): WebApp = WebApp(unsafeCast<WebAppJs>())

internal fun emit(mock: JsAny, type: String, payload: JsAny?) {
    js("mock._emit(type, payload);")
}

internal fun handlerCount(mock: JsAny, type: String): Int = js("mock._count(type)")

internal fun lastArgument(mock: JsAny, method: String): JsAny? = js("mock._last[method]")

internal fun eventPayload(json: String): JsAny = js("JSON.parse(json)")
