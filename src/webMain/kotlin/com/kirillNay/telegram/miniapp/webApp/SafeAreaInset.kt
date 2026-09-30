package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.InsetJs
import com.kirillNay.telegram.miniapp.webApp.internal.asIntOrNull

/**
 * Bot API 8.0+
 *
 * Insets in CSS pixels. Used both for the device safe area ([WebApp.safeAreaInset], avoiding notches and system bars)
 * and for the content safe area ([WebApp.contentSafeAreaInset], avoiding Telegram UI elements).
 */
data class SafeAreaInset(
    val top: Int,
    val bottom: Int,
    val left: Int,
    val right: Int,
) {

    companion object {

        val Zero = SafeAreaInset(0, 0, 0, 0)

        internal fun from(js: InsetJs?): SafeAreaInset = if (js == null) {
            Zero
        } else {
            SafeAreaInset(
                top = js.top.asIntOrNull() ?: 0,
                bottom = js.bottom.asIntOrNull() ?: 0,
                left = js.left.asIntOrNull() ?: 0,
                right = js.right.asIntOrNull() ?: 0,
            )
        }
    }
}
