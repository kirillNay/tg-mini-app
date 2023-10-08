package nay.kirill.telegram_kmp.mini_app.webApp

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * This object controls the cloud storage. Each bot can store up to 1024 items per user in the cloud storage.
 */
class CloudStorage internal constructor(
    private val jsDelegate: CloudStorageJs
) {

    /**
     * Bot API 6.9+
     *
     * A method that stores a value in the cloud storage using the specified key. The key should contain 1-128 characters, only A-Z, a-z, 0-9, _ and - are allowed. The value should contain 0-4096 characters. You can store up to 1024 keys in the cloud storage. If an optional callback parameter was passed, the callback function will be called. In case of an error, the first argument will contain the error. In case of success, the first argument will be null and the second argument will be a boolean indicating whether the value was stored.
     */
    suspend fun setItem(key: String, value: String): Result<Boolean> = suspendCoroutine { continuation ->
        jsDelegate.setItem(key, value) { error, result ->
            if (error != null && error != undefined) {
                continuation.resume(Result.failure(IllegalStateException("Failed to set item: $error")))
            } else {
                continuation.resume(Result.success(result))
            }
        }
    }

    /**
     * Bot API 6.9+
     *
     * A method that receives a value from the cloud storage using the specified key. The key should contain 1-128 characters, only A-Z, a-z, 0-9, _ and - are allowed. In case of an error, the callback function will be called and the first argument will contain the error. In case of success, the first argument will be null and the value will be passed as the second argument.
     */
    suspend fun getItem(key: String): Result<String> = suspendCoroutine { continuation ->
        jsDelegate.getItem(key) { error, result ->
            if (error != null && error != undefined) {
                continuation.resume(Result.failure(IllegalStateException("Failed to get item: $error")))
            } else {
                continuation.resume(Result.success(result))
            }
        }
    }

    /**
     * Bot API 6.9+
     *
     * A method that receives values from the cloud storage using the specified keys. The keys should contain 1-128 characters, only A-Z, a-z, 0-9, _ and - are allowed. In case of an error, the callback function will be called and the first argument will contain the error. In case of success, the first argument will be null and the values will be passed as the second argument.
     */
    suspend fun getItems(vararg key: String): Result<Map<String, String>> = suspendCoroutine { continuation ->
        jsDelegate.getItems(key.asList().toTypedArray()) { error, result ->
            if (error != null && error != undefined) {
                continuation.resume(Result.failure(IllegalStateException("Failed to get items: $error")))
            } else {
                continuation.resume(Result.success(mapOf(result)))
            }
        }
    }

    /**
     * Bot API 6.9+
     *
     * A method that removes a value from the cloud storage using the specified key. The key should contain 1-128 characters, only A-Z, a-z, 0-9, _ and - are allowed. If an optional callback parameter was passed, the callback function will be called. In case of an error, the first argument will contain the error. In case of success, the first argument will be null and the second argument will be a boolean indicating whether the value was removed.
     */
    suspend fun removeItem(key: String): Result<Boolean> = suspendCoroutine { continuation ->
        jsDelegate.removeItem(key) { error, result ->
            if (error != null && error != undefined) {
                continuation.resume(Result.failure(IllegalStateException("Failed to remove item: $error")))
            } else {
                continuation.resume(Result.success(result))
            }
        }
    }

    /**
     * Bot API 6.9+
     *
     * A method that removes values from the cloud storage using the specified keys. The keys should contain 1-128 characters, only A-Z, a-z, 0-9, _ and - are allowed. If an optional callback parameter was passed, the callback function will be called. In case of an error, the first argument will contain the error. In case of success, the first argument will be null and the second argument will be a boolean indicating whether the values were removed.
     */
    suspend fun removeItems(vararg key: String): Result<Boolean> = suspendCoroutine { continuation ->
        jsDelegate.removeItems(key.asList().toTypedArray()) { error, result ->
            if (error != null && error != undefined) {
                continuation.resume(Result.failure(IllegalStateException("Failed to get items: $error")))
            } else {
                continuation.resume(Result.success(result))
            }
        }
    }

    /**
     * Bot API 6.9+
     *
     * A method that receives the list of all keys stored in the cloud storage. In case of an error, the callback function will be called and the first argument will contain the error. In case of success, the first argument will be null and the list of keys will be passed as the second argument.
     */
    suspend fun getKeys(): Result<List<String>> = suspendCoroutine { continuation ->
        jsDelegate.getKeys { error, result ->
            if (error != null && error != undefined) {
                continuation.resume(Result.failure(IllegalStateException("Failed to get items: $error")))
            } else {
                continuation.resume(Result.success(result.toList()))
            }
        }
    }

    private fun mapOf(jsObject: dynamic): Map<String, String> =
        (js("Object.entries") as (dynamic) -> Array<Array<String>>)
            .invoke(jsObject)
            .associate { entry -> entry[0] to entry[1] }

}

internal external class CloudStorageJs {

    @JsName("setItem")
    fun setItem(key: String, value: String, callback: (dynamic, Boolean) -> Unit): CloudStorageJs

    @JsName("getItem")
    fun getItem(key: String, callback: (dynamic, String) -> Unit): CloudStorageJs

    @JsName("getItems")
    fun getItems(key: Array<String>, callback: (dynamic, dynamic) -> Unit): CloudStorageJs

    @JsName("removeItem")
    fun removeItem(key: String, callback: (dynamic, Boolean) -> Unit): CloudStorageJs

    @JsName("removeItems")
    fun removeItems(key: Array<String>, callback: (dynamic, Boolean) -> Unit): CloudStorageJs

    @JsName("getKeys")
    fun getKeys(callback: (dynamic, Array<String>) -> Unit): CloudStorageJs

}
