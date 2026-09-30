package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.HapticFeedbackJs

/**
 * Controls haptic feedback. All methods return the object, so calls can be chained.
 */
class HapticFeedback internal constructor(
    private val js: HapticFeedbackJs,
) {

    /** Bot API 6.1+ Tells that an impact occurred. The Telegram app may play the appropriate haptics based on [style]. */
    fun impactOccurred(style: ImpactStyle): HapticFeedback = apply { js.impactOccurred(style.value) }

    /** Bot API 6.1+ Tells that a task or action has succeeded, failed, or produced a warning. */
    fun notificationOccurred(type: NotificationType): HapticFeedback = apply { js.notificationOccurred(type.value) }

    /**
     * Bot API 6.1+ Tells that the user has changed a selection.
     *
     * Do not use this feedback when the user makes or confirms a selection; use it only when the selection changes.
     */
    fun selectionChanged(): HapticFeedback = apply { js.selectionChanged() }

    enum class ImpactStyle(val value: String) {
        /** A collision between small or lightweight UI objects. */
        LIGHT("light"),

        /** A collision between medium-sized or medium-weight UI objects. */
        MEDIUM("medium"),

        /** A collision between large or heavyweight UI objects. */
        HEAVY("heavy"),

        /** A collision between hard or inflexible UI objects. */
        RIGID("rigid"),

        /** A collision between soft or flexible UI objects. */
        SOFT("soft")
    }

    enum class NotificationType(val value: String) {
        /** A task or action has failed. */
        ERROR("error"),

        /** A task or action has completed successfully. */
        SUCCESS("success"),

        /** A task or action produced a warning. */
        WARNING("warning")
    }
}
