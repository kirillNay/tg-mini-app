package com.kirillNay.telegram.miniapp.webApp.buttons

import com.kirillNay.telegram.miniapp.webApp.internal.CallbackRegistry
import com.kirillNay.telegram.miniapp.webApp.internal.SettingsButtonJs

/**
 * Bot API 7.0+
 *
 * Controls the Settings item in the context menu of the Mini App in the Telegram interface.
 *
 * All methods return the button, so calls can be chained.
 */
class SettingsButton internal constructor(
    private val js: SettingsButtonJs,
) {

    private val callbacks = CallbackRegistry()

    /** Shows whether the context menu item is visible. */
    val isVisible: Boolean get() = js.isVisible

    /** Bot API 7.0+ Sets the press event handler. Pass the same [callback] to [offClick] to remove it. */
    fun onClick(callback: () -> Unit): SettingsButton = apply { js.onClick(callbacks.register(callback)) }

    /** Bot API 7.0+ Removes a handler previously set with [onClick]. */
    fun offClick(callback: () -> Unit): SettingsButton = apply { callbacks.unregister(callback)?.let(js::offClick) }

    /** Bot API 7.0+ Makes the Settings item in the context menu visible. */
    fun show(): SettingsButton = apply { js.show() }

    /** Bot API 7.0+ Hides the Settings item in the context menu. */
    fun hide(): SettingsButton = apply { js.hide() }
}
