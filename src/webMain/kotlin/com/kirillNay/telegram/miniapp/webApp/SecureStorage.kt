package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.SecureStorageJs
import com.kirillNay.telegram.miniapp.webApp.internal.isNullOrUndefined
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.awaitResult
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Bot API 9.0+
 *
 * Secure storage on the user's device for sensitive data: the Keychain on iOS and the Keystore on Android.
 * Each bot can store up to 10 items per user.
 *
 * Every method returns [Result.failure] with a [WebAppException] if Telegram reports an error.
 */
class SecureStorage internal constructor(
    private val js: SecureStorageJs,
) {

    /** Stores [value] under [key]. The result tells whether the value was stored. */
    suspend fun setItem(key: String, value: String): Result<Boolean> =
        awaitResult({ js.setItem(key, value, it) }) { it.isTrue() }

    /**
     * Receives the value stored under [key]. If the key was not found, [Item.value] is `null` and
     * [Item.canRestore] tells whether it can be restored from the current device with [restoreItem].
     */
    suspend fun getItem(key: String): Result<Item> = suspendCoroutine { continuation ->
        try {
            js.getItem(key) { error, value, canRestore ->
                val outcome = if (error.isNullOrUndefined()) {
                    Result.success(Item(value = value.asStringOrNull(), canRestore = canRestore.isTrue()))
                } else {
                    Result.failure(WebAppException(error.asStringOrNull().orEmpty()))
                }
                continuation.resume(outcome)
            }
        } catch (e: Throwable) {
            continuation.resume(Result.failure(e))
        }
    }

    /**
     * Attempts to restore a key that previously existed on the current device. The user is asked for permission.
     * The result contains the restored value.
     */
    suspend fun restoreItem(key: String): Result<String?> =
        awaitResult({ js.restoreItem(key, it) }) { it.asStringOrNull() }

    /** Removes the value stored under [key]. The result tells whether the value was removed. */
    suspend fun removeItem(key: String): Result<Boolean> =
        awaitResult({ js.removeItem(key, it) }) { it.isTrue() }

    /** Clears all keys previously stored by the bot. The result tells whether all values were removed. */
    suspend fun clear(): Result<Boolean> =
        awaitResult({ js.clear(it) }) { it.isTrue() }

    /** Result of [getItem]. */
    data class Item(
        val value: String?,
        val canRestore: Boolean,
    )
}
