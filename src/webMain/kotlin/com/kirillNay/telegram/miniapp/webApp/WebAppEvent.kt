package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.isNullOrUndefined
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.jsGet
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull

/**
 * Events the Mini App can receive from the Telegram app. Subscribe with [WebApp.onEvent].
 *
 * [T] is the type of the value passed to the handler. Events without parameters pass [Unit];
 * for them the current state can be read from [WebApp] (for example [WebApp.themeParams] after [ThemeChanged]).
 */
sealed class WebAppEvent<out T>(
    /** The event name used by telegram-web-app.js. */
    val type: String,
) {

    internal abstract fun payload(raw: JsAny?): T

    override fun toString(): String = type

    /** An event whose handler receives no parameters. */
    sealed class Simple(type: String) : WebAppEvent<Unit>(type) {
        override fun payload(raw: JsAny?) = Unit
    }

    /** An event whose handler receives the `error` field describing a failure, for example `UNSUPPORTED`. */
    sealed class Failure(type: String) : WebAppEvent<String>(type) {
        override fun payload(raw: JsAny?): String = field(raw, "error").asStringOrNull().orEmpty()
    }

    /** Bot API 8.0+ The Mini App becomes active (e.g. opened from minimized state or selected among tabs). */
    data object Activated : Simple("activated")

    /** Bot API 8.0+ The Mini App becomes inactive (e.g. minimized or moved to an inactive tab). */
    data object Deactivated : Simple("deactivated")

    /** Theme settings are changed in the user's Telegram app, including switching to night mode. */
    data object ThemeChanged : Simple("themeChanged")

    /**
     * The visible section of the Mini App is changed. The handler receives `isStateStable`:
     * true if resizing is finished, false if it is ongoing.
     */
    data object ViewportChanged : WebAppEvent<Boolean>("viewportChanged") {
        override fun payload(raw: JsAny?): Boolean = field(raw, "isStateStable").isTrue()
    }

    /** Bot API 8.0+ The device's safe area insets change. Read the new value from [WebApp.safeAreaInset]. */
    data object SafeAreaChanged : Simple("safeAreaChanged")

    /** Bot API 8.0+ The content safe area changes. Read the new value from [WebApp.contentSafeAreaInset]. */
    data object ContentSafeAreaChanged : Simple("contentSafeAreaChanged")

    /** The main button is pressed. */
    data object MainButtonClicked : Simple("mainButtonClicked")

    /** Bot API 7.10+ The secondary button is pressed. */
    data object SecondaryButtonClicked : Simple("secondaryButtonClicked")

    /** Bot API 6.1+ The back button is pressed. */
    data object BackButtonClicked : Simple("backButtonClicked")

    /** Bot API 6.1+ The Settings item in the context menu is pressed. */
    data object SettingsButtonClicked : Simple("settingsButtonClicked")

    /** Bot API 6.1+ The opened invoice is closed. */
    data object InvoiceClosed : WebAppEvent<InvoiceClosedData>("invoiceClosed") {
        override fun payload(raw: JsAny?) = InvoiceClosedData(
            url = field(raw, "url").asStringOrNull().orEmpty(),
            status = InvoiceStatus.from(field(raw, "status").asStringOrNull()),
        )
    }

    /** Bot API 6.2+ The opened popup is closed. The handler receives the id of the pressed button, or `null` if none was pressed. */
    data object PopupClosed : WebAppEvent<String?>("popupClosed") {
        override fun payload(raw: JsAny?): String? = field(raw, "button_id").asStringOrNull()
    }

    /** Bot API 6.4+ The QR code scanner catches a code with text data. The handler receives the text. */
    data object QrTextReceived : WebAppEvent<String>("qrTextReceived") {
        override fun payload(raw: JsAny?): String = field(raw, "data").asStringOrNull().orEmpty()
    }

    /** Bot API 7.7+ The QR code scanner popup is closed by the user. */
    data object ScanQrPopupClosed : Simple("scanQrPopupClosed")

    /**
     * Bot API 6.4+ [WebApp.readTextFromClipboard] was called. The handler receives the clipboard text:
     * an empty string for non-text data and `null` if the Mini App has no access to the clipboard.
     */
    data object ClipboardTextReceived : WebAppEvent<String?>("clipboardTextReceived") {
        override fun payload(raw: JsAny?): String? = field(raw, "data").asStringOrNull()
    }

    /** Bot API 6.9+ Write permission was requested. The handler receives whether the user granted it. */
    data object WriteAccessRequested : WebAppEvent<Boolean>("writeAccessRequested") {
        override fun payload(raw: JsAny?): Boolean = field(raw, "status").asStringOrNull() == "allowed"
    }

    /** Bot API 6.9+ The user's phone number was requested. The handler receives whether the user shared it. */
    data object ContactRequested : WebAppEvent<Boolean>("contactRequested") {
        override fun payload(raw: JsAny?): Boolean = field(raw, "status").asStringOrNull() == "sent"
    }

    /** Bot API 7.2+ The [BiometricManager] object is changed. */
    data object BiometricManagerUpdated : Simple("biometricManagerUpdated")

    /** Bot API 7.2+ Biometric authentication was requested. */
    data object BiometricAuthRequested : WebAppEvent<BiometricAuthResult>("biometricAuthRequested") {
        override fun payload(raw: JsAny?) = BiometricAuthResult(
            isAuthenticated = field(raw, "isAuthenticated").isTrue(),
            biometricToken = field(raw, "biometricToken").asStringOrNull(),
        )
    }

    /** Bot API 7.2+ The biometric token was updated. The handler receives whether it was updated. */
    data object BiometricTokenUpdated : WebAppEvent<Boolean>("biometricTokenUpdated") {
        override fun payload(raw: JsAny?): Boolean = field(raw, "isUpdated").isTrue()
    }

    /** Bot API 8.0+ The Mini App enters or exits fullscreen mode. Read the state from [WebApp.isFullscreen]. */
    data object FullscreenChanged : Simple("fullscreenChanged")

    /** Bot API 8.0+ A request to enter fullscreen mode fails: `UNSUPPORTED` or `ALREADY_FULLSCREEN`. */
    data object FullscreenFailed : Failure("fullscreenFailed")

    /** Bot API 8.0+ The Mini App is added to the home screen. */
    data object HomeScreenAdded : Simple("homeScreenAdded")

    /** Bot API 8.0+ The home screen status was checked. */
    data object HomeScreenChecked : WebAppEvent<HomeScreenStatus>("homeScreenChecked") {
        override fun payload(raw: JsAny?): HomeScreenStatus = HomeScreenStatus.from(field(raw, "status").asStringOrNull())
    }

    /** Bot API 8.0+ Accelerometer tracking has started. */
    data object AccelerometerStarted : Simple("accelerometerStarted")

    /** Bot API 8.0+ Accelerometer tracking has stopped. */
    data object AccelerometerStopped : Simple("accelerometerStopped")

    /** Bot API 8.0+ New accelerometer data is available in [WebApp.accelerometer]. */
    data object AccelerometerChanged : Simple("accelerometerChanged")

    /** Bot API 8.0+ Starting accelerometer tracking fails: `UNSUPPORTED`. */
    data object AccelerometerFailed : Failure("accelerometerFailed")

    /** Bot API 8.0+ Device orientation tracking has started. */
    data object DeviceOrientationStarted : Simple("deviceOrientationStarted")

    /** Bot API 8.0+ Device orientation tracking has stopped. */
    data object DeviceOrientationStopped : Simple("deviceOrientationStopped")

    /** Bot API 8.0+ New orientation data is available in [WebApp.deviceOrientation]. */
    data object DeviceOrientationChanged : Simple("deviceOrientationChanged")

    /** Bot API 8.0+ Starting device orientation tracking fails: `UNSUPPORTED`. */
    data object DeviceOrientationFailed : Failure("deviceOrientationFailed")

    /** Bot API 8.0+ Gyroscope tracking has started. */
    data object GyroscopeStarted : Simple("gyroscopeStarted")

    /** Bot API 8.0+ Gyroscope tracking has stopped. */
    data object GyroscopeStopped : Simple("gyroscopeStopped")

    /** Bot API 8.0+ New gyroscope data is available in [WebApp.gyroscope]. */
    data object GyroscopeChanged : Simple("gyroscopeChanged")

    /** Bot API 8.0+ Starting gyroscope tracking fails: `UNSUPPORTED`. */
    data object GyroscopeFailed : Failure("gyroscopeFailed")

    /** Bot API 8.0+ The [LocationManager] object is changed. */
    data object LocationManagerUpdated : Simple("locationManagerUpdated")

    /** Bot API 8.0+ Location data is requested. The handler receives the location, or `null` if it is unavailable. */
    data object LocationRequested : WebAppEvent<LocationData?>("locationRequested") {
        override fun payload(raw: JsAny?): LocationData? = LocationData.fromOrNull(field(raw, "locationData"))
    }

    /** Bot API 8.0+ The message is shared by the user. */
    data object ShareMessageSent : Simple("shareMessageSent")

    /**
     * Bot API 8.0+ Sharing the message fails: `UNSUPPORTED`, `MESSAGE_EXPIRED`, `MESSAGE_SEND_FAILED`,
     * `USER_DECLINED` or `UNKNOWN_ERROR`.
     */
    data object ShareMessageFailed : Failure("shareMessageFailed")

    /** Bot API 8.0+ The emoji status is set. */
    data object EmojiStatusSet : Simple("emojiStatusSet")

    /**
     * Bot API 8.0+ Setting the emoji status fails: `UNSUPPORTED`, `SUGGESTED_EMOJI_INVALID`, `DURATION_INVALID`,
     * `USER_DECLINED`, `SERVER_ERROR` or `UNKNOWN_ERROR`.
     */
    data object EmojiStatusFailed : Failure("emojiStatusFailed")

    /** Bot API 8.0+ Emoji status permission was requested. The handler receives whether the user granted it. */
    data object EmojiStatusAccessRequested : WebAppEvent<Boolean>("emojiStatusAccessRequested") {
        override fun payload(raw: JsAny?): Boolean = field(raw, "status").asStringOrNull() == "allowed"
    }

    /** Bot API 8.0+ The user responds to a file download request. The handler receives whether the download has started. */
    data object FileDownloadRequested : WebAppEvent<Boolean>("fileDownloadRequested") {
        override fun payload(raw: JsAny?): Boolean = field(raw, "status").asStringOrNull() == "downloading"
    }
}

/** Parameters of [WebAppEvent.InvoiceClosed]. */
data class InvoiceClosedData(
    val url: String,
    val status: InvoiceStatus,
)

/** A handler registered with [WebApp.onEvent]. */
fun interface EventSubscription {

    /** Removes the handler. */
    fun unsubscribe()
}

private fun field(raw: JsAny?, name: String): JsAny? = if (raw.isNullOrUndefined()) null else jsGet(raw!!, name)
