package com.kirillNay.telegram.miniapp.webApp.internal

import com.kirillNay.telegram.miniapp.webApp.WebAppException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Adapts a node-style `callback(error, result)` API of telegram-web-app.js (storages) to a suspend call.
 * Synchronous exceptions, such as `WebAppMethodUnsupported` on old clients, are also returned as a failure.
 */
internal suspend fun <T> awaitResult(
    start: (callback: (error: JsAny?, result: JsAny?) -> Unit) -> Unit,
    map: (JsAny?) -> T,
): Result<T> = suspendCoroutine { continuation ->
    try {
        start { error, result ->
            val outcome = if (error.isNullOrUndefined()) {
                runCatching { map(result) }
            } else {
                Result.failure(WebAppException(error.asStringOrNull().orEmpty()))
            }
            continuation.resume(outcome)
        }
    } catch (e: Throwable) {
        continuation.resume(Result.failure(e))
    }
}

/** Adapts a `callback(value)` API of telegram-web-app.js to a suspend call. */
internal suspend fun <T> awaitValue(
    start: (callback: (value: JsAny?) -> Unit) -> Unit,
    map: (JsAny?) -> T,
): T = suspendCoroutine { continuation ->
    start { value -> continuation.resume(map(value)) }
}
