package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.CloudStorageJs
import com.kirillNay.telegram.miniapp.webApp.internal.awaitResult
import com.kirillNay.telegram.miniapp.webApp.internal.jsGet
import com.kirillNay.telegram.miniapp.webApp.internal.jsObjectKeys
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.toJsStringArray
import com.kirillNay.telegram.miniapp.webApp.internal.toStringList
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull

/**
 * Bot API 6.9+
 *
 * Cloud storage of the bot. Each bot can store up to 1024 items per user.
 *
 * Keys should contain 1-128 characters, only A-Z, a-z, 0-9, _ and - are allowed. Values should contain 0-4096 characters.
 * Every method returns [Result.failure] with a [WebAppException] if Telegram reports an error.
 */
class CloudStorage internal constructor(
    private val js: CloudStorageJs,
) {

    /** Stores [value] under [key]. The result tells whether the value was stored. */
    suspend fun setItem(key: String, value: String): Result<Boolean> =
        awaitResult({ js.setItem(key, value, it) }) { it.isTrue() }

    /** Receives the value stored under [key]. Telegram returns an empty string for a missing key. */
    suspend fun getItem(key: String): Result<String> =
        awaitResult({ js.getItem(key, it) }) { it.asStringOrNull().orEmpty() }

    /** Receives the values stored under [keys]. */
    suspend fun getItems(keys: List<String>): Result<Map<String, String>> =
        awaitResult({ js.getItems(keys.toJsStringArray(), it) }) { result ->
            if (result == null) {
                emptyMap()
            } else {
                jsObjectKeys(result).toStringList().associateWith { key -> jsGet(result, key).asStringOrNull().orEmpty() }
            }
        }

    /** Receives the values stored under [keys]. */
    suspend fun getItems(vararg keys: String): Result<Map<String, String>> = getItems(keys.asList())

    /** Removes the value stored under [key]. The result tells whether the value was removed. */
    suspend fun removeItem(key: String): Result<Boolean> =
        awaitResult({ js.removeItem(key, it) }) { it.isTrue() }

    /** Removes the values stored under [keys]. The result tells whether the values were removed. */
    suspend fun removeItems(keys: List<String>): Result<Boolean> =
        awaitResult({ js.removeItems(keys.toJsStringArray(), it) }) { it.isTrue() }

    /** Removes the values stored under [keys]. The result tells whether the values were removed. */
    suspend fun removeItems(vararg keys: String): Result<Boolean> = removeItems(keys.asList())

    /** Receives the list of all keys stored in the cloud storage. */
    suspend fun getKeys(): Result<List<String>> =
        awaitResult({ js.getKeys(it) }) { result ->
            result?.unsafeCast<JsArray<JsString>>()?.toStringList().orEmpty()
        }
}
