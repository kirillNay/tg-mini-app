package com.kirillNay.telegram.miniapp

import com.kirillNay.telegram.miniapp.compose.TelegramColors
import com.kirillNay.telegram.miniapp.compose.TelegramColors.Companion.toColor
import com.kirillNay.telegram.miniapp.webApp.ColorScheme
import com.kirillNay.telegram.miniapp.webApp.InvoiceStatus
import com.kirillNay.telegram.miniapp.webApp.SafeAreaInset
import com.kirillNay.telegram.miniapp.webApp.WebAppEvent
import com.kirillNay.telegram.miniapp.webApp.WebAppException
import com.kirillNay.telegram.miniapp.webApp.internal.jsGet
import com.kirillNay.telegram.miniapp.webApp.internal.isNullOrUndefined
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull
import com.kirillNay.telegram.miniapp.webApp.popup.PopupButton
import com.kirillNay.telegram.miniapp.webApp.popup.PopupParams
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WebAppTest {

    @Test
    fun parsesInitDataDeliveredAsStrings() {
        val initData = createTelegramMock().toWebApp().initDataUnsafe

        assertEquals("AAA", initData.queryId)
        assertEquals(1_700_000_000L, initData.authDate)
        assertEquals(5, initData.canSendAfter)
        val user = initData.user!!
        assertEquals(123_456_789_012L, user.id)
        assertEquals("Ann", user.firstName)
        assertTrue(user.isPremium)
        assertFalse(user.isBot)
        assertNull(initData.chat)
        assertNull(initData.hash)
        assertNull(user.lastName)
    }

    @Test
    fun readsStateAndFallsBackForUnknownValues() {
        val webApp = createTelegramMock().toWebApp()

        assertEquals(ColorScheme.DARK, webApp.colorScheme)
        assertEquals(600.0, webApp.viewportHeight)
        assertEquals(SafeAreaInset(10, 20, 0, 0), webApp.safeAreaInset)
        assertEquals(SafeAreaInset.Zero, webApp.contentSafeAreaInset)
        assertFalse(webApp.isFullscreen)
        assertTrue(webApp.isActive)
        assertTrue(webApp.isVerticalSwipesEnabled)
        assertNull(webApp.bottomBarColor)
    }

    @Test
    fun eventHandlerReceivesPayloadAndUnsubscribes() {
        val mock = createTelegramMock()
        val webApp = mock.toWebApp()
        val received = mutableListOf<Boolean>()

        val subscription = webApp.onEvent(WebAppEvent.ViewportChanged) { received += it }
        emit(mock, "viewportChanged", eventPayload("""{"isStateStable": true}"""))
        subscription.unsubscribe()
        emit(mock, "viewportChanged", eventPayload("""{"isStateStable": false}"""))

        assertEquals(listOf(true), received)
        assertEquals(0, handlerCount(mock, "viewportChanged"))
    }

    @Test
    fun parsesTypedEventPayloads() {
        val mock = createTelegramMock()
        val webApp = mock.toWebApp()
        var invoice: Any? = null
        var buttonId: String? = "unset"

        webApp.onEvent(WebAppEvent.InvoiceClosed) { invoice = it }
        webApp.onEvent(WebAppEvent.PopupClosed) { buttonId = it }
        emit(mock, "invoiceClosed", eventPayload("""{"url": "https://t.me/$1", "status": "refunded"}"""))
        emit(mock, "popupClosed", eventPayload("""{"button_id": null}"""))

        val data = assertIs<com.kirillNay.telegram.miniapp.webApp.InvoiceClosedData>(invoice)
        assertEquals(InvoiceStatus.UNKNOWN, data.status)
        assertNull(buttonId)
    }

    @Test
    fun offClickRemovesTheHandlerRegisteredByOnClick() {
        val mock = createTelegramMock()
        val backButton = mock.toWebApp().backButton
        val callback = {}

        backButton.onClick(callback).show()
        assertEquals(1, handlerCount(mock, "backButtonClicked"))
        assertTrue(backButton.isVisible)

        backButton.offClick(callback)
        assertEquals(0, handlerCount(mock, "backButtonClicked"))
    }

    @Test
    fun popupParamsSkipNullFields() {
        val mock = createTelegramMock()
        var pressed = ""

        mock.toWebApp().showPopup(PopupParams(message = "Hi", buttons = listOf(PopupButton(type = PopupButton.Type.OK)))) { pressed = it }

        val params = lastArgument(mock, "showPopup")!!
        assertTrue(jsGet(params, "title").isNullOrUndefined())
        val button = jsGet(jsGet(params, "buttons")!!, "0")!!
        assertTrue(jsGet(button, "id").isNullOrUndefined())
        assertEquals("ok", jsGet(button, "type").asStringOrNull())
        assertEquals("ok_id", pressed)
    }

    @Test
    fun openLinkPassesOptionsObject() {
        val mock = createTelegramMock()

        mock.toWebApp().openLink("https://telegram.org", tryInstantView = true)

        assertTrue(jsGet(lastArgument(mock, "openLink")!!, "try_instant_view").isTrue())
    }

    @Test
    fun cloudStorageReturnsValuesAndErrors() = runTest {
        val cloudStorage = createTelegramMock().toWebApp().cloudStorage

        assertEquals("stored", cloudStorage.getItem("v").getOrThrow())
        assertEquals(mapOf("v" to "stored", "x" to ""), cloudStorage.getItems("v", "x").getOrThrow())
        assertEquals(listOf("v"), cloudStorage.getKeys().getOrThrow())
        val error = cloudStorage.getItem("bad").exceptionOrNull()
        assertIs<WebAppException>(error)
        assertEquals("STORAGE_KEY_INVALID", error.message)
    }

    @Test
    fun awaitConfirmResumesWithCallbackValue() = runTest {
        assertTrue(createTelegramMock().toWebApp().awaitConfirm("Sure?"))
    }

    @Test
    fun mapsThemeColorsWithFallbacks() {
        val colors = TelegramColors.from(createTelegramMock().toWebApp().themeParams)

        assertEquals(Color(0xFF112233), colors.backgroundColor)
        assertEquals(Color(0xFF445566), colors.buttonColor)
        assertEquals(colors.backgroundColor, colors.headerBackgroundColor)
        assertEquals(Color(0x80112233), "#11223380".toColor())
        assertNull("red".toColor())
    }
}
