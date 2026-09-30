package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.buttons.BackButton
import com.kirillNay.telegram.miniapp.webApp.buttons.BottomButton
import com.kirillNay.telegram.miniapp.webApp.buttons.SettingsButton
import com.kirillNay.telegram.miniapp.webApp.data.WebAppInitData
import com.kirillNay.telegram.miniapp.webApp.internal.WebAppJs
import com.kirillNay.telegram.miniapp.webApp.internal.asBooleanOrNull
import com.kirillNay.telegram.miniapp.webApp.internal.awaitValue
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.jsFunction1
import com.kirillNay.telegram.miniapp.webApp.internal.jsObject
import com.kirillNay.telegram.miniapp.webApp.internal.telegramWebAppJsOrNull
import com.kirillNay.telegram.miniapp.webApp.internal.asDoubleOrNull
import com.kirillNay.telegram.miniapp.webApp.internal.toJsStringArray
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull
import com.kirillNay.telegram.miniapp.webApp.popup.PopupParams
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * The Telegram Mini App object, `window.Telegram.WebApp`.
 *
 * Throws [IllegalStateException] on first access if telegram-web-app.js is not loaded.
 * Check [WebApp.isAvailable] or use [WebApp.getOrNull] if the page can also be opened outside Telegram.
 */
val webApp: WebApp by lazy {
    WebApp.getOrNull() ?: error(
        "window.Telegram.WebApp is not available. Add <script src=\"https://telegram.org/js/telegram-web-app.js\"></script> to the page."
    )
}

/**
 * Kotlin wrapper over `window.Telegram.WebApp`. Use the global [webApp] instance.
 *
 * Methods that accept an optional callback have `await*` suspend counterparts.
 * Before using features from newer Bot API versions, check [isVersionAtLeast].
 */
class WebApp internal constructor(
    private val js: WebAppJs,
) {

    companion object {

        private val instance: WebApp? by lazy { telegramWebAppJsOrNull()?.let(::WebApp) }

        /** Returns the WebApp object, or `null` if telegram-web-app.js is not loaded. */
        fun getOrNull(): WebApp? = instance

        /** True if telegram-web-app.js is loaded on the page. */
        val isAvailable: Boolean get() = instance != null

        /**
         * True if the page was opened by Telegram as a Mini App, i.e. the script is loaded and received launch data.
         * A page opened in a regular browser with the script loaded has an empty [rawInitData].
         */
        val isRunningInTelegram: Boolean get() = instance?.rawInitData?.isNotEmpty() == true
    }

    /**
     * A string with raw data transferred to the Mini App, convenient for
     * [validating data](https://core.telegram.org/bots/webapps#validating-data-received-via-the-mini-app).
     *
     * **WARNING**: Validate data from this field before using it on the bot's server.
     */
    val rawInitData: String get() = js.initData

    /**
     * Input data transferred to the Mini App.
     *
     * **WARNING**: Data from this field should not be trusted. Use data from [rawInitData] on the bot's server, after it has been validated.
     */
    val initDataUnsafe: WebAppInitData by lazy { WebAppInitData.from(js.initDataUnsafe) }

    /** The version of the Bot API available in the user's Telegram app. */
    val version: String get() = js.version

    /** The name of the platform of the user's Telegram app. */
    val platform: String get() = js.platform

    /** The color scheme currently used in the Telegram app. Also available as the CSS variable `var(--tg-color-scheme)`. */
    val colorScheme: ColorScheme get() = ColorScheme.from(js.colorScheme)

    /** The current theme settings used in the Telegram app. */
    val themeParams: ThemeParams get() = ThemeParams.from(js.themeParams)

    /** Bot API 8.0+ True if the Mini App is currently active, false if it is minimized. */
    val isActive: Boolean get() = js.isActive.asBooleanOrNull() ?: true

    /** True if the Mini App is expanded to the maximum available height. */
    val isExpanded: Boolean get() = js.isExpanded

    /**
     * The current height of the visible area of the Mini App, in CSS pixels.
     *
     * The refresh rate of this value is not sufficient to smoothly follow the lower border of the window.
     * Use [viewportStableHeight] to pin interface elements to the bottom of the visible area.
     */
    val viewportHeight: Double get() = js.viewportHeight.asDoubleOrNull() ?: 0.0

    /**
     * The height of the visible area of the Mini App in its last stable state, in CSS pixels.
     * Unlike [viewportHeight], it doesn't change during user gestures and animations.
     */
    val viewportStableHeight: Double get() = js.viewportStableHeight.asDoubleOrNull() ?: 0.0

    /** Current header color in the #RRGGBB format. */
    val headerColor: String? get() = js.headerColor

    /** Current background color in the #RRGGBB format. */
    val backgroundColor: String? get() = js.backgroundColor

    /** Bot API 7.10+ Current bottom bar color in the #RRGGBB format. */
    val bottomBarColor: String? get() = js.bottomBarColor

    /** True if the confirmation dialog is enabled while the user is trying to close the Mini App. */
    val isClosingConfirmationEnabled: Boolean get() = js.isClosingConfirmationEnabled

    /** Bot API 7.7+ True if vertical swipes to close or minimize the Mini App are enabled. */
    val isVerticalSwipesEnabled: Boolean get() = js.isVerticalSwipesEnabled.asBooleanOrNull() ?: true

    /** Bot API 8.0+ True if the Mini App is currently displayed in fullscreen mode. */
    val isFullscreen: Boolean get() = js.isFullscreen.isTrue()

    /** Bot API 8.0+ True if the Mini App's orientation is currently locked. */
    val isOrientationLocked: Boolean get() = js.isOrientationLocked.isTrue()

    /** Bot API 8.0+ The device's safe area insets, accounting for system UI elements like notches or navigation bars. */
    val safeAreaInset: SafeAreaInset get() = SafeAreaInset.from(js.safeAreaInset)

    /** Bot API 8.0+ The safe area for displaying content within the app, free from overlapping Telegram UI elements. */
    val contentSafeAreaInset: SafeAreaInset get() = SafeAreaInset.from(js.contentSafeAreaInset)

    /** The back button displayed in the header of the Mini App. */
    val backButton: BackButton by lazy { BackButton(js.BackButton) }

    /** The main button displayed at the bottom of the Mini App. */
    val mainButton: BottomButton by lazy { BottomButton(js.MainButton) }

    /** Bot API 7.10+ The secondary button displayed at the bottom of the Mini App. */
    val secondaryButton: BottomButton by lazy { BottomButton(js.SecondaryButton) }

    /** Bot API 7.0+ The Settings item in the context menu of the Mini App. */
    val settingsButton: SettingsButton by lazy { SettingsButton(js.SettingsButton) }

    /** Haptic feedback. */
    val hapticFeedback: HapticFeedback by lazy { HapticFeedback(js.HapticFeedback) }

    /** Bot API 6.9+ Cloud storage. */
    val cloudStorage: CloudStorage by lazy { CloudStorage(js.CloudStorage) }

    /** Bot API 9.0+ Persistent local storage on the device. */
    val deviceStorage: DeviceStorage by lazy { DeviceStorage(js.DeviceStorage) }

    /** Bot API 9.0+ Secure storage on the device. */
    val secureStorage: SecureStorage by lazy { SecureStorage(js.SecureStorage) }

    /** Bot API 7.2+ Biometrics on the device. */
    val biometricManager: BiometricManager by lazy { BiometricManager(js.BiometricManager) }

    /** Bot API 8.0+ Accelerometer data. */
    val accelerometer: Accelerometer by lazy { Accelerometer(js.Accelerometer) }

    /** Bot API 8.0+ Gyroscope data. */
    val gyroscope: Gyroscope by lazy { Gyroscope(js.Gyroscope) }

    /** Bot API 8.0+ Device orientation data. */
    val deviceOrientation: DeviceOrientation by lazy { DeviceOrientation(js.DeviceOrientation) }

    /** Bot API 8.0+ Location on the device. */
    val locationManager: LocationManager by lazy { LocationManager(js.LocationManager) }

    /** Returns true if the user's app supports a version of the Bot API that is equal to or higher than [version]. */
    fun isVersionAtLeast(version: String): Boolean = js.isVersionAtLeast(version)

    /**
     * Bot API 6.1+ Sets the app header color in the #RRGGBB format. The keywords `bg_color` and `secondary_bg_color` are also accepted.
     *
     * Up to Bot API 6.9 only `bg_color` and `secondary_bg_color` values are supported.
     */
    fun setHeaderColor(color: String) = js.setHeaderColor(color)

    /** Bot API 6.1+ Sets the app background color in the #RRGGBB format. The keywords `bg_color` and `secondary_bg_color` are also accepted. */
    fun setBackgroundColor(color: String) = js.setBackgroundColor(color)

    /**
     * Bot API 7.10+ Sets the app's bottom bar color in the #RRGGBB format. The keywords `bg_color`, `secondary_bg_color`
     * and `bottom_bar_bg_color` are also accepted. The color is also applied to the navigation bar on Android.
     */
    fun setBottomBarColor(color: String) = js.setBottomBarColor(color)

    /** Bot API 6.2+ Enables a confirmation dialog while the user is trying to close the Mini App. */
    fun enableClosingConfirmation() = js.enableClosingConfirmation()

    /** Bot API 6.2+ Disables the confirmation dialog while the user is trying to close the Mini App. */
    fun disableClosingConfirmation() = js.disableClosingConfirmation()

    /** Bot API 7.7+ Enables vertical swipes to close or minimize the Mini App. */
    fun enableVerticalSwipes() = js.enableVerticalSwipes()

    /** Bot API 7.7+ Disables vertical swipes to close or minimize the Mini App, e.g. if they conflict with the app's own gestures. */
    fun disableVerticalSwipes() = js.disableVerticalSwipes()

    /**
     * Bot API 8.0+ Requests opening the Mini App in fullscreen mode. Listen to [WebAppEvent.FullscreenChanged]
     * and [WebAppEvent.FullscreenFailed] for the result.
     */
    fun requestFullscreen() = js.requestFullscreen()

    /** Bot API 8.0+ Requests exiting fullscreen mode. */
    fun exitFullscreen() = js.exitFullscreen()

    /** Bot API 8.0+ Locks the Mini App's orientation to its current mode (portrait or landscape). */
    fun lockOrientation() = js.lockOrientation()

    /** Bot API 8.0+ Unlocks the Mini App's orientation, so it follows the device's rotation. */
    fun unlockOrientation() = js.unlockOrientation()

    /**
     * Bot API 8.0+ Prompts the user to add the Mini App to the home screen. [WebAppEvent.HomeScreenAdded] is sent
     * after the icon is added, if the device supports it.
     */
    fun addToHomeScreen() = js.addToHomeScreen()

    /** Bot API 8.0+ Checks if adding to the home screen is supported and if the Mini App has already been added. */
    fun checkHomeScreenStatus(callback: ((status: HomeScreenStatus) -> Unit)? = null) =
        js.checkHomeScreenStatus { callback?.invoke(HomeScreenStatus.from(it.asStringOrNull())) }

    /** Suspend version of [checkHomeScreenStatus]. */
    suspend fun awaitHomeScreenStatus(): HomeScreenStatus =
        awaitValue({ js.checkHomeScreenStatus(it) }) { HomeScreenStatus.from(it.asStringOrNull()) }

    /**
     * Sets a handler for [event]. The returned [EventSubscription] removes it.
     *
     * ```
     * val subscription = webApp.onEvent(WebAppEvent.ViewportChanged) { isStateStable -> ... }
     * subscription.unsubscribe()
     * ```
     */
    fun <T> onEvent(event: WebAppEvent<T>, handler: (T) -> Unit): EventSubscription {
        val function = jsFunction1 { raw -> handler(event.payload(raw)) }
        js.onEvent(event.type, function)
        return EventSubscription { js.offEvent(event.type, function) }
    }

    /**
     * Sends data to the bot and closes the Mini App. The bot receives a service message with up to 4096 bytes of [data]
     * in the field web_app_data of the class Message.
     *
     * Only available for Mini Apps launched via a Keyboard button.
     */
    fun sendData(data: String) = js.sendData(data)

    /**
     * Bot API 6.7+ Inserts the bot's username and the specified inline [query] in the current chat's input field.
     * If [chatTypes] are passed, the client prompts the user to choose a chat of these types first.
     */
    fun switchInlineQuery(query: String = "", chatTypes: List<ChatType> = emptyList()) =
        js.switchInlineQuery(query, chatTypes.takeIf { it.isNotEmpty() }?.map { it.value }?.toJsStringArray())

    /**
     * Opens a link in an external browser. The Mini App is not closed.
     *
     * Bot API 6.4+ If [tryInstantView] is true, the link is opened in Instant View mode if possible.
     *
     * Can be called only in response to user interaction with the Mini App interface.
     */
    fun openLink(url: String, tryInstantView: Boolean = false) =
        js.openLink(url, jsObject { if (tryInstantView) put("try_instant_view", true) })

    /**
     * Opens a telegram link inside the Telegram app.
     *
     * Up to Bot API 7.0 the Mini App is closed after this method is called.
     */
    fun openTelegramLink(url: String) = js.openTelegramLink(url)

    /**
     * Bot API 6.1+ Opens an invoice using the link [url]. The Mini App also receives [WebAppEvent.InvoiceClosed] when it is closed.
     */
    fun openInvoice(url: String, callback: ((status: InvoiceStatus) -> Unit)? = null) =
        js.openInvoice(url) { callback?.invoke(InvoiceStatus.from(it.asStringOrNull())) }

    /** Suspend version of [openInvoice]. Returns the invoice status when the invoice is closed. */
    suspend fun awaitInvoice(url: String): InvoiceStatus =
        awaitValue({ js.openInvoice(url, it) }) { InvoiceStatus.from(it.asStringOrNull()) }

    /** Bot API 7.8+ Opens the native story editor with the media specified by the HTTPS [mediaUrl]. */
    fun shareToStory(mediaUrl: String, params: StoryShareParams? = null) = js.shareToStory(mediaUrl, params?.toJs())

    /**
     * Bot API 8.0+ Opens a dialog allowing the user to share a message provided by the bot. [messageId] must belong
     * to a PreparedInlineMessage obtained via the Bot API method savePreparedInlineMessage.
     * The callback receives whether the message was sent.
     */
    fun shareMessage(messageId: String, callback: ((isSent: Boolean) -> Unit)? = null) =
        js.shareMessage(messageId) { callback?.invoke(it.isTrue()) }

    /** Suspend version of [shareMessage]. Returns whether the message was sent. */
    suspend fun awaitShareMessage(messageId: String): Boolean =
        awaitValue({ js.shareMessage(messageId, it) }) { it.isTrue() }

    /**
     * Bot API 8.0+ Opens a dialog allowing the user to set the custom emoji [customEmojiId] as their status.
     * The callback receives whether the status was set.
     *
     * For fully programmatic changes use the Bot API method setUserEmojiStatus after [requestEmojiStatusAccess].
     */
    fun setEmojiStatus(
        customEmojiId: String,
        params: EmojiStatusParams? = null,
        callback: ((isSet: Boolean) -> Unit)? = null,
    ) = js.setEmojiStatus(customEmojiId, params?.toJs()) { callback?.invoke(it.isTrue()) }

    /** Suspend version of [setEmojiStatus]. Returns whether the status was set. */
    suspend fun awaitEmojiStatus(customEmojiId: String, params: EmojiStatusParams? = null): Boolean =
        awaitValue({ js.setEmojiStatus(customEmojiId, params?.toJs(), it) }) { it.isTrue() }

    /**
     * Bot API 8.0+ Shows a native popup requesting permission for the bot to manage the user's emoji status.
     * The callback receives whether access was granted.
     */
    fun requestEmojiStatusAccess(callback: ((isGranted: Boolean) -> Unit)? = null) =
        js.requestEmojiStatusAccess { callback?.invoke(it.isTrue()) }

    /** Suspend version of [requestEmojiStatusAccess]. Returns whether access was granted. */
    suspend fun awaitEmojiStatusAccess(): Boolean = awaitValue({ js.requestEmojiStatusAccess(it) }) { it.isTrue() }

    /**
     * Bot API 8.0+ Displays a native popup prompting the user to download a file.
     * The callback receives whether the user accepted the download request.
     */
    fun downloadFile(params: DownloadFileParams, callback: ((isAccepted: Boolean) -> Unit)? = null) =
        js.downloadFile(params.toJs()) { callback?.invoke(it.isTrue()) }

    /** Suspend version of [downloadFile]. Returns whether the user accepted the download request. */
    suspend fun awaitDownloadFile(params: DownloadFileParams): Boolean =
        awaitValue({ js.downloadFile(params.toJs(), it) }) { it.isTrue() }

    /** Bot API 9.1+ Hides the on-screen keyboard, if it is currently visible. */
    fun hideKeyboard() = js.hideKeyboard()

    /**
     * Bot API 6.2+ Shows a native popup. The callback receives the id of the pressed button, or an empty string
     * if the popup was closed without pressing a button. [WebAppEvent.PopupClosed] is also sent.
     */
    fun showPopup(params: PopupParams, callback: ((buttonId: String) -> Unit)? = null) =
        js.showPopup(params.toJs()) { callback?.invoke(it.asStringOrNull().orEmpty()) }

    /** Suspend version of [showPopup]. Returns the id of the pressed button. */
    suspend fun awaitPopup(params: PopupParams): String =
        awaitValue({ js.showPopup(params.toJs(), it) }) { it.asStringOrNull().orEmpty() }

    /** Bot API 6.2+ Shows [message] in a simple alert with a 'Close' button. [onClose] is called when the popup is closed. */
    fun showAlert(message: String, onClose: (() -> Unit)? = null) = js.showAlert(message) { onClose?.invoke() }

    /** Suspend version of [showAlert]. Resumes when the popup is closed. */
    suspend fun awaitAlert(message: String): Unit = suspendCoroutine { continuation ->
        js.showAlert(message) { continuation.resume(Unit) }
    }

    /**
     * Bot API 6.2+ Shows [message] in a simple confirmation window with 'OK' and 'Cancel' buttons.
     * The callback receives whether the user pressed 'OK'.
     */
    fun showConfirm(message: String, onClose: ((isConfirmed: Boolean) -> Unit)? = null) =
        js.showConfirm(message) { onClose?.invoke(it.isTrue()) }

    /** Suspend version of [showConfirm]. Returns whether the user pressed 'OK'. */
    suspend fun awaitConfirm(message: String): Boolean = awaitValue({ js.showConfirm(message, it) }) { it.isTrue() }

    /**
     * Bot API 6.4+ Shows a native popup for scanning a QR code. [onData] is called with the text of every scanned code;
     * returning true closes the popup. [WebAppEvent.QrTextReceived] is also sent, and since Bot API 7.7
     * [WebAppEvent.ScanQrPopupClosed] is sent if the user closes the popup.
     */
    fun showScanQrPopup(params: ScanQrPopupParams = ScanQrPopupParams(), onData: ((data: String) -> Boolean)? = null) =
        js.showScanQrPopup(params.toJs()) { onData?.invoke(it.asStringOrNull().orEmpty()) ?: false }

    /** Bot API 6.4+ Closes the popup opened with [showScanQrPopup]. */
    fun closeScanQrPopup() = js.closeScanQrPopup()

    /**
     * Bot API 6.4+ Requests text from the clipboard. The callback receives the text, or `null` if there is no access.
     * [WebAppEvent.ClipboardTextReceived] is also sent.
     *
     * Can be called only for Mini Apps launched from the attachment menu and only in response to user interaction.
     */
    fun readTextFromClipboard(callback: ((text: String?) -> Unit)? = null) =
        js.readTextFromClipboard { callback?.invoke(it.asStringOrNull()) }

    /** Suspend version of [readTextFromClipboard]. */
    suspend fun awaitClipboardText(): String? = awaitValue({ js.readTextFromClipboard(it) }) { it.asStringOrNull() }

    /**
     * Bot API 6.9+ Shows a native popup requesting permission for the bot to send messages to the user.
     * The callback receives whether access was granted.
     */
    fun requestWriteAccess(callback: ((isGranted: Boolean) -> Unit)? = null) =
        js.requestWriteAccess { callback?.invoke(it.isTrue()) }

    /** Suspend version of [requestWriteAccess]. Returns whether access was granted. */
    suspend fun awaitWriteAccess(): Boolean = awaitValue({ js.requestWriteAccess(it) }) { it.isTrue() }

    /**
     * Bot API 6.9+ Shows a native popup prompting the user for their phone number.
     * The callback receives whether the user shared it.
     */
    fun requestContact(callback: ((isShared: Boolean) -> Unit)? = null) =
        js.requestContact { callback?.invoke(it.isTrue()) }

    /** Suspend version of [requestContact]. Returns whether the user shared their phone number. */
    suspend fun awaitContact(): Boolean = awaitValue({ js.requestContact(it) }) { it.isTrue() }

    /**
     * Bot API 9.6+ Opens a dialog allowing the user to select an existing chat or create a new one.
     * [requestId] must belong to a PreparedKeyboardButton obtained via the Bot API method savePreparedKeyboardButton.
     * The callback receives whether the message was sent.
     */
    fun requestChat(requestId: String, callback: ((isSent: Boolean) -> Unit)? = null) =
        js.requestChat(requestId) { callback?.invoke(it.isTrue()) }

    /** Suspend version of [requestChat]. Returns whether the message was sent. */
    suspend fun awaitRequestChat(requestId: String): Boolean = awaitValue({ js.requestChat(requestId, it) }) { it.isTrue() }

    /**
     * Informs the Telegram app that the Mini App is ready to be displayed. Call it as early as possible,
     * as soon as all essential interface elements are loaded.
     */
    fun ready() = js.ready()

    /** Expands the Mini App to the maximum available height. See [isExpanded]. */
    fun expand() = js.expand()

    /** Closes the Mini App. */
    fun close() = js.close()
}
