package com.kirillNay.telegram.miniapp.webApp

/**
 * This object controls haptic feedback.
 */
external class HapticFeedback {

    /**
     * Bot API 6.1+
     * A method tells that an impact occurred. The Telegram app may play the appropriate haptics based on style value passed. Style can be one of these values:
     * - light, indicates a collision between small or lightweight UI objects,
     * - medium, indicates a collision between medium-sized or medium-weight UI objects,
     * - heavy, indicates a collision between large or heavyweight UI objects,
     * - rigid, indicates a collision between hard or inflexible UI objects,
     * - soft, indicates a collision between soft or flexible UI objects.
     */
    @JsName("impactOccurred")
    fun impactOccurred(style: String): HapticFeedback

    /**
     * Bot API 6.1+
     * A method tells that a task or action has succeeded, failed, or produced a warning. The Telegram app may play the appropriate haptics based on type value passed. Type can be one of these values:
     * - error, indicates that a task or action has failed,
     * - success, indicates that a task or action has completed successfully,
     * - warning, indicates that a task or action produced a warning.
     */
    @JsName("notificationOccurred")
    fun notificationOccurred(type: String): HapticFeedback

    /**
     * Bot API 6.1+ A method tells that the user has changed a selection. The Telegram app may play the appropriate haptics.
     *
     * Do not use this feedback when the user makes or confirms a selection; use it only when the selection changes.
     */
    @JsName("selectionChanged")
    fun selectionChanged(): HapticFeedback

}