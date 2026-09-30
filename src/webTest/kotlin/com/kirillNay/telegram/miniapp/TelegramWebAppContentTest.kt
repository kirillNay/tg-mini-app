package com.kirillNay.telegram.miniapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import com.kirillNay.telegram.miniapp.compose.LocalTelegramStyle
import com.kirillNay.telegram.miniapp.compose.TelegramStyle
import com.kirillNay.telegram.miniapp.compose.TelegramWebAppContent
import com.kirillNay.telegram.miniapp.webApp.ColorScheme
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class TelegramWebAppContentTest {

    @Test
    fun providesStyleFromWebApp() = composeUiTest {
        val mock = createTelegramMock()
        var received: TelegramStyle? = null
        var local: TelegramStyle? = null

        setContent {
            TelegramWebAppContent(mock.toWebApp()) { style ->
                received = style
                local = LocalTelegramStyle.current
                BasicText("height=${style.viewPort.height.value.toInt()}")
            }
        }

        onNodeWithText("height=600").assertExists()
        val style = received!!
        assertEquals(style, local)
        assertEquals(ColorScheme.DARK, style.colorScheme)
        assertEquals(Color(0xFF112233), style.colors.backgroundColor)
        assertEquals(580, style.viewPort.stableHeight.value.toInt())
    }

    @Test
    fun recomposesOnThemeAndViewportEvents() = composeUiTest {
        val mock = createTelegramMock()

        setContent {
            TelegramWebAppContent(mock.toWebApp()) { style ->
                Column {
                    BasicText("bg=${style.colors.backgroundColor}")
                    BasicText("height=${style.viewPort.height.value.toInt()}")
                }
            }
        }
        onNodeWithText("height=600").assertExists()

        setThemeParam(mock, "bg_color", "#ffffff")
        emit(mock, "themeChanged", null)
        setMockNumber(mock, "viewportHeight", 420.0)
        emit(mock, "viewportChanged", eventPayload("""{"isStateStable": true}"""))
        waitForIdle()

        onNodeWithText("bg=${Color.White}").assertExists()
        onNodeWithText("height=420").assertExists()
    }

    @Test
    fun unsubscribesWhenLeavingComposition() = composeUiTest {
        val mock = createTelegramMock()
        var show by mutableStateOf(true)

        setContent {
            if (show) {
                TelegramWebAppContent(mock.toWebApp()) { BasicText("content") }
            }
        }
        waitForIdle()
        assertEquals(1, handlerCount(mock, "themeChanged"))
        assertEquals(1, handlerCount(mock, "safeAreaChanged"))

        show = false
        waitForIdle()

        assertEquals(0, handlerCount(mock, "themeChanged"))
        assertEquals(0, handlerCount(mock, "viewportChanged"))
        assertEquals(0, handlerCount(mock, "safeAreaChanged"))
        assertEquals(0, handlerCount(mock, "contentSafeAreaChanged"))
    }
}
