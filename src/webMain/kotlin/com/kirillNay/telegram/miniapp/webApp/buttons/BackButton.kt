package com.kirillNay.telegram.miniapp.webApp.buttons

import com.kirillNay.telegram.miniapp.webApp.internal.BackButtonJs
import com.kirillNay.telegram.miniapp.webApp.internal.CallbackRegistry

/**
 * Controls the back button, which can be displayed in the header of the Mini App in the Telegram interface.
 *
 * All methods return the button, so calls can be chained.
 */
class BackButton internal constructor(
    private val js: BackButtonJs,
) {

    private val callbacks = CallbackRegistry()

    /** Shows whether the button is visible. */
    val isVisible: Boolean get() = js.isVisible

    /** Bot API 6.1+ Sets the button press event handler. Pass the same [callback] to [offClick] to remove it. */
    fun onClick(callback: () -> Unit): BackButton = apply { js.onClick(callbacks.register(callback)) }

    /** Bot API 6.1+ Removes a handler previously set with [onClick]. */
    fun offClick(callback: () -> Unit): BackButton = apply { callbacks.unregister(callback)?.let(js::offClick) }

    /** Bot API 6.1+ Makes the button active and visible. */
    fun show(): BackButton = apply { js.show() }

    /** Bot API 6.1+ Hides the button. */
    fun hide(): BackButton = apply { js.hide() }
}
