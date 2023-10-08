package com.kirillNay.telegram.miniapp.webApp

enum class EventType(val value: String) {

    /**
     * Occurs whenever theme settings are changed in the user's Telegram app (including switching to night mode).
     * eventHandler receives no parameters, new theme settings and color scheme can be received via this.themeParams and this.colorScheme respectively.
     */
    THEME_CHANGED("themeChanged"),

    /**
     * Occurs when the visible section of the Mini App is changed.
     * eventHandler receives an object with the single field isStateStable. If isStateStable is true, the resizing of the Mini App is finished. If it is false, the resizing is ongoing (the user is expanding or collapsing the Mini App or an animated object is playing). The current value of the visible section’s height is available in this.viewportHeight.
     */
    VIEWPORT_CHANGED("viewportChanged"),

    /**
     * Occurs when the main button is pressed.
     * eventHandler receives no parameters.
     */
    MAIN_BUTTON_CLICKED("mainButtonClicked"),

    /**
     * Bot API 6.1+
     *
     * Occurs when the back button is pressed.
     * eventHandler receives no parameters.
     */
    BACK_BUTTON_CLICKED("backButtonClicked"),

    /**
     * Bot API 6.1+
     *
     * Occurs when the Settings item in context menu is pressed.
     * eventHandler receives no parameters.
     */
    SETTINGS_BUTTON_CLICKED("settingsButtonClicked"),

    /**
     * Bot API 6.1+
     *
     * Occurs when the opened invoice is closed.
     * eventHandler receives an object with the two fields: url – invoice link provided and status – one of the invoice statuses:
     * - **paid** – invoice was paid successfully,
     * - **cancelled** – user closed this invoice without paying,
     * - **failed** – user tried to pay, but the payment was failed,
     * - **pending** – the payment is still processing. The bot will receive a service message about a successful payment when the payment is successfully paid.
     */
    INVOICE_CLOSED("invoiceClosed"),

    /**
     * Bot API 6.2+
     *
     * Occurs when the opened popup is closed.
     * eventHandler receives an object with the single field button_id – the value of the field id of the pressed button. If no buttons were pressed, the field button_id will be null.
     */
    POPUP_CLOSED("popupClosed"),

    /**
     * Bot API 6.4+
     *
     * Occurs when the QR code scanner catches a code with text data.
     * eventHandler receives an object with the single field data containing text data from the QR code.
     */
    QR_TEXT_RECEIVED("qrTextReceived"),

    /**
     * Bot API 6.4+
     *
     * Occurs when the readTextFromClipboard method is called.
     * eventHandler receives an object with the single field data containing text data from the clipboard. If the clipboard contains non-text data, the field data will be an empty string. If the Mini App has no access to the clipboard, the field data will be null.
     */
    CLIPBOARD_TEXT_RECEIVED("clipboardTextReceived"),

    /**
     * Bot API 6.9+
     *
     * Occurs when the write permission was requested.
     * eventHandler receives an object with the single field status containing one of the statuses:
     * - **allowed** – user granted write permission to the bot,
     * - **cancelled** – user declined this request.
     */
    WRITE_ACCESS_REQUESTED("writeAccessRequested"),

    /**
     * Bot API 6.9+
     *
     * Occurs when the user's phone number was requested.
     * eventHandler receives an object with the single field status containing one of the statuses:
     * - **sent** – user shared their phone number with the bot,
     * - **cancelled** – user declined this request.
     */
    CONTACT_REQUESTED("contactRequested")

}