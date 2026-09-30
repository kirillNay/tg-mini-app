package com.kirillNay.telegram.miniapp

import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.internal.WebAppJs

/**
 * A Proxy-based Telegram.WebApp that records every method call as `path(args)`, e.g. `MainButton.setText(["Pay"])`.
 * Arguments are serialized with JSON.stringify, so functions become null.
 * If a result is configured for a path with [respondWith], the last function argument (the callback) is invoked with it.
 */
internal class RecordingMock {

    val js: JsAny = createRecordingProxy()

    val webApp: WebApp = WebApp(js.unsafeCast<WebAppJs>())

    /** Makes the callback of [path] receive the arguments encoded as a JSON array. */
    fun respondWith(path: String, argumentsJson: String) = setResult(js, path, argumentsJson)

    fun calls(): List<String> = List(callCount(js)) { callAt(js, it) }

    fun lastCall(): String = calls().last()
}

private fun createRecordingProxy(): JsAny = js(
    """(function () {
        var calls = [];
        var results = {};
        function make(path) {
            return new Proxy(function () {}, {
                get: function (target, prop) {
                    if (prop === '__calls') return calls;
                    if (prop === '__results') return results;
                    if (typeof prop === 'symbol' || prop === 'then' || prop === 'toJSON') return undefined;
                    return make(path ? path + '.' + prop : prop);
                },
                apply: function (target, thisArg, args) {
                    calls.push(path + '(' + JSON.stringify(args) + ')');
                    var callbacks = args.filter(function (a) { return typeof a === 'function'; });
                    var callback = callbacks[callbacks.length - 1];
                    if (callback && results[path]) callback.apply(null, results[path]);
                    return undefined;
                }
            });
        }
        return make('');
    })()"""
)

private fun setResult(mock: JsAny, path: String, argumentsJson: String) {
    js("mock.__results[path] = JSON.parse(argumentsJson);")
}

private fun callCount(mock: JsAny): Int = js("mock.__calls.length")

private fun callAt(mock: JsAny, index: Int): String = js("mock.__calls[index]")
