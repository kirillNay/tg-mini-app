@file:Suppress("PropertyName", "FunctionName")

package com.kirillNay.telegram.miniapp.webApp.internal

// Raw bindings to telegram-web-app.js. Member names match the JS names exactly.
// Values that telegram-web-app.js may leave undefined or deliver as strings are typed as nullable or JsAny?.

internal external interface WebAppJs : JsAny {
    val initData: String
    val initDataUnsafe: InitDataJs
    val version: String
    val platform: String
    val colorScheme: String?
    val themeParams: ThemeParamsJs
    val isActive: JsAny?
    val isExpanded: Boolean
    val viewportHeight: JsAny?
    val viewportStableHeight: JsAny?
    val headerColor: String?
    val backgroundColor: String?
    val bottomBarColor: String?
    val isClosingConfirmationEnabled: Boolean
    val isVerticalSwipesEnabled: JsAny?
    val isFullscreen: JsAny?
    val isOrientationLocked: JsAny?
    val safeAreaInset: InsetJs?
    val contentSafeAreaInset: InsetJs?

    val BackButton: BackButtonJs
    val MainButton: BottomButtonJs
    val SecondaryButton: BottomButtonJs
    val SettingsButton: SettingsButtonJs
    val HapticFeedback: HapticFeedbackJs
    val CloudStorage: CloudStorageJs
    val DeviceStorage: DeviceStorageJs
    val SecureStorage: SecureStorageJs
    val BiometricManager: BiometricManagerJs
    val Accelerometer: MotionSensorJs
    val Gyroscope: MotionSensorJs
    val DeviceOrientation: DeviceOrientationJs
    val LocationManager: LocationManagerJs

    fun isVersionAtLeast(version: String): Boolean
    fun setHeaderColor(color: String)
    fun setBackgroundColor(color: String)
    fun setBottomBarColor(color: String)
    fun enableClosingConfirmation()
    fun disableClosingConfirmation()
    fun enableVerticalSwipes()
    fun disableVerticalSwipes()
    fun requestFullscreen()
    fun exitFullscreen()
    fun lockOrientation()
    fun unlockOrientation()
    fun addToHomeScreen()
    fun checkHomeScreenStatus(callback: (JsAny?) -> Unit)
    fun onEvent(eventType: String, eventHandler: JsAny)
    fun offEvent(eventType: String, eventHandler: JsAny)
    fun sendData(data: String)
    fun switchInlineQuery(query: String, chooseChatTypes: JsArray<JsString>?)
    fun openLink(url: String, options: JsAny?)
    fun openTelegramLink(url: String)
    fun openInvoice(url: String, callback: (JsAny?) -> Unit)
    fun shareToStory(mediaUrl: String, params: JsAny?)
    fun shareMessage(msgId: String, callback: (JsAny?) -> Unit)
    fun setEmojiStatus(customEmojiId: String, params: JsAny?, callback: (JsAny?) -> Unit)
    fun requestEmojiStatusAccess(callback: (JsAny?) -> Unit)
    fun downloadFile(params: JsAny, callback: (JsAny?) -> Unit)
    fun hideKeyboard()
    fun showPopup(params: JsAny, callback: (JsAny?) -> Unit)
    fun showAlert(message: String, callback: () -> Unit)
    fun showConfirm(message: String, callback: (JsAny?) -> Unit)
    fun showScanQrPopup(params: JsAny, callback: (JsAny?) -> Boolean)
    fun closeScanQrPopup()
    fun readTextFromClipboard(callback: (JsAny?) -> Unit)
    fun requestWriteAccess(callback: (JsAny?) -> Unit)
    fun requestContact(callback: (JsAny?) -> Unit)
    fun requestChat(reqId: String, callback: (JsAny?) -> Unit)
    fun ready()
    fun expand()
    fun close()
}

internal external interface InitDataJs : JsAny {
    val query_id: String?
    val chat_join_request_query_id: String?
    val user: UserJs?
    val receiver: UserJs?
    val chat: ChatJs?
    val chat_type: String?
    val chat_instance: String?
    val start_param: String?
    val can_send_after: JsAny?
    val auth_date: JsAny?
    val hash: String?
    val signature: String?
}

internal external interface UserJs : JsAny {
    val id: JsAny?
    val is_bot: JsAny?
    val first_name: String?
    val last_name: String?
    val username: String?
    val language_code: String?
    val is_premium: JsAny?
    val added_to_attachment_menu: JsAny?
    val allows_write_to_pm: JsAny?
    val photo_url: String?
}

internal external interface ChatJs : JsAny {
    val id: JsAny?
    val type: String?
    val title: String?
    val username: String?
    val photo_url: String?
}

internal external interface ThemeParamsJs : JsAny {
    val bg_color: String?
    val text_color: String?
    val hint_color: String?
    val link_color: String?
    val button_color: String?
    val button_text_color: String?
    val secondary_bg_color: String?
    val header_bg_color: String?
    val bottom_bar_bg_color: String?
    val accent_text_color: String?
    val section_bg_color: String?
    val section_header_text_color: String?
    val section_separator_color: String?
    val subtitle_text_color: String?
    val destructive_text_color: String?
}

internal external interface InsetJs : JsAny {
    val top: JsAny?
    val bottom: JsAny?
    val left: JsAny?
    val right: JsAny?
}

internal external interface BackButtonJs : JsAny {
    val isVisible: Boolean
    fun onClick(callback: JsAny)
    fun offClick(callback: JsAny)
    fun show()
    fun hide()
}

internal external interface SettingsButtonJs : JsAny {
    val isVisible: Boolean
    fun onClick(callback: JsAny)
    fun offClick(callback: JsAny)
    fun show()
    fun hide()
}

internal external interface BottomButtonJs : JsAny {
    val type: String?
    val text: String
    val color: String
    val textColor: String
    val isVisible: Boolean
    val isActive: Boolean
    val hasShineEffect: JsAny?
    val position: String?
    val isProgressVisible: Boolean
    val iconCustomEmojiId: String?
    fun setText(text: String)
    fun onClick(callback: JsAny)
    fun offClick(callback: JsAny)
    fun show()
    fun hide()
    fun enable()
    fun disable()
    fun showProgress(leaveActive: Boolean)
    fun hideProgress()
    fun setParams(params: JsAny)
}

internal external interface HapticFeedbackJs : JsAny {
    fun impactOccurred(style: String)
    fun notificationOccurred(type: String)
    fun selectionChanged()
}

internal external interface CloudStorageJs : JsAny {
    fun setItem(key: String, value: String, callback: (JsAny?, JsAny?) -> Unit)
    fun getItem(key: String, callback: (JsAny?, JsAny?) -> Unit)
    fun getItems(keys: JsArray<JsString>, callback: (JsAny?, JsAny?) -> Unit)
    fun removeItem(key: String, callback: (JsAny?, JsAny?) -> Unit)
    fun removeItems(keys: JsArray<JsString>, callback: (JsAny?, JsAny?) -> Unit)
    fun getKeys(callback: (JsAny?, JsAny?) -> Unit)
}

internal external interface DeviceStorageJs : JsAny {
    fun setItem(key: String, value: String, callback: (JsAny?, JsAny?) -> Unit)
    fun getItem(key: String, callback: (JsAny?, JsAny?) -> Unit)
    fun removeItem(key: String, callback: (JsAny?, JsAny?) -> Unit)
    fun clear(callback: (JsAny?, JsAny?) -> Unit)
}

internal external interface SecureStorageJs : JsAny {
    fun setItem(key: String, value: String, callback: (JsAny?, JsAny?) -> Unit)
    fun getItem(key: String, callback: (JsAny?, JsAny?, JsAny?) -> Unit)
    fun restoreItem(key: String, callback: (JsAny?, JsAny?) -> Unit)
    fun removeItem(key: String, callback: (JsAny?, JsAny?) -> Unit)
    fun clear(callback: (JsAny?, JsAny?) -> Unit)
}

internal external interface BiometricManagerJs : JsAny {
    val isInited: Boolean
    val isBiometricAvailable: Boolean
    val biometricType: String?
    val isAccessRequested: Boolean
    val isAccessGranted: Boolean
    val isBiometricTokenSaved: Boolean
    val deviceId: String?
    fun init(callback: () -> Unit)
    fun requestAccess(params: JsAny, callback: (JsAny?) -> Unit)
    fun authenticate(params: JsAny, callback: (JsAny?, JsAny?) -> Unit)
    fun updateBiometricToken(token: String, callback: (JsAny?) -> Unit)
    fun openSettings()
}

/** Shared shape of `Accelerometer` and `Gyroscope`. */
internal external interface MotionSensorJs : JsAny {
    val isStarted: Boolean
    val x: JsAny?
    val y: JsAny?
    val z: JsAny?
    fun start(params: JsAny, callback: (JsAny?) -> Unit)
    fun stop(callback: (JsAny?) -> Unit)
}

internal external interface DeviceOrientationJs : JsAny {
    val isStarted: Boolean
    val absolute: Boolean
    val alpha: JsAny?
    val beta: JsAny?
    val gamma: JsAny?
    fun start(params: JsAny, callback: (JsAny?) -> Unit)
    fun stop(callback: (JsAny?) -> Unit)
}

internal external interface LocationManagerJs : JsAny {
    val isInited: Boolean
    val isLocationAvailable: Boolean
    val isAccessRequested: Boolean
    val isAccessGranted: Boolean
    fun init(callback: () -> Unit)
    fun getLocation(callback: (JsAny?) -> Unit)
    fun openSettings()
}

internal external interface LocationDataJs : JsAny {
    val latitude: JsAny?
    val longitude: JsAny?
    val altitude: JsAny?
    val course: JsAny?
    val speed: JsAny?
    val horizontal_accuracy: JsAny?
    val vertical_accuracy: JsAny?
    val course_accuracy: JsAny?
    val speed_accuracy: JsAny?
}
