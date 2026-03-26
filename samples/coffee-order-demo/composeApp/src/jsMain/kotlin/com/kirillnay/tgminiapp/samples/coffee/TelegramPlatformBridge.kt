package com.kirillnay.tgminiapp.samples.coffee

import androidx.compose.ui.graphics.Color
import com.kirillNay.telegram.miniapp.compose.TelegramStyle
import com.kirillNay.telegram.miniapp.webApp.webApp
import kotlinx.browser.window
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val NoteStorageKey = "coffee_order_demo_note"

class TelegramPlatformBridge(
    private val style: TelegramStyle,
) : PlatformAppBridge {

    private var currentBackAction: (() -> Unit)? = null
    private var currentMainAction: (() -> Unit)? = null

    override val environment: AppEnvironment = AppEnvironment(
        palette = AppPalette(
            isDark = webApp.colorScheme.value == "dark",
            background = style.colors.backgroundColor,
            surface = style.colors.secondaryBackgroundColor,
            surfaceAccent = blend(style.colors.secondaryBackgroundColor, style.colors.buttonColor, 0.1f),
            primary = style.colors.buttonColor,
            onPrimary = style.colors.buttonTextColor,
            text = style.colors.textColor,
            mutedText = style.colors.hintColor,
            border = blend(style.colors.hintColor, style.colors.backgroundColor, 0.5f),
        ),
        platformLabel = "Web",
        runtimeLabel = "Telegram ${webApp.platform} / Bot API ${webApp.version}",
        userLabel = webApp.initDataUnsafe.user?.firstName ?: "Telegram guest",
        usernameLabel = webApp.initDataUnsafe.user?.username?.let { "@$it" },
        storageLabel = if (webApp.isVersionAtLeast("6.9")) {
            "Telegram CloudStorage with localStorage fallback"
        } else {
            "Browser localStorage fallback"
        },
        viewportLabel = "${style.viewPort.viewPortHeight.value.toInt()}dp visible / ${style.viewPort.viewportStableHeight.value.toInt()}dp stable",
        themeLabel = "Telegram ${webApp.colorScheme.value} theme",
        isTelegramRuntime = true,
    )

    init {
        webApp.ready()
        webApp.expand()
        webApp.enableClosingConfirmation(true)
        webApp.setBackgroundColor("bg_color")
        webApp.setHeaderColor("secondary_bg_color")
    }

    override suspend fun loadNote(): Result<String> {
        val localValue = runCatching { window.localStorage.getItem(NoteStorageKey).orEmpty() }.getOrDefault("")
        if (!webApp.isVersionAtLeast("6.9")) {
            return Result.success(localValue)
        }

        return webApp.cloudStorage.getItem(NoteStorageKey)
            .map { cloudValue -> if (cloudValue.isBlank()) localValue else cloudValue }
            .recover { localValue }
    }

    override suspend fun saveNote(note: String): Result<Unit> {
        val localResult = runCatching {
            window.localStorage.setItem(NoteStorageKey, note)
        }

        if (localResult.isFailure) {
            return Result.failure(localResult.exceptionOrNull() ?: IllegalStateException("Unable to write localStorage."))
        }

        if (!webApp.isVersionAtLeast("6.9")) {
            return Result.success(Unit)
        }

        return webApp.cloudStorage.setItem(NoteStorageKey, note)
            .fold(
                onSuccess = { Result.success(Unit) },
                onFailure = { Result.success(Unit) },
            )
    }

    override suspend fun confirmOrder(summary: String): Boolean = suspendCoroutine { continuation ->
        webApp.showConfirm("Confirm coffee order?\n$summary") { isConfirmed ->
            continuation.resume(isConfirmed)
        }
    }

    override fun updateChrome(backAction: (() -> Unit)?, mainAction: BridgeAction?) {
        currentBackAction?.let { webApp.backButton.offClick(it) }
        currentBackAction = backAction
        if (backAction == null) {
            webApp.backButton.hide()
        } else {
            webApp.backButton.onClick(backAction)
            webApp.backButton.show()
        }

        currentMainAction?.let { webApp.mainButton.offClick(it) }
        currentMainAction = mainAction?.onClick
        if (mainAction == null) {
            webApp.mainButton.hideProgress()
            webApp.mainButton.hide()
        } else {
            webApp.mainButton.setText(mainAction.label)
            webApp.mainButton.enable()
            webApp.mainButton.onClick(mainAction.onClick)
            webApp.mainButton.show()
        }
    }

    override fun clearChrome() {
        currentBackAction?.let { webApp.backButton.offClick(it) }
        currentBackAction = null
        webApp.backButton.hide()

        currentMainAction?.let { webApp.mainButton.offClick(it) }
        currentMainAction = null
        webApp.mainButton.hideProgress()
        webApp.mainButton.hide()
    }

    override fun onItemAdded() {
        webApp.hapticFeedback.impactOccurred("light")
    }

    override fun onOrderCompleted() {
        webApp.hapticFeedback.notificationOccurred("success")
    }
}

private fun blend(first: Color, second: Color, amount: Float): Color {
    val inverse = 1f - amount
    return Color(
        red = first.red * inverse + second.red * amount,
        green = first.green * inverse + second.green * amount,
        blue = first.blue * inverse + second.blue * amount,
        alpha = 1f,
    )
}
