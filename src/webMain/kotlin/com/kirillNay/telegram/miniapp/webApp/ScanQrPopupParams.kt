package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * The native popup for scanning QR codes.
 *
 * @param text the text to be displayed under the 'Scan QR' heading, 0-64 characters.
 */
data class ScanQrPopupParams(
    val text: String? = null,
) {

    internal fun toJs(): JsAny = jsObject { put("text", text) }
}
