package com.kirillNay.telegram.miniapp.webApp.internal

/**
 * Keeps the JS function created for a Kotlin callback, so that `offClick(callback)` removes exactly the
 * function that `onClick(callback)` registered. Without it every call would create a new JS wrapper.
 */
internal class CallbackRegistry {

    private val functions = HashMap<Any, JsAny>()

    fun register(callback: () -> Unit): JsAny = functions.getOrPut(callback) { jsFunction0(callback) }

    fun unregister(callback: () -> Unit): JsAny? = functions.remove(callback)
}
