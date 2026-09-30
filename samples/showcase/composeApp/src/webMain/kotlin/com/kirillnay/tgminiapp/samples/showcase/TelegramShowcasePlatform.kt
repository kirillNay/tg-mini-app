package com.kirillnay.tgminiapp.samples.showcase

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.kirillNay.telegram.miniapp.compose.TelegramStyle
import com.kirillNay.telegram.miniapp.webApp.ChatType
import com.kirillNay.telegram.miniapp.webApp.DownloadFileParams
import com.kirillNay.telegram.miniapp.webApp.EmojiStatusParams
import com.kirillNay.telegram.miniapp.webApp.HapticFeedback
import com.kirillNay.telegram.miniapp.webApp.ScanQrPopupParams
import com.kirillNay.telegram.miniapp.webApp.StoryShareParams
import com.kirillNay.telegram.miniapp.webApp.StoryWidgetLink
import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.WebAppEvent
import com.kirillNay.telegram.miniapp.webApp.buttons.BottomButtonParams
import com.kirillNay.telegram.miniapp.webApp.buttons.BottomButtonPosition
import com.kirillNay.telegram.miniapp.webApp.popup.PopupButton
import com.kirillNay.telegram.miniapp.webApp.popup.PopupParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Runs the catalog against the real Telegram WebApp through tg-mini-app. */
class TelegramShowcasePlatform(
    private val webApp: WebApp,
) : ShowcasePlatform {

    override val hostName: String = "Telegram (${webApp.platform})"

    override var palette: ShowcasePalette by mutableStateOf(ShowcasePalette(false, black, black, black, black, black, black, black, black, black))
        private set

    override var contentPadding: PaddingValues by mutableStateOf(PaddingValues(0.dp))
        private set

    private val _launchData = MutableStateFlow(collectLaunchData())
    override val launchData: StateFlow<List<InfoGroup>> = _launchData

    private val _eventLog = MutableStateFlow<List<LogEntry>>(emptyList())
    override val eventLog: StateFlow<List<LogEntry>> = _eventLog
    private var nextEventIndex = 1

    override val hasNativeBackButton = true

    private var backHandler: (() -> Unit)? = null
    private val onBackClick: () -> Unit = { backHandler?.invoke() }

    init {
        allEvents.forEach { event ->
            webApp.onEvent(event) { payload ->
                log(event.type, if (payload == Unit) "" else payload.toString())
                _launchData.value = collectLaunchData()
            }
        }
        webApp.backButton.onClick(onBackClick)
    }

    /** Applies the current Telegram theme and insets. Called on every [TelegramStyle] change. */
    fun update(style: TelegramStyle) {
        val colors = style.colors
        palette = ShowcasePalette(
            isDark = style.isDark,
            background = colors.secondaryBackgroundColor,
            surface = colors.sectionBackgroundColor,
            primary = colors.buttonColor,
            onPrimary = colors.buttonTextColor,
            text = colors.textColor,
            hint = colors.hintColor,
            accent = colors.accentTextColor,
            destructive = colors.destructiveTextColor,
            separator = colors.sectionSeparatorColor,
        )
        val device = webApp.safeAreaInset
        val content = webApp.contentSafeAreaInset
        contentPadding = PaddingValues(
            start = (device.left + content.left).dp,
            top = (device.top + content.top).dp,
            end = (device.right + content.right).dp,
            bottom = (device.bottom + content.bottom).dp,
        )
        _launchData.value = collectLaunchData()
    }

    override fun availability(feature: Feature): Availability {
        val required = feature.minBotApi ?: return Availability.Available
        return if (webApp.isVersionAtLeast(required)) {
            Availability.Available
        } else {
            Availability.RequiresBotApi(required, webApp.version)
        }
    }

    override fun onBackAvailable(onBack: (() -> Unit)?) {
        backHandler = onBack
        if (onBack == null) webApp.backButton.hide() else webApp.backButton.show()
    }

    override fun clearEventLog() {
        _eventLog.value = emptyList()
    }

    override suspend fun run(feature: Feature, inputs: Map<String, String>): FeatureResult {
        fun input(key: String) = inputs[key].orEmpty()
        fun isOn(key: String) = input(key) == "on"
        fun required(key: String): String? = input(key).trim().ifEmpty { null }

        return when (feature.id) {
            // App behavior
            "expand" -> done("expand()") { webApp.expand() }
            "closing_confirmation" -> done("closing confirmation ${input("state")}") {
                if (isOn("state")) webApp.enableClosingConfirmation() else webApp.disableClosingConfirmation()
            }
            "vertical_swipes" -> done("vertical swipes ${input("state")}") {
                if (isOn("state")) webApp.enableVerticalSwipes() else webApp.disableVerticalSwipes()
            }
            "version_check" -> ok("isVersionAtLeast(${input("version")}) = ${webApp.isVersionAtLeast(input("version"))}, app version ${webApp.version}")
            "send_data" -> done("sendData()") { webApp.sendData(input("data")) }
            "close" -> done("close()") { webApp.close() }

            // Appearance
            "header_color" -> done("header color ${input("color")}") { webApp.setHeaderColor(input("color")) }
            "background_color" -> done("background color ${input("color")}") { webApp.setBackgroundColor(input("color")) }
            "bottom_bar_color" -> done("bottom bar color ${input("color")}") { webApp.setBottomBarColor(input("color")) }
            "fullscreen" -> done("fullscreen ${input("state")}, watch the event log") {
                if (input("state") == "enter") webApp.requestFullscreen() else webApp.exitFullscreen()
            }
            "orientation_lock" -> done("orientation ${input("state")}") {
                if (input("state") == "lock") webApp.lockOrientation() else webApp.unlockOrientation()
            }

            // Buttons
            "main_button" -> done("main button: ${input("action")}") {
                val button = webApp.mainButton
                when (input("action")) {
                    "show" -> button.setParams(
                        BottomButtonParams(
                            text = input("text"),
                            hasShineEffect = if (webApp.isVersionAtLeast("7.10")) isOn("shine") else null,
                            isActive = true,
                            isVisible = true,
                        ),
                    )
                    "hide" -> button.hide()
                    "progress" -> button.showProgress()
                    "stop progress" -> button.hideProgress()
                    "disable" -> button.disable()
                    "enable" -> button.enable()
                }
            }
            "secondary_button" -> done("secondary button: ${input("action")}") {
                val button = webApp.secondaryButton
                if (input("action") == "show") {
                    button.setParams(
                        BottomButtonParams(
                            text = input("text"),
                            position = BottomButtonPosition.entries.first { it.value == input("position") },
                            isVisible = true,
                        ),
                    )
                } else {
                    button.hide()
                }
            }
            "back_button" -> done("back button: ${input("action")}") {
                if (input("action") == "show") webApp.backButton.show() else webApp.backButton.hide()
            }
            "settings_button" -> done("settings button: ${input("action")}") {
                if (input("action") == "show") webApp.settingsButton.show() else webApp.settingsButton.hide()
            }

            // Popups and input
            "alert" -> {
                webApp.awaitAlert(input("message"))
                ok("Alert closed")
            }
            "confirm" -> ok(if (webApp.awaitConfirm(input("message"))) "Confirmed: OK" else "Cancelled")
            "popup" -> {
                val buttons = when (input("buttons")) {
                    "ok + cancel" -> listOf(PopupButton("ok", PopupButton.Type.OK), PopupButton("cancel", PopupButton.Type.CANCEL))
                    "ok + destructive" -> listOf(
                        PopupButton("ok", PopupButton.Type.OK),
                        PopupButton("delete", PopupButton.Type.DESTRUCTIVE, "Delete"),
                    )
                    else -> listOf(PopupButton("close", PopupButton.Type.CLOSE))
                }
                val pressed = webApp.awaitPopup(PopupParams(message = input("message"), title = required("title"), buttons = buttons))
                ok("Pressed button id: \"$pressed\"")
            }
            "scan_qr" -> done("Scanner opened, scanned text appears in the event log") {
                webApp.showScanQrPopup(ScanQrPopupParams(required("text"))) { true }
            }
            "clipboard" -> ok("Clipboard: ${webApp.awaitClipboardText() ?: "no access"}")
            "hide_keyboard" -> done("hideKeyboard()") { webApp.hideKeyboard() }

            // Haptics
            "haptic_impact" -> done("impact ${input("style")}") {
                webApp.hapticFeedback.impactOccurred(HapticFeedback.ImpactStyle.entries.first { it.value == input("style") })
            }
            "haptic_notification" -> done("notification ${input("type")}") {
                webApp.hapticFeedback.notificationOccurred(HapticFeedback.NotificationType.entries.first { it.value == input("type") })
            }
            "haptic_selection" -> done("selection changed") { webApp.hapticFeedback.selectionChanged() }

            // Links and payments
            "open_link" -> done("openLink()") { webApp.openLink(input("url"), tryInstantView = isOn("instant_view")) }
            "open_telegram_link" -> done("openTelegramLink()") { webApp.openTelegramLink(input("url")) }
            "open_invoice" -> required("url")?.let { ok("Invoice closed: ${webApp.awaitInvoice(it)}") }
                ?: missing("an invoice link")
            "switch_inline_query" -> done("switchInlineQuery()") {
                val types = ChatType.entries.filter { it.value == input("chat_types") }
                webApp.switchInlineQuery(input("query"), types)
            }

            // Sharing and home screen
            "share_story" -> done("Story editor opened") {
                webApp.shareToStory(
                    input("media_url"),
                    StoryShareParams(text = required("text"), widgetLink = required("widget_url")?.let { StoryWidgetLink(it) }),
                )
            }
            "share_message" -> required("message_id")?.let { ok(if (webApp.awaitShareMessage(it)) "Message sent" else "Not sent") }
                ?: missing("a prepared message id")
            "download_file" -> ok(
                if (webApp.awaitDownloadFile(DownloadFileParams(input("url"), input("file_name")))) "Download accepted" else "Download declined",
            )
            "emoji_status" -> required("custom_emoji_id")?.let {
                val set = webApp.awaitEmojiStatus(it, EmojiStatusParams(input("duration").toIntOrNull()))
                ok(if (set) "Emoji status set" else "Emoji status not set")
            } ?: missing("a custom emoji id")
            "emoji_status_access" -> ok(if (webApp.awaitEmojiStatusAccess()) "Access granted" else "Access denied")
            "home_screen_add" -> done("Prompt shown, watch for homeScreenAdded in the event log") { webApp.addToHomeScreen() }
            "home_screen_status" -> ok("Home screen status: ${webApp.awaitHomeScreenStatus()}")

            // Permissions and chats
            "write_access" -> ok(if (webApp.awaitWriteAccess()) "Write access granted" else "Write access denied")
            "contact" -> ok(if (webApp.awaitContact()) "Phone number shared" else "Phone number not shared")
            "request_chat" -> required("request_id")?.let { ok(if (webApp.awaitRequestChat(it)) "Chat selected" else "Cancelled") }
                ?: missing("a request id")

            // Storages
            "cloud_set" -> webApp.cloudStorage.setItem(input("key"), input("value")).report { "Stored: $it" }
            "cloud_get" -> webApp.cloudStorage.getItem(input("key")).report { "Value: \"$it\"" }
            "cloud_remove" -> webApp.cloudStorage.removeItem(input("key")).report { "Removed: $it" }
            "cloud_keys" -> webApp.cloudStorage.getKeys().report { "Keys: $it" }
            "device_set" -> webApp.deviceStorage.setItem(input("key"), input("value")).report { "Stored: $it" }
            "device_get" -> webApp.deviceStorage.getItem(input("key")).report { "Value: ${it?.let { v -> "\"$v\"" } ?: "none"}" }
            "device_remove" -> webApp.deviceStorage.removeItem(input("key")).report { "Removed: $it" }
            "device_clear" -> webApp.deviceStorage.clear().report { "Cleared: $it" }
            "secure_set" -> webApp.secureStorage.setItem(input("key"), input("value")).report { "Stored: $it" }
            "secure_get" -> webApp.secureStorage.getItem(input("key")).report { "Value: ${it.value ?: "none"}, can restore: ${it.canRestore}" }
            "secure_restore" -> webApp.secureStorage.restoreItem(input("key")).report { "Restored: ${it ?: "none"}" }
            "secure_remove" -> webApp.secureStorage.removeItem(input("key")).report { "Removed: $it" }
            "secure_clear" -> webApp.secureStorage.clear().report { "Cleared: $it" }

            // Biometrics
            "bio_init" -> {
                val manager = webApp.biometricManager
                manager.init()
                ok(
                    "available: ${manager.isBiometricAvailable}, type: ${manager.biometricType}, " +
                        "access requested: ${manager.isAccessRequested}, granted: ${manager.isAccessGranted}, " +
                        "token saved: ${manager.isBiometricTokenSaved}",
                )
            }
            "bio_access" -> ok("Access granted: ${webApp.biometricManager.requestAccess(required("reason"))}")
            "bio_authenticate" -> ok(webApp.biometricManager.authenticate(required("reason")).toString())
            "bio_token" -> ok("Token updated: ${webApp.biometricManager.updateBiometricToken(input("token"))}")
            "bio_settings" -> done("Settings opened") { webApp.biometricManager.openSettings() }

            // Location
            "location_init" -> {
                val manager = webApp.locationManager
                manager.init()
                ok("available: ${manager.isLocationAvailable}, access requested: ${manager.isAccessRequested}, granted: ${manager.isAccessGranted}")
            }
            "location_get" -> ok(webApp.locationManager.getLocation()?.toString() ?: "No access to location")
            "location_settings" -> done("Settings opened") { webApp.locationManager.openSettings() }

            // Motion sensors
            "accelerometer" -> ok(
                if (input("action") == "start") {
                    "Started: ${webApp.accelerometer.start(input("refresh_rate").toIntOrNull())}"
                } else {
                    "Stopped: ${webApp.accelerometer.stop()}"
                },
            )
            "gyroscope" -> ok(
                if (input("action") == "start") {
                    "Started: ${webApp.gyroscope.start(input("refresh_rate").toIntOrNull())}"
                } else {
                    "Stopped: ${webApp.gyroscope.stop()}"
                },
            )
            "device_orientation" -> ok(
                if (input("action") == "start") {
                    "Started: ${webApp.deviceOrientation.start(input("refresh_rate").toIntOrNull(), isOn("absolute"))}"
                } else {
                    "Stopped: ${webApp.deviceOrientation.stop()}"
                },
            )

            else -> FeatureResult.Failure("Unknown feature ${feature.id}")
        }
    }

    private fun log(name: String, details: String) {
        _eventLog.update { it + LogEntry(nextEventIndex++, name, details) }
    }

    private fun collectLaunchData(): List<InfoGroup> {
        val init = webApp.initDataUnsafe
        val theme = webApp.themeParams
        val device = webApp.safeAreaInset
        val content = webApp.contentSafeAreaInset
        return listOf(
            InfoGroup(
                "Telegram app",
                listOf(
                    "Platform" to webApp.platform,
                    "Bot API version" to webApp.version,
                    "Color scheme" to webApp.colorScheme.value,
                    "Active" to webApp.isActive.toString(),
                    "Expanded" to webApp.isExpanded.toString(),
                    "Fullscreen" to webApp.isFullscreen.toString(),
                    "Orientation locked" to webApp.isOrientationLocked.toString(),
                    "Vertical swipes" to webApp.isVerticalSwipesEnabled.toString(),
                    "Closing confirmation" to webApp.isClosingConfirmationEnabled.toString(),
                    "Header color" to webApp.headerColor.orDash(),
                    "Background color" to webApp.backgroundColor.orDash(),
                    "Bottom bar color" to webApp.bottomBarColor.orDash(),
                ),
            ),
            InfoGroup(
                "Init data (unsafe)",
                listOf(
                    "User" to (init.user?.let { "${it.firstName} ${it.lastName.orEmpty()}".trim() } ?: "—"),
                    "Username" to (init.user?.username?.let { "@$it" } ?: "—"),
                    "User id" to (init.user?.id?.toString() ?: "—"),
                    "Language" to init.user?.languageCode.orDash(),
                    "Premium" to (init.user?.isPremium?.toString() ?: "—"),
                    "Receiver" to (init.receiver?.firstName ?: "—"),
                    "Chat" to (init.chat?.let { "${it.title} (${it.type})" } ?: "—"),
                    "Chat type" to init.chatType.orDash(),
                    "Chat instance" to init.chatInstance.orDash(),
                    "Start param" to init.startParam.orDash(),
                    "Query id" to init.queryId.orDash(),
                    "Auth date" to (init.authDate?.toString() ?: "—"),
                    "Hash" to if (init.hash.isNullOrEmpty()) "—" else "present",
                    "Signature" to if (init.signature.isNullOrEmpty()) "—" else "present",
                ),
            ),
            InfoGroup(
                "Theme params",
                listOf(
                    "bg_color" to theme.bgColor.orDash(),
                    "text_color" to theme.textColor.orDash(),
                    "hint_color" to theme.hintColor.orDash(),
                    "link_color" to theme.linkColor.orDash(),
                    "button_color" to theme.buttonColor.orDash(),
                    "button_text_color" to theme.buttonTextColor.orDash(),
                    "secondary_bg_color" to theme.secondaryBgColor.orDash(),
                    "header_bg_color" to theme.headerBgColor.orDash(),
                    "bottom_bar_bg_color" to theme.bottomBarBgColor.orDash(),
                    "accent_text_color" to theme.accentTextColor.orDash(),
                    "section_bg_color" to theme.sectionBgColor.orDash(),
                    "section_header_text_color" to theme.sectionHeaderTextColor.orDash(),
                    "section_separator_color" to theme.sectionSeparatorColor.orDash(),
                    "subtitle_text_color" to theme.subtitleTextColor.orDash(),
                    "destructive_text_color" to theme.destructiveTextColor.orDash(),
                ),
            ),
            InfoGroup(
                "Viewport and safe area",
                listOf(
                    "Viewport height" to webApp.viewportHeight.toInt().toString(),
                    "Stable height" to webApp.viewportStableHeight.toInt().toString(),
                    "Safe area" to "top ${device.top}, bottom ${device.bottom}, left ${device.left}, right ${device.right}",
                    "Content safe area" to "top ${content.top}, bottom ${content.bottom}, left ${content.left}, right ${content.right}",
                ),
            ),
            InfoGroup(
                "Motion sensors",
                listOf(
                    "Accelerometer" to with(webApp.accelerometer) { if (isStarted) "x ${x.fmt()}, y ${y.fmt()}, z ${z.fmt()}" else "stopped" },
                    "Gyroscope" to with(webApp.gyroscope) { if (isStarted) "x ${x.fmt()}, y ${y.fmt()}, z ${z.fmt()}" else "stopped" },
                    "Orientation" to with(webApp.deviceOrientation) {
                        if (isStarted) "α ${alpha.fmt()}, β ${beta.fmt()}, γ ${gamma.fmt()}, absolute $absolute" else "stopped"
                    },
                ),
            ),
        )
    }

    private companion object {

        val black = androidx.compose.ui.graphics.Color.Black

        /** Every event wrapped by tg-mini-app; all of them are shown in the event log. */
        val allEvents: List<WebAppEvent<*>> = listOf(
            WebAppEvent.Activated, WebAppEvent.Deactivated, WebAppEvent.ThemeChanged, WebAppEvent.ViewportChanged,
            WebAppEvent.SafeAreaChanged, WebAppEvent.ContentSafeAreaChanged, WebAppEvent.MainButtonClicked,
            WebAppEvent.SecondaryButtonClicked, WebAppEvent.BackButtonClicked, WebAppEvent.SettingsButtonClicked,
            WebAppEvent.InvoiceClosed, WebAppEvent.PopupClosed, WebAppEvent.QrTextReceived, WebAppEvent.ScanQrPopupClosed,
            WebAppEvent.ClipboardTextReceived, WebAppEvent.WriteAccessRequested, WebAppEvent.ContactRequested,
            WebAppEvent.BiometricManagerUpdated, WebAppEvent.BiometricAuthRequested, WebAppEvent.BiometricTokenUpdated,
            WebAppEvent.FullscreenChanged, WebAppEvent.FullscreenFailed, WebAppEvent.HomeScreenAdded,
            WebAppEvent.HomeScreenChecked, WebAppEvent.AccelerometerStarted, WebAppEvent.AccelerometerStopped,
            WebAppEvent.AccelerometerChanged, WebAppEvent.AccelerometerFailed, WebAppEvent.DeviceOrientationStarted,
            WebAppEvent.DeviceOrientationStopped, WebAppEvent.DeviceOrientationChanged, WebAppEvent.DeviceOrientationFailed,
            WebAppEvent.GyroscopeStarted, WebAppEvent.GyroscopeStopped, WebAppEvent.GyroscopeChanged, WebAppEvent.GyroscopeFailed,
            WebAppEvent.LocationManagerUpdated, WebAppEvent.LocationRequested, WebAppEvent.ShareMessageSent,
            WebAppEvent.ShareMessageFailed, WebAppEvent.EmojiStatusSet, WebAppEvent.EmojiStatusFailed,
            WebAppEvent.EmojiStatusAccessRequested, WebAppEvent.FileDownloadRequested,
        )
    }
}

private fun ok(message: String) = FeatureResult.Success(message)

private fun missing(what: String) = FeatureResult.Failure("Enter $what first")

private inline fun done(message: String, action: () -> Unit): FeatureResult {
    action()
    return FeatureResult.Success(message)
}

private inline fun <T> Result<T>.report(describe: (T) -> String): FeatureResult = fold(
    onSuccess = { FeatureResult.Success(describe(it)) },
    onFailure = { FeatureResult.Failure("Error: ${it.message}") },
)

private fun String?.orDash() = if (isNullOrEmpty()) "—" else this

private fun Double?.fmt() = this?.let { (kotlin.math.round(it * 100) / 100).toString() } ?: "—"
