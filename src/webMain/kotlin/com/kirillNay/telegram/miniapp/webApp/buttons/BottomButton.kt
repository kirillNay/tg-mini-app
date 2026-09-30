package com.kirillNay.telegram.miniapp.webApp.buttons

import com.kirillNay.telegram.miniapp.webApp.internal.BottomButtonJs
import com.kirillNay.telegram.miniapp.webApp.internal.CallbackRegistry
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue

/**
 * Controls a button displayed at the bottom of the Mini App in the Telegram interface:
 * either the main button or (Bot API 7.10+) the secondary button.
 *
 * All methods return the button, so calls can be chained.
 */
class BottomButton internal constructor(
    private val js: BottomButtonJs,
) {

    private val callbacks = CallbackRegistry()

    /** Whether this is the main or the secondary button. */
    val type: BottomButtonType get() = BottomButtonType.from(js.type)

    /** Current button text. */
    val text: String get() = js.text

    /** Current button color. */
    val color: String get() = js.color

    /** Current button text color. */
    val textColor: String get() = js.textColor

    /** Shows whether the button is visible. */
    val isVisible: Boolean get() = js.isVisible

    /** Shows whether the button is active. */
    val isActive: Boolean get() = js.isActive

    /** Bot API 7.10+ Shows whether the button has a shine effect. */
    val hasShineEffect: Boolean get() = js.hasShineEffect.isTrue()

    /** Bot API 7.10+ Position of the secondary button. `null` for the main button. */
    val position: BottomButtonPosition? get() = BottomButtonPosition.from(js.position)

    /** Shows whether the button is displaying a loading indicator. */
    val isProgressVisible: Boolean get() = js.isProgressVisible

    /** Bot API 9.5+ Unique identifier of the custom emoji shown before the text of the button. */
    val iconCustomEmojiId: String? get() = js.iconCustomEmojiId?.takeIf { it.isNotEmpty() }

    /** Sets the button text. */
    fun setText(text: String): BottomButton = apply { js.setText(text) }

    /** Sets the button press event handler. Pass the same [callback] to [offClick] to remove it. */
    fun onClick(callback: () -> Unit): BottomButton = apply { js.onClick(callbacks.register(callback)) }

    /** Removes a handler previously set with [onClick]. */
    fun offClick(callback: () -> Unit): BottomButton = apply { callbacks.unregister(callback)?.let(js::offClick) }

    /**
     * Makes the button visible.
     *
     * Note that opening the Mini App from the attachment menu hides the main button until the user interacts with the Mini App interface.
     */
    fun show(): BottomButton = apply { js.show() }

    /** Hides the button. */
    fun hide(): BottomButton = apply { js.hide() }

    /** Enables the button. */
    fun enable(): BottomButton = apply { js.enable() }

    /** Disables the button. */
    fun disable(): BottomButton = apply { js.disable() }

    /**
     * Shows a loading indicator on the button. The button is disabled while the action is in progress,
     * unless [leaveActive] is true.
     */
    fun showProgress(leaveActive: Boolean = false): BottomButton = apply { js.showProgress(leaveActive) }

    /** Hides the loading indicator. */
    fun hideProgress(): BottomButton = apply { js.hideProgress() }

    /** Changes several button parameters at once. Only non-null fields of [params] are applied. */
    fun setParams(params: BottomButtonParams): BottomButton = apply { js.setParams(params.toJs()) }
}

/** Type of a [BottomButton]. */
enum class BottomButtonType(val value: String) {

    MAIN("main"),

    SECONDARY("secondary");

    internal companion object {

        fun from(value: String?): BottomButtonType = entries.find { it.value == value } ?: MAIN
    }
}

/** Bot API 7.10+ Position of the secondary button relative to the main button. Applies only if both buttons are visible. */
enum class BottomButtonPosition(val value: String) {

    /** Displayed to the left of the main button. */
    LEFT("left"),

    /** Displayed to the right of the main button. */
    RIGHT("right"),

    /** Displayed above the main button. */
    TOP("top"),

    /** Displayed below the main button. */
    BOTTOM("bottom");

    internal companion object {

        fun from(value: String?): BottomButtonPosition? = entries.find { it.value == value }
    }
}
