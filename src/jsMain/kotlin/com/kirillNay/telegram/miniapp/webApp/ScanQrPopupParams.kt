package com.kirillNay.telegram.miniapp.webApp

/**
 * This object describes the native popup for scanning QR codes.
 *
 * @param text
 * Optional. The text to be displayed under the 'Scan QR' heading, 0-64 characters.
 */
class ScanQrPopupParams(
    @JsName("text")
    val text: String? = null
)