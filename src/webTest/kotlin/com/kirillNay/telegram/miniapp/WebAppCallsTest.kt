package com.kirillNay.telegram.miniapp

import com.kirillNay.telegram.miniapp.webApp.BiometricAuthResult
import com.kirillNay.telegram.miniapp.webApp.BiometricType
import com.kirillNay.telegram.miniapp.webApp.ChatType
import com.kirillNay.telegram.miniapp.webApp.DownloadFileParams
import com.kirillNay.telegram.miniapp.webApp.EmojiStatusParams
import com.kirillNay.telegram.miniapp.webApp.HapticFeedback
import com.kirillNay.telegram.miniapp.webApp.HomeScreenStatus
import com.kirillNay.telegram.miniapp.webApp.InvoiceStatus
import com.kirillNay.telegram.miniapp.webApp.LocationData
import com.kirillNay.telegram.miniapp.webApp.ScanQrPopupParams
import com.kirillNay.telegram.miniapp.webApp.SecureStorage
import com.kirillNay.telegram.miniapp.webApp.StoryShareParams
import com.kirillNay.telegram.miniapp.webApp.StoryWidgetLink
import com.kirillNay.telegram.miniapp.webApp.WebApp
import com.kirillNay.telegram.miniapp.webApp.WebAppException
import com.kirillNay.telegram.miniapp.webApp.buttons.BottomButtonParams
import com.kirillNay.telegram.miniapp.webApp.buttons.BottomButtonPosition
import com.kirillNay.telegram.miniapp.webApp.popup.PopupButton
import com.kirillNay.telegram.miniapp.webApp.popup.PopupParams
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Checks that every public method calls the matching telegram-web-app.js method with the expected arguments. */
class WebAppCallsTest {

    private fun assertCall(expected: String, action: (WebApp) -> Unit) {
        val mock = RecordingMock()
        action(mock.webApp)
        assertEquals(expected, mock.lastCall())
    }

    @Test
    fun lifecycleAndAppearance() {
        assertCall("ready([])") { it.ready() }
        assertCall("expand([])") { it.expand() }
        assertCall("close([])") { it.close() }
        assertCall("isVersionAtLeast([\"8.0\"])") { it.isVersionAtLeast("8.0") }
        assertCall("setHeaderColor([\"bg_color\"])") { it.setHeaderColor("bg_color") }
        assertCall("setBackgroundColor([\"#ffffff\"])") { it.setBackgroundColor("#ffffff") }
        assertCall("setBottomBarColor([\"bottom_bar_bg_color\"])") { it.setBottomBarColor("bottom_bar_bg_color") }
        assertCall("enableClosingConfirmation([])") { it.enableClosingConfirmation() }
        assertCall("disableClosingConfirmation([])") { it.disableClosingConfirmation() }
        assertCall("enableVerticalSwipes([])") { it.enableVerticalSwipes() }
        assertCall("disableVerticalSwipes([])") { it.disableVerticalSwipes() }
        assertCall("requestFullscreen([])") { it.requestFullscreen() }
        assertCall("exitFullscreen([])") { it.exitFullscreen() }
        assertCall("lockOrientation([])") { it.lockOrientation() }
        assertCall("unlockOrientation([])") { it.unlockOrientation() }
        assertCall("addToHomeScreen([])") { it.addToHomeScreen() }
        assertCall("hideKeyboard([])") { it.hideKeyboard() }
    }

    @Test
    fun linksAndSharing() {
        assertCall("sendData([\"payload\"])") { it.sendData("payload") }
        assertCall("switchInlineQuery([\"q\",null])") { it.switchInlineQuery("q") }
        assertCall("switchInlineQuery([\"\",[\"users\",\"channels\"]])") {
            it.switchInlineQuery(chatTypes = listOf(ChatType.USERS, ChatType.CHANNELS))
        }
        assertCall("openLink([\"https://a.b\",{}])") { it.openLink("https://a.b") }
        assertCall("openLink([\"https://a.b\",{\"try_instant_view\":true}])") { it.openLink("https://a.b", tryInstantView = true) }
        assertCall("openTelegramLink([\"https://t.me/x\"])") { it.openTelegramLink("https://t.me/x") }
        assertCall("shareToStory([\"https://a.b/i.png\",null])") { it.shareToStory("https://a.b/i.png") }
        assertCall("shareToStory([\"https://a.b/i.png\",{\"text\":\"hi\",\"widget_link\":{\"url\":\"https://a.b\"}}])") {
            it.shareToStory("https://a.b/i.png", StoryShareParams(text = "hi", widgetLink = StoryWidgetLink("https://a.b")))
        }
        assertCall("downloadFile([{\"url\":\"https://a.b/f\",\"file_name\":\"f.txt\"},null])") {
            it.downloadFile(DownloadFileParams("https://a.b/f", "f.txt"))
        }
        assertCall("setEmojiStatus([\"123\",{\"duration\":60},null])") { it.setEmojiStatus("123", EmojiStatusParams(duration = 60)) }
        assertCall("showScanQrPopup([{\"text\":\"Scan\"},null])") { it.showScanQrPopup(ScanQrPopupParams("Scan")) }
        assertCall("closeScanQrPopup([])") { it.closeScanQrPopup() }
        assertCall("showPopup([{\"message\":\"m\"},null])") { it.showPopup(PopupParams(message = "m")) }
        assertCall("showPopup([{\"title\":\"t\",\"message\":\"m\",\"buttons\":[{\"id\":\"del\",\"type\":\"destructive\",\"text\":\"Delete\"}]},null])") {
            it.showPopup(PopupParams(message = "m", title = "t", buttons = listOf(PopupButton("del", PopupButton.Type.DESTRUCTIVE, "Delete"))))
        }
    }

    @Test
    fun callbackResultsAreMapped() = runTest {
        val mock = RecordingMock()
        val webApp = mock.webApp
        mock.respondWith("openInvoice", """["paid"]""")
        mock.respondWith("checkHomeScreenStatus", """["added"]""")
        mock.respondWith("showPopup", """["ok"]""")
        mock.respondWith("showConfirm", """[false]""")
        mock.respondWith("showAlert", """[]""")
        mock.respondWith("readTextFromClipboard", """[null]""")
        mock.respondWith("requestWriteAccess", """[true]""")
        mock.respondWith("requestContact", """[false]""")
        mock.respondWith("shareMessage", """[true]""")
        mock.respondWith("setEmojiStatus", """[true]""")
        mock.respondWith("requestEmojiStatusAccess", """[true]""")
        mock.respondWith("downloadFile", """[true]""")
        mock.respondWith("requestChat", """[true]""")

        assertEquals(InvoiceStatus.PAID, webApp.awaitInvoice("https://t.me/\$inv"))
        assertEquals(HomeScreenStatus.ADDED, webApp.awaitHomeScreenStatus())
        assertEquals("ok", webApp.awaitPopup(PopupParams(message = "m")))
        assertFalse(webApp.awaitConfirm("?"))
        webApp.awaitAlert("!")
        assertNull(webApp.awaitClipboardText())
        assertTrue(webApp.awaitWriteAccess())
        assertFalse(webApp.awaitContact())
        assertTrue(webApp.awaitShareMessage("msg"))
        assertTrue(webApp.awaitEmojiStatus("123"))
        assertTrue(webApp.awaitEmojiStatusAccess())
        assertTrue(webApp.awaitDownloadFile(DownloadFileParams("https://a.b/f", "f")))
        assertTrue(webApp.awaitRequestChat("req"))

        var status: InvoiceStatus? = null
        webApp.openInvoice("https://t.me/\$inv") { status = it }
        assertEquals(InvoiceStatus.PAID, status)
    }

    @Test
    fun buttonsAndHaptics() {
        assertCall("MainButton.setText([\"Pay\"])") { it.mainButton.setText("Pay") }
        assertCall("MainButton.showProgress([false])") { it.mainButton.showProgress() }
        assertCall("MainButton.hideProgress([])") { it.mainButton.hideProgress() }
        assertCall("MainButton.enable([])") { it.mainButton.enable() }
        assertCall("MainButton.disable([])") { it.mainButton.disable() }
        assertCall("SecondaryButton.show([])") { it.secondaryButton.show() }
        assertCall("SecondaryButton.hide([])") { it.secondaryButton.hide() }
        assertCall("SecondaryButton.setParams([{\"text\":\"Cancel\",\"has_shine_effect\":true,\"position\":\"top\",\"icon_custom_emoji_id\":\"5\"}])") {
            it.secondaryButton.setParams(
                BottomButtonParams(text = "Cancel", hasShineEffect = true, position = BottomButtonPosition.TOP, iconCustomEmojiId = "5")
            )
        }
        assertCall("SettingsButton.show([])") { it.settingsButton.show() }
        assertCall("SettingsButton.hide([])") { it.settingsButton.hide() }
        assertCall("BackButton.hide([])") { it.backButton.hide() }
        assertCall("HapticFeedback.impactOccurred([\"rigid\"])") { it.hapticFeedback.impactOccurred(HapticFeedback.ImpactStyle.RIGID) }
        assertCall("HapticFeedback.notificationOccurred([\"warning\"])") {
            it.hapticFeedback.notificationOccurred(HapticFeedback.NotificationType.WARNING)
        }
        assertCall("HapticFeedback.selectionChanged([])") { it.hapticFeedback.selectionChanged() }
    }

    @Test
    fun clickHandlersAreRegisteredOncePerLambda() {
        val mock = RecordingMock()
        val callback = {}

        mock.webApp.mainButton.onClick(callback).onClick(callback).offClick(callback).offClick(callback)

        assertEquals(listOf("MainButton.onClick([null])", "MainButton.onClick([null])", "MainButton.offClick([null])"), mock.calls())
    }

    @Test
    fun storages() = runTest {
        val mock = RecordingMock()
        val webApp = mock.webApp
        mock.respondWith("DeviceStorage.getItem", """[null, "local"]""")
        mock.respondWith("DeviceStorage.setItem", """[null, true]""")
        mock.respondWith("DeviceStorage.clear", """["UNSUPPORTED"]""")
        mock.respondWith("SecureStorage.getItem", """[null, null, true]""")
        mock.respondWith("SecureStorage.restoreItem", """[null, "restored"]""")
        mock.respondWith("CloudStorage.removeItems", """[null, true]""")

        assertEquals("local", webApp.deviceStorage.getItem("k").getOrThrow())
        assertTrue(webApp.deviceStorage.setItem("k", "v").getOrThrow())
        assertIs<WebAppException>(webApp.deviceStorage.clear().exceptionOrNull())
        assertEquals(SecureStorage.Item(value = null, canRestore = true), webApp.secureStorage.getItem("token").getOrThrow())
        assertEquals("restored", webApp.secureStorage.restoreItem("token").getOrThrow())
        assertTrue(webApp.cloudStorage.removeItems("a", "b").getOrThrow())
        assertTrue("CloudStorage.removeItems([[\"a\",\"b\"],null])" in mock.calls())
    }

    @Test
    fun biometricsSensorsAndLocation() = runTest {
        val mock = RecordingMock()
        val webApp = mock.webApp
        mock.respondWith("BiometricManager.init", """[]""")
        mock.respondWith("BiometricManager.requestAccess", """[true]""")
        mock.respondWith("BiometricManager.authenticate", """[true, "token"]""")
        mock.respondWith("BiometricManager.updateBiometricToken", """[true]""")
        mock.respondWith("Accelerometer.start", """[true]""")
        mock.respondWith("Gyroscope.stop", """[true]""")
        mock.respondWith("DeviceOrientation.start", """[false]""")
        mock.respondWith("LocationManager.init", """[]""")
        mock.respondWith("LocationManager.getLocation", """[{"latitude": 55.75, "longitude": 37.61, "speed": null}]""")

        webApp.biometricManager.init()
        assertTrue(webApp.biometricManager.requestAccess(reason = "Sign in"))
        assertEquals(BiometricAuthResult(true, "token"), webApp.biometricManager.authenticate())
        assertTrue(webApp.biometricManager.updateBiometricToken(""))
        assertTrue(webApp.accelerometer.start(refreshRate = 100))
        assertTrue(webApp.gyroscope.stop())
        assertFalse(webApp.deviceOrientation.start(needAbsolute = true))
        webApp.locationManager.init()
        assertEquals(
            LocationData(55.75, 37.61, null, null, null, null, null, null, null),
            webApp.locationManager.getLocation(),
        )

        val calls = mock.calls()
        assertTrue("BiometricManager.requestAccess([{\"reason\":\"Sign in\"},null])" in calls)
        assertTrue("BiometricManager.authenticate([{},null])" in calls)
        assertTrue("Accelerometer.start([{\"refresh_rate\":100},null])" in calls)
        assertTrue("DeviceOrientation.start([{\"need_absolute\":true},null])" in calls)
    }

    @Test
    fun enumsFallBackForUnknownValues() {
        assertEquals(BiometricType.UNKNOWN, BiometricType.entries.first { it.value == "unknown" })
        assertEquals(InvoiceStatus.UNKNOWN, InvoiceStatus.entries.last())
    }
}
