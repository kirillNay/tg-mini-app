package com.kirillnay.tgminiapp.samples.coffee

import androidx.compose.ui.graphics.Color
import com.kirillNay.telegram.miniapp.compose.TelegramStyle
import com.kirillNay.telegram.miniapp.webApp.HapticFeedback
import com.kirillNay.telegram.miniapp.webApp.webApp

private const val NoteStorageKey = "coffee_order_demo_note"

class TelegramPlatformBridge(
    private val style: TelegramStyle,
) : PlatformAppBridge {

    private var currentBackAction: (() -> Unit)? = null
    private var currentMainAction: (() -> Unit)? = null

    private val hasCloudStorage = webApp.isVersionAtLeast("6.9")

    override val environment: AppEnvironment = AppEnvironment(
        palette = AppPalette(
            isDark = style.isDark,
            background = style.colors.backgroundColor,
            surface = style.colors.secondaryBackgroundColor,
            surfaceAccent = blend(style.colors.secondaryBackgroundColor, style.colors.buttonColor, 0.1f),
            primary = style.colors.buttonColor,
            onPrimary = style.colors.buttonTextColor,
            text = style.colors.textColor,
            mutedText = style.colors.hintColor,
            border = style.colors.sectionSeparatorColor,
        ),
        platformLabel = "Web",
        runtimeLabel = "Telegram ${webApp.platform} / Bot API ${webApp.version}",
        userLabel = webApp.initDataUnsafe.user?.firstName ?: "Telegram guest",
        usernameLabel = webApp.initDataUnsafe.user?.username?.let { "@$it" },
        storageLabel = if (hasCloudStorage) {
            "Telegram CloudStorage with localStorage fallback"
        } else {
            "Browser localStorage fallback"
        },
        viewportLabel = "${style.viewPort.height.value.toInt()}dp visible / ${style.viewPort.stableHeight.value.toInt()}dp stable",
        themeLabel = "Telegram ${style.colorScheme.value} theme",
        isTelegramRuntime = true,
    )

    override suspend fun loadNote(): Result<String> {
        val localValue = runCatching { localStorageGet(NoteStorageKey).orEmpty() }.getOrDefault("")
        if (!hasCloudStorage) {
            return Result.success(localValue)
        }

        return webApp.cloudStorage.getItem(NoteStorageKey)
            .map { cloudValue -> cloudValue.ifBlank { localValue } }
            .recover { localValue }
    }

    override suspend fun saveNote(note: String): Result<Unit> {
        val localResult = runCatching { localStorageSet(NoteStorageKey, note) }
        if (localResult.isFailure) {
            return Result.failure(localResult.exceptionOrNull() ?: IllegalStateException("Unable to write localStorage."))
        }

        if (hasCloudStorage) {
            // The local copy is enough to keep the note if CloudStorage fails.
            webApp.cloudStorage.setItem(NoteStorageKey, note)
        }
        return Result.success(Unit)
    }

    override suspend fun confirmOrder(summary: String): Boolean =
        webApp.awaitConfirm("Confirm coffee order?\n$summary")

    override fun updateChrome(backAction: (() -> Unit)?, mainAction: BridgeAction?) {
        currentBackAction?.let { webApp.backButton.offClick(it) }
        currentBackAction = backAction
        if (backAction == null) {
            webApp.backButton.hide()
        } else {
            webApp.backButton.onClick(backAction).show()
        }

        currentMainAction?.let { webApp.mainButton.offClick(it) }
        currentMainAction = mainAction?.onClick
        if (mainAction == null) {
            webApp.mainButton.hideProgress().hide()
        } else {
            webApp.mainButton
                .setText(mainAction.label)
                .enable()
                .onClick(mainAction.onClick)
                .show()
        }
    }

    override fun clearChrome() = updateChrome(backAction = null, mainAction = null)

    override fun onItemAdded() {
        webApp.hapticFeedback.impactOccurred(HapticFeedback.ImpactStyle.LIGHT)
    }

    override fun onOrderCompleted() {
        webApp.hapticFeedback.notificationOccurred(HapticFeedback.NotificationType.SUCCESS)
    }
}

private fun localStorageGet(key: String): String? = js("window.localStorage.getItem(key)")

private fun localStorageSet(key: String, value: String) {
    js("window.localStorage.setItem(key, value);")
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
