@file:Suppress("UnsafeCastFromDynamic")

package nay.kirill.telegram_kmp.mini_app.webApp

import nay.kirill.telegram_kmp.mini_app.webApp.buttons.BackButton
import nay.kirill.telegram_kmp.mini_app.webApp.buttons.MainButton
import nay.kirill.telegram_kmp.mini_app.webApp.data.WebAppInitData
import nay.kirill.telegram_kmp.mini_app.webApp.popup.PopupParams
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val webApp: WebApp by lazy { WebApp(js("window.Telegram.WebApp")) }

class WebApp internal constructor(
    private val jsDelegate: WebAppJs
) {

    /**
     * A string with raw data transferred to the Mini App, convenient for [validating data](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app).
     *
     * **WARNING**: Validate data from this field before using it on the bot's server.
     */
    val rawInitData: String get() = jsDelegate.initData

    /**
     * An object with input data transferred to the Mini App.
     *
     * **WARNING**: Data from this field should not be trusted. You should only use data from initData on the bot's server and only after it has been [validated](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app).
     */
    val initDataUnsafe: WebAppInitData get() = jsDelegate.initDataUnsafe

    /**
     * The version of the Bot API available in the user's Telegram app.
     */
    val version: String get() = jsDelegate.version

    /**
     * The name of the platform of the user's Telegram app.
     */
    val platform: String get() = jsDelegate.platform

    /**
     * The color scheme currently used in the Telegram app. Either “light” or “dark”.
     * Also available as the CSS variable var(--tg-color-scheme).
     */
    val colorScheme: ColorScheme get() = ColorScheme.getValue(jsDelegate.colorScheme)

    /**
     * An object containing the current theme settings used in the Telegram app.
     */
    val themeParams: ThemeParams get() = jsDelegate.themeParams

    /**
     * True, if the Mini App is expanded to the maximum available height. False, if the Mini App occupies part of the screen and can be expanded to the full height using the [expand] method.
     */
    val isExpanded: Boolean get() = jsDelegate.isExpanded

    /**
     * he current height of the visible area of the Mini App. Also available in CSS as the variable var(--tg-viewport-height).
     *
     * The application can display just the top part of the Mini App, with its lower part remaining outside the screen area. From this position, the user can “pull” the Mini App to its maximum height, while the bot can do the same by calling the expand() method. As the position of the Mini App changes, the current height value of the visible area will be updated in real time.
     *
     * Please note that the refresh rate of this value is not sufficient to smoothly follow the lower border of the window. It should not be used to pin interface elements to the bottom of the visible area. It's more appropriate to use the value of the viewportStableHeight field for this purpose.
     */
    val viewportHeight: Float get() = jsDelegate.viewportHeight

    /**
     * The height of the visible area of the Mini App in its last stable state. Also available in CSS as a variable var(--tg-viewport-stable-height).
     *
     * The application can display just the top part of the Mini App, with its lower part remaining outside the screen area. From this position, the user can “pull” the Mini App to its maximum height, while the bot can do the same by calling the expand() method. Unlike the value of viewportHeight, the value of viewportStableHeight does not change as the position of the Mini App changes with user gestures or during animations. The value of viewportStableHeight will be updated after all gestures and animations are completed and the Mini App reaches its final size.
     *
     * Note the event viewportChanged with the passed parameter isStateStable=true, which will allow you to track when the stable state of the height of the visible area changes.
     */
    val viewportStableHeight: Float get() = jsDelegate.viewportStableHeight

    /**
     * Current header color in the #RRGGBB format.
     */
    val headerColor: String get() = jsDelegate.headerColor

    /**
     * Current background color in the #RRGGBB format.
     */
    val backgroundColor: String get() = jsDelegate.backgroundColor

    /**
     * True, if the confirmation dialog is enabled while the user is trying to close the Mini App. False, if the confirmation dialog is disabled.
     */
    val isClosingConfirmationEnabled: Boolean get() = jsDelegate.isClosingConfirmationEnabled

    /**
     * An object for controlling the back button which can be displayed in the header of the Mini App in the Telegram interface.
     */
    val backButton: BackButton get() = jsDelegate.backButton

    /**
     * An object for controlling the main button, which is displayed at the bottom of the Mini App in the Telegram interface.
     */
    val mainButton: MainButton get() = jsDelegate.mainButton

    /**
     * An object for controlling haptic feedback.
     */
    val hapticFeedback: HapticFeedback get() = jsDelegate.hapticFeedback

    /**
     * An object for controlling cloud storage.
     */
    val cloudStorage: CloudStorage get() = CloudStorage(jsDelegate.cloudStorage)

    /**
     * Returns true if the user's app supports a version of the Bot API that is equal to or higher than the version passed as the parameter.
     */
    fun isVersionAtLeast(version: String): Boolean = jsDelegate.isVersionAtLeast(version)

    /**
     * Bot API 6.1+
     *
     * A method that sets the app header color in the #RRGGBB format. You can also use keywords bg_color and secondary_bg_color.
     *
     * Up to Bot API 6.9 You can only pass Telegram.WebApp.themeParams.bg_color or Telegram.WebApp.themeParams.secondary_bg_color as a color or bg_color, secondary_bg_color keywords.
     */
    fun setHeaderColor(color: String) = jsDelegate.setHeaderColor(color)

    /**
     * Bot API 6.1+
     *
     * A method that sets the app background color in the #RRGGBB format. You can also use keywords bg_color and secondary_bg_color.
     */
    fun setBackgroundColor(color: String) = jsDelegate.setBackgroundColor(color)

    /**
     * Bot API 6.2+
     *
     * A method that enables or disables a confirmation dialog while the user is trying to close the Mini App.
     */
    fun enableClosingConfirmation(enable: Boolean) {
        if (enable) {
            jsDelegate.enableClosingConfirmation()
        } else {
            jsDelegate.disableClosingConfirmation()
        }
    }

    /**
     * A method that sets the app event handler.
     */
    fun addEventHandler(eventType: EventType, handler: (Any) -> Unit) = jsDelegate.onEvent(eventType.value, handler)

    /**
     * A method that deletes a previously set event handler.
     */
    fun removeEventHandler(eventType: EventType, handler: (Any) -> Unit) = jsDelegate.offEvent(eventType.value, handler)

    /**
     * A method used to send data to the bot. When this method is called, a service message is sent to the bot containing the data data of the length up to 4096 bytes, and the Mini App is closed. See the field web_app_data in the class Message.
     *
     * This method is only available for Mini Apps launched via a Keyboard button.
     */
    fun sendData(data: String) = jsDelegate.sendData(data)

    /**
     * Bot API 6.7+
     *
     * A method that inserts the bots username and the specified inline query in the current chat's input field. Query may be empty, in which case only the bot's username will be inserted. If an optional choose_chat_types parameter was passed, the client prompts the user to choose a specific chat, then opens that chat and inserts the bot's username and the specified inline query in the input field. You can specify which types of chats the user will be able to choose from. It can be one or more of the following types: users, bots, groups, channels.
     */
    fun switchInlineQuery(query: String? = null, vararg chatType: ChatType) = jsDelegate.switchInlineQuery(query.orEmpty(), chatType.map { it.value }.toTypedArray())

    /**
     * A method that opens a link in an external browser. The Mini App will not be closed.
     * Bot API 6.4+
     * If [tryInstantView] is true, the link will be opened in Instant View mode if possible.
     *
     * Note that this method can be called only in response to user interaction with the Mini App interface (e.g. a click inside the Mini App or on the main button)
     */
    fun openLink(url: String, tryInstantView: Boolean? = null) {
        val options = tryInstantView?.let { asDynamic() }
        options?.try_instant_view = tryInstantView
        jsDelegate.openLink(url, options)
    }

    /**
     * A method that opens a telegram link inside Telegram app. The Mini App will be closed.
     */
    fun openTelegramLink(url: String) = jsDelegate.openTelegramLink(url)

    /**
     * Bot API 6.1+
     *
     * A method that opens an invoice using the link url. The Mini App will receive the event invoiceClosed when the invoice is closed. If an optional callback parameter was passed, the callback function will be called and the invoice status will be passed as the first argument.
     */
    fun openInvoice(url: String, callback: ((status: InvoiceStatus) -> Unit)? = null) {
        jsDelegate.openInvoice(url) { status ->
            callback?.invoke(InvoiceStatus.getValue(status))
        }
    }

    /**
     * Bot API 6.2+
     *
     * A method that shows a native popup described by the params argument of the type PopupParams. The Mini App will receive the event popupClosed when the popup is closed. If an optional callback parameter was passed, the callback function will be called and the field id of the pressed button will be passed as the first argument.
     */
    fun showPopup(params: PopupParams, callback: ((id: String) -> Unit)? = null) = jsDelegate.showPopup(params) { callback?.invoke(it) }

    /**
     * Bot API 6.2+
     *
     * A method that shows message in a simple alert with a 'Close' button. If an optional callback parameter was passed, the callback function will be called when the popup is closed.
     */
    fun showAlert(message: String, onClose: (() -> Unit)? = null) = jsDelegate.showAlert(message) { onClose?.invoke() }

    /**
     * Bot API 6.2+
     *
     * A method that shows message in a simple confirmation window with 'OK' and 'Cancel' buttons. If an optional callback parameter was passed, the callback function will be called when the popup is closed and the first argument will be a boolean indicating whether the user pressed the 'OK' button.
     */
    fun showConfirm(message: String, onClose: ((isConfirmed: Boolean) -> Unit)? = null) = jsDelegate.showConfirm(message) { onClose?.invoke(it) }

    /**
     * Bot API 6.4+ A method that shows a native popup for scanning a QR code described by the params argument of the type ScanQrPopupParams. The Mini App will receive the event qrTextReceived every time the scanner catches a code with text data. If an optional callback parameter was passed, the callback function will be called and the text from the QR code will be passed as the first argument. Returning true inside this callback function causes the popup to be closed.
     */
    fun showScanQrPopup(params: ScanQrPopupParams, onData: ((data: String) -> Boolean)? = null) = jsDelegate.showScanQrPopup(params) { onData?.invoke(it) ?: false }

    /**
     * Bot API 6.4+ A method that closes the native popup for scanning a QR code opened with the showScanQrPopup method. Run it if you received valid data in the event qrTextReceived.
     */
    fun closeScanQrPopup() = jsDelegate.closeScanQrPopup()

    /**
     * Bot API 6.4+
     *
     * A method that requests text from the clipboard. The Mini App will receive the event clipboardTextReceived. If an optional callback parameter was passed, the callback function will be called and the text from the clipboard will be passed as the first argument.
     *
     * Note: this method can be called only for Mini Apps launched from the attachment menu and only in response to a user interaction with the Mini App interface (e.g. a click inside the Mini App or on the main button).
     */
    fun readTextFromClipboard(onReceivedText: ((text: String) -> Unit)? = null) = jsDelegate.readTextFromClipboard { onReceivedText?.invoke(it) }

    /**
     * A method that requests text from the clipboard as a suspend function.
     */
    suspend fun readTextFromClipboard(): String = suspendCoroutine { continuation ->
        jsDelegate.readTextFromClipboard { continuation.resume(it) }
    }

    /**
     * Bot API 6.9+
     *
     * A method that shows a native popup requesting permission for the bot to send messages to the user. If an optional callback parameter was passed, the callback function will be called when the popup is closed and the first argument will be a boolean indicating whether the user granted this access.
     */
    fun requestWriteAccess(callback: ((isGranted: Boolean) -> Unit)? = null) = jsDelegate.requestWriteAccess { callback?.invoke(it) }

    /**
     * Bot API 6.9+
     *
     * A method that shows a native popup prompting the user for their phone number. If an optional callback parameter was passed, the callback function will be called when the popup is closed and the first argument will be a boolean indicating whether the user shared its phone number.
     */
    fun requestContact(callback: ((isShared: Boolean) -> Unit)? = null) = jsDelegate.requestContact { callback?.invoke(it) }

    /**
     * A method that informs the Telegram app that the Mini App is ready to be displayed.
     * It is recommended to call this method as early as possible, as soon as all essential interface elements are loaded. Once this method is called, the loading placeholder is hidden and the Mini App is shown.
     * If the method is not called, the placeholder will be hidden only when the page is fully loaded.
     */
    fun ready() = jsDelegate.ready()

    /**
     * A method that expands the Mini App to the maximum available height. To find out if the Mini App is expanded to the maximum height, refer to the value of the Telegram.WebApp.isExpanded parameter
     */
    fun expand() = jsDelegate.expand()

    /**
     * A method that closes the Mini App.
     */
    fun close() = jsDelegate.close()

}

internal external class WebAppJs {

    @JsName("initData")
    val initData: String

    @JsName("initDataUnsafe")
    val initDataUnsafe: WebAppInitData

    @JsName("version")
    val version: String

    @JsName("platform")
    val platform: String

    @JsName("colorScheme")
    val colorScheme: String

    @JsName("themeParams")
    val themeParams: ThemeParams

    @JsName("isExpanded")
    val isExpanded: Boolean

    @JsName("viewportHeight")
    val viewportHeight: Float

    @JsName("viewportStableHeight")
    val viewportStableHeight: Float

    @JsName("headerColor")
    val headerColor: String

    @JsName("backgroundColor")
    val backgroundColor: String

    @JsName("isClosingConfirmationEnabled")
    val isClosingConfirmationEnabled: Boolean

    @JsName("BackButton")
    val backButton: BackButton

    @JsName("MainButton")
    val mainButton: MainButton

    @JsName("HapticFeedback")
    val hapticFeedback: HapticFeedback

    @JsName("CloudStorage")
    val cloudStorage: CloudStorageJs

    @JsName("isVersionAtLeast")
    fun isVersionAtLeast(version: String): Boolean

    @JsName("setHeaderColor")
    fun setHeaderColor(color: String)

    @JsName("setBackgroundColor")
    fun setBackgroundColor(color: String)

    @JsName("enableClosingConfirmation")
    fun enableClosingConfirmation()

    @JsName("disableClosingConfirmation")
    fun disableClosingConfirmation()

    @JsName("onEvent")
    fun onEvent(eventType: String, eventHandler: (Any) -> Unit)

    @JsName("offEvent")
    fun offEvent(eventType: String, eventHandler: (Any) -> Unit)

    @JsName("sendData")
    fun sendData(data: String)

    @JsName("switchInlineQuery")
    fun switchInlineQuery(query: String, chatTypes: Array<String>)

    @JsName("openLink")
    fun openLink(url: String, options: dynamic)

    @JsName("openTelegramLink")
    fun openTelegramLink(url: String)

    @JsName("openInvoice")
    fun openInvoice(url: String, callback: (String) -> Unit)

    @JsName("showPopup")
    fun showPopup(params: PopupParams, callback: (String) -> Unit)

    @JsName("showAlert")
    fun showAlert(message: String, callback: () -> Unit)

    @JsName("showConfirm")
    fun showConfirm(message: String, callback: (Boolean) -> Unit)

    @JsName("showScanQrPopup")
    fun showScanQrPopup(params: ScanQrPopupParams, callback: (String) -> Boolean)

    @JsName("closeScanQrPopup")
    fun closeScanQrPopup()

    @JsName("readTextFromClipboard")
    fun readTextFromClipboard(callback: (String) -> Unit)

    @JsName("requestWriteAccess")
    fun requestWriteAccess(callback: (Boolean) -> Unit)

    @JsName("requestContact")
    fun requestContact(callback: (Boolean) -> Unit)

    @JsName("ready")
    fun ready()

    @JsName("expand")
    fun expand()

    @JsName("close")
    fun close()

}
