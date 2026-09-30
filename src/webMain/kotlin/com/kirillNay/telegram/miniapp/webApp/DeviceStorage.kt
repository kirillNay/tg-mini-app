package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.DeviceStorageJs
import com.kirillNay.telegram.miniapp.webApp.internal.awaitResult
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull

/**
 * Bot API 9.0+
 *
 * Persistent local storage on the user's device, similar to the browser's localStorage but integrated within the Telegram client.
 * Data is available only to the bot that created it. Each bot can store up to 5 MB per user.
 *
 * Every method returns [Result.failure] with a [WebAppException] if Telegram reports an error.
 */
class DeviceStorage internal constructor(
    private val js: DeviceStorageJs,
) {

    /** Stores [value] under [key]. The result tells whether the value was stored. */
    suspend fun setItem(key: String, value: String): Result<Boolean> =
        awaitResult({ js.setItem(key, value, it) }) { it.isTrue() }

    /** Receives the value stored under [key], or `null` if there is none. */
    suspend fun getItem(key: String): Result<String?> =
        awaitResult({ js.getItem(key, it) }) { it.asStringOrNull() }

    /** Removes the value stored under [key]. The result tells whether the value was removed. */
    suspend fun removeItem(key: String): Result<Boolean> =
        awaitResult({ js.removeItem(key, it) }) { it.isTrue() }

    /** Clears all keys previously stored by the bot. The result tells whether all values were removed. */
    suspend fun clear(): Result<Boolean> =
        awaitResult({ js.clear(it) }) { it.isTrue() }
}
