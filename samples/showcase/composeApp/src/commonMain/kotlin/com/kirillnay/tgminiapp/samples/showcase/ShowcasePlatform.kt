package com.kirillnay.tgminiapp.samples.showcase

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Everything the shared showcase UI needs from a host. Only the web host talks to Telegram;
 * Android and iOS hosts show the same catalog with every feature unavailable.
 */
interface ShowcasePlatform {

    /** Name of the host, e.g. "Telegram (android)" or "Android". */
    val hostName: String

    val palette: ShowcasePalette

    /** Insets to keep content out of the device and Telegram UI. */
    val contentPadding: PaddingValues

    /** Grouped key/value facts about the launch, theme, viewport and sensors. Updated live. */
    val launchData: StateFlow<List<InfoGroup>>

    /** Telegram events received by the Mini App, newest last. */
    val eventLog: StateFlow<List<LogEntry>>

    /** True if the host shows its own back button (Telegram's header button), so the UI hides the in-app one. */
    val hasNativeBackButton: Boolean

    fun availability(feature: Feature): Availability

    suspend fun run(feature: Feature, inputs: Map<String, String>): FeatureResult

    /** Called when the navigation stack changes; `null` means there is nothing to go back to. */
    fun onBackAvailable(onBack: (() -> Unit)?)

    fun clearEventLog()
}

data class ShowcasePalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val primary: Color,
    val onPrimary: Color,
    val text: Color,
    val hint: Color,
    val accent: Color,
    val destructive: Color,
    val separator: Color,
)

data class InfoGroup(
    val title: String,
    val entries: List<Pair<String, String>>,
)

data class LogEntry(
    val index: Int,
    val name: String,
    val details: String,
)

sealed interface Availability {
    data object Available : Availability

    data class RequiresBotApi(val version: String, val current: String) : Availability

    data class Unavailable(val reason: String) : Availability
}

sealed interface FeatureResult {
    val message: String

    data class Success(override val message: String) : FeatureResult

    data class Failure(override val message: String) : FeatureResult
}

/** Host without Telegram (Android and iOS): the catalog is browsable, nothing can run. */
class OutsideTelegramPlatform(
    override val hostName: String,
    isDark: Boolean,
) : ShowcasePlatform {

    override val palette = if (isDark) {
        ShowcasePalette(
            isDark = true,
            background = Color(0xFF17212B),
            surface = Color(0xFF232E3C),
            primary = Color(0xFF5288C1),
            onPrimary = Color.White,
            text = Color(0xFFF5F5F5),
            hint = Color(0xFF708499),
            accent = Color(0xFF6AB3F3),
            destructive = Color(0xFFEC3942),
            separator = Color(0xFF111921),
        )
    } else {
        ShowcasePalette(
            isDark = false,
            background = Color(0xFFEFEFF4),
            surface = Color.White,
            primary = Color(0xFF2481CC),
            onPrimary = Color.White,
            text = Color.Black,
            hint = Color(0xFF999999),
            accent = Color(0xFF2481CC),
            destructive = Color(0xFFE53935),
            separator = Color(0xFFD9D9DE),
        )
    }

    override val contentPadding = PaddingValues(0.dp)

    override val launchData: StateFlow<List<InfoGroup>> = MutableStateFlow(
        listOf(
            InfoGroup(
                "Host",
                listOf(
                    "Platform" to hostName,
                    "Telegram" to "not available",
                ),
            ),
        ),
    )

    override val eventLog: StateFlow<List<LogEntry>> = MutableStateFlow(emptyList())

    override val hasNativeBackButton = false

    override fun availability(feature: Feature) =
        Availability.Unavailable("Available only inside Telegram: open the web showcase from the bot.")

    override suspend fun run(feature: Feature, inputs: Map<String, String>) =
        FeatureResult.Failure("Not available on $hostName")

    override fun onBackAvailable(onBack: (() -> Unit)?) = Unit

    override fun clearEventLog() = Unit
}
