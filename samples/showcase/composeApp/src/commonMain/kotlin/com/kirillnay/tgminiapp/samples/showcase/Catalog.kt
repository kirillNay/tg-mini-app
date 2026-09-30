package com.kirillnay.tgminiapp.samples.showcase

/**
 * A text input of a [Feature]. With [options] it is rendered as a single-choice chip row.
 */
data class FeatureInput(
    val key: String,
    val label: String,
    val default: String = "",
    val options: List<String> = emptyList(),
    val hint: String? = null,
)

/**
 * One capability of the Telegram Mini Apps API. The catalog is shared by all platforms;
 * each [ShowcasePlatform] decides whether it can run it.
 *
 * @param actionLabel the text of the button that runs the feature. It is unique across the catalog.
 * @param minBotApi the Bot API version that introduced the feature, or `null` if it is available in every version.
 */
data class Feature(
    val id: String,
    val title: String,
    val description: String,
    val actionLabel: String,
    val minBotApi: String? = null,
    val inputs: List<FeatureInput> = emptyList(),
)

data class FeatureSection(
    val id: String,
    val title: String,
    val description: String,
    val features: List<Feature>,
)

private val onOff = listOf("on", "off")

/** Every Telegram Mini Apps capability wrapped by tg-mini-app, grouped for the showcase. */
object Catalog {

    val sections: List<FeatureSection> = listOf(
        FeatureSection(
            id = "app",
            title = "App behavior",
            description = "Lifecycle, closing confirmation, swipes and Bot API version checks.",
            features = listOf(
                Feature("expand", "Expand", "Expands the Mini App to the maximum available height.", "Expand app"),
                Feature(
                    "closing_confirmation", "Closing confirmation",
                    "Asks the user to confirm closing the Mini App.", "Apply closing confirmation", "6.2",
                    listOf(FeatureInput("state", "State", "on", onOff)),
                ),
                Feature(
                    "vertical_swipes", "Vertical swipes",
                    "Enables or disables swipes that close or minimize the Mini App.", "Apply vertical swipes", "7.7",
                    listOf(FeatureInput("state", "State", "off", onOff)),
                ),
                Feature(
                    "version_check", "Version check",
                    "Checks whether the Telegram app supports a Bot API version.", "Check version",
                    inputs = listOf(FeatureInput("version", "Bot API version", "8.0")),
                ),
                Feature(
                    "send_data", "Send data to the bot",
                    "Sends data to the bot and closes the Mini App. Works only when launched from a keyboard button.",
                    "Send data", inputs = listOf(FeatureInput("data", "Data", "showcase")),
                ),
                Feature("close", "Close", "Closes the Mini App.", "Close app"),
            ),
        ),
        FeatureSection(
            id = "appearance",
            title = "Appearance",
            description = "Header, background and bottom bar colors, fullscreen and orientation.",
            features = listOf(
                Feature(
                    "header_color", "Header color", "Sets the header color: #RRGGBB or a theme key.",
                    "Set header color", "6.1",
                    listOf(FeatureInput("color", "Color", "secondary_bg_color", listOf("bg_color", "secondary_bg_color", "#8e44ad"))),
                ),
                Feature(
                    "background_color", "Background color", "Sets the background color: #RRGGBB or a theme key.",
                    "Set background color", "6.1",
                    listOf(FeatureInput("color", "Color", "bg_color", listOf("bg_color", "secondary_bg_color", "#1e3a5f"))),
                ),
                Feature(
                    "bottom_bar_color", "Bottom bar color", "Sets the bottom bar color, also used for the Android navigation bar.",
                    "Set bottom bar color", "7.10",
                    listOf(FeatureInput("color", "Color", "bottom_bar_bg_color", listOf("bg_color", "secondary_bg_color", "bottom_bar_bg_color"))),
                ),
                Feature(
                    "fullscreen", "Fullscreen", "Enters or exits fullscreen mode.", "Apply fullscreen", "8.0",
                    listOf(FeatureInput("state", "State", "enter", listOf("enter", "exit"))),
                ),
                Feature(
                    "orientation_lock", "Orientation lock", "Locks the current orientation or lets it follow the device.",
                    "Apply orientation lock", "8.0",
                    listOf(FeatureInput("state", "State", "lock", listOf("lock", "unlock"))),
                ),
            ),
        ),
        FeatureSection(
            id = "buttons",
            title = "Buttons",
            description = "Main, secondary, back and settings buttons. Presses appear in the event log.",
            features = listOf(
                Feature(
                    "main_button", "Main button", "Controls the main button at the bottom of the Mini App.",
                    "Apply main button",
                    inputs = listOf(
                        FeatureInput("action", "Action", "show", listOf("show", "hide", "progress", "stop progress", "disable", "enable")),
                        FeatureInput("text", "Text", "Main action"),
                        FeatureInput("shine", "Shine effect (7.10+)", "off", onOff),
                    ),
                ),
                Feature(
                    "secondary_button", "Secondary button", "Controls the secondary button next to the main one.",
                    "Apply secondary button", "7.10",
                    listOf(
                        FeatureInput("action", "Action", "show", listOf("show", "hide")),
                        FeatureInput("text", "Text", "Secondary"),
                        FeatureInput("position", "Position", "left", listOf("left", "right", "top", "bottom")),
                    ),
                ),
                Feature(
                    "back_button", "Back button", "Shows or hides the back button in the header.", "Apply back button", "6.1",
                    listOf(FeatureInput("action", "Action", "show", listOf("show", "hide"))),
                ),
                Feature(
                    "settings_button", "Settings button", "Shows or hides the Settings item in the context menu.",
                    "Apply settings button", "7.0",
                    listOf(FeatureInput("action", "Action", "show", listOf("show", "hide"))),
                ),
            ),
        ),
        FeatureSection(
            id = "popups",
            title = "Popups and input",
            description = "Native alerts, confirmations, popups, the QR scanner, clipboard and keyboard.",
            features = listOf(
                Feature(
                    "alert", "Alert", "Shows a message with a Close button.", "Show alert", "6.2",
                    listOf(FeatureInput("message", "Message", "Hello from tg-mini-app")),
                ),
                Feature(
                    "confirm", "Confirm", "Shows a message with OK and Cancel buttons.", "Show confirm", "6.2",
                    listOf(FeatureInput("message", "Message", "Do you like this showcase?")),
                ),
                Feature(
                    "popup", "Popup", "Shows a native popup with a title and custom buttons.", "Show popup", "6.2",
                    listOf(
                        FeatureInput("title", "Title", "Popup"),
                        FeatureInput("message", "Message", "Pick a button"),
                        FeatureInput("buttons", "Buttons", "ok + destructive", listOf("close", "ok + cancel", "ok + destructive")),
                    ),
                ),
                Feature(
                    "scan_qr", "QR scanner", "Opens the native QR scanner and closes it after the first code.",
                    "Scan QR code", "6.4", listOf(FeatureInput("text", "Hint", "Scan any QR code")),
                ),
                Feature(
                    "clipboard", "Read clipboard", "Reads text from the clipboard. Only for attachment menu launches.",
                    "Read clipboard", "6.4",
                ),
                Feature(
                    "hide_keyboard", "Hide keyboard", "Focus the field to open the keyboard, then hide it.",
                    "Hide keyboard", "9.1", listOf(FeatureInput("text", "Tap here first", "")),
                ),
            ),
        ),
        FeatureSection(
            id = "haptics",
            title = "Haptic feedback",
            description = "Impact, notification and selection haptics.",
            features = listOf(
                Feature(
                    "haptic_impact", "Impact", "Plays an impact haptic.", "Play impact", "6.1",
                    listOf(FeatureInput("style", "Style", "medium", listOf("light", "medium", "heavy", "rigid", "soft"))),
                ),
                Feature(
                    "haptic_notification", "Notification", "Plays a notification haptic.", "Play notification", "6.1",
                    listOf(FeatureInput("type", "Type", "success", listOf("success", "warning", "error"))),
                ),
                Feature("haptic_selection", "Selection changed", "Plays a selection haptic.", "Play selection", "6.1"),
            ),
        ),
        FeatureSection(
            id = "links",
            title = "Links and payments",
            description = "External and Telegram links, invoices and inline queries.",
            features = listOf(
                Feature(
                    "open_link", "Open link", "Opens a link in the browser without closing the Mini App.", "Open link",
                    inputs = listOf(
                        FeatureInput("url", "URL", "https://core.telegram.org/bots/webapps"),
                        FeatureInput("instant_view", "Instant View (6.4+)", "off", onOff),
                    ),
                ),
                Feature(
                    "open_telegram_link", "Open Telegram link", "Opens a t.me link inside Telegram.", "Open Telegram link",
                    inputs = listOf(FeatureInput("url", "URL", "https://t.me/telegram")),
                ),
                Feature(
                    "open_invoice", "Open invoice", "Opens an invoice link.", "Open invoice", "6.1",
                    listOf(FeatureInput("url", "Invoice link", "", hint = "Create one with the Bot API method createInvoiceLink")),
                ),
                Feature(
                    "switch_inline_query", "Switch inline query", "Inserts the bot's username and a query into a chat.",
                    "Switch inline query", "6.7",
                    listOf(
                        FeatureInput("query", "Query", "showcase"),
                        FeatureInput("chat_types", "Chat types", "users", listOf("current chat", "users", "groups", "channels")),
                    ),
                ),
            ),
        ),
        FeatureSection(
            id = "sharing",
            title = "Sharing and home screen",
            description = "Stories, prepared messages, file downloads, emoji status and home screen shortcuts.",
            features = listOf(
                Feature(
                    "share_story", "Share to story", "Opens the story editor with media.", "Share to story", "7.8",
                    listOf(
                        FeatureInput("media_url", "Media URL", "https://telegram.org/img/t_logo.png"),
                        FeatureInput("text", "Caption", "Made with tg-mini-app"),
                        FeatureInput("widget_url", "Widget link (premium)", ""),
                    ),
                ),
                Feature(
                    "share_message", "Share message", "Shares a message prepared by the bot.", "Share message", "8.0",
                    listOf(FeatureInput("message_id", "Prepared message id", "", hint = "Get it from the Bot API method savePreparedInlineMessage")),
                ),
                Feature(
                    "download_file", "Download file", "Asks the user to download a file.", "Download file", "8.0",
                    listOf(
                        FeatureInput("url", "File URL", "https://telegram.org/img/t_logo.png"),
                        FeatureInput("file_name", "File name", "telegram-logo.png"),
                    ),
                ),
                Feature(
                    "emoji_status", "Set emoji status", "Asks the user to set a custom emoji as their status.",
                    "Set emoji status", "8.0",
                    listOf(
                        FeatureInput("custom_emoji_id", "Custom emoji id", "", hint = "Get it from a message entity or getCustomEmojiStickers"),
                        FeatureInput("duration", "Duration, seconds", "3600"),
                    ),
                ),
                Feature(
                    "emoji_status_access", "Emoji status access", "Asks for permission to manage the emoji status.",
                    "Request emoji status access", "8.0",
                ),
                Feature("home_screen_add", "Add to home screen", "Prompts the user to add a home screen shortcut.", "Add to home screen", "8.0"),
                Feature("home_screen_status", "Home screen status", "Checks whether the shortcut was added.", "Check home screen status", "8.0"),
            ),
        ),
        FeatureSection(
            id = "permissions",
            title = "Permissions and chats",
            description = "Write access, phone number and chat requests.",
            features = listOf(
                Feature("write_access", "Write access", "Asks for permission to send messages to the user.", "Request write access", "6.9"),
                Feature("contact", "Phone number", "Asks the user to share their phone number.", "Request contact", "6.9"),
                Feature(
                    "request_chat", "Request chat", "Lets the user pick or create a chat.", "Request chat", "9.6",
                    listOf(FeatureInput("request_id", "Request id", "", hint = "Get it from the Bot API method savePreparedKeyboardButton")),
                ),
            ),
        ),
        FeatureSection(
            id = "cloud_storage",
            title = "Cloud storage",
            description = "Up to 1024 items per user, synced across devices.",
            features = listOf(
                Feature(
                    "cloud_set", "Set item", "Stores a value.", "Cloud: set item", "6.9",
                    listOf(FeatureInput("key", "Key", "showcase_key"), FeatureInput("value", "Value", "Hello")),
                ),
                Feature("cloud_get", "Get item", "Reads a value.", "Cloud: get item", "6.9", listOf(FeatureInput("key", "Key", "showcase_key"))),
                Feature("cloud_remove", "Remove item", "Removes a value.", "Cloud: remove item", "6.9", listOf(FeatureInput("key", "Key", "showcase_key"))),
                Feature("cloud_keys", "List keys", "Lists all stored keys.", "Cloud: list keys", "6.9"),
            ),
        ),
        FeatureSection(
            id = "device_storage",
            title = "Device storage",
            description = "Persistent local storage on the device, up to 5 MB.",
            features = listOf(
                Feature(
                    "device_set", "Set item", "Stores a value.", "Device: set item", "9.0",
                    listOf(FeatureInput("key", "Key", "showcase_key"), FeatureInput("value", "Value", "Hello")),
                ),
                Feature("device_get", "Get item", "Reads a value.", "Device: get item", "9.0", listOf(FeatureInput("key", "Key", "showcase_key"))),
                Feature("device_remove", "Remove item", "Removes a value.", "Device: remove item", "9.0", listOf(FeatureInput("key", "Key", "showcase_key"))),
                Feature("device_clear", "Clear", "Removes all values.", "Device: clear", "9.0"),
            ),
        ),
        FeatureSection(
            id = "secure_storage",
            title = "Secure storage",
            description = "Keychain / Keystore storage for secrets, up to 10 items.",
            features = listOf(
                Feature(
                    "secure_set", "Set item", "Stores a secret.", "Secure: set item", "9.0",
                    listOf(FeatureInput("key", "Key", "showcase_token"), FeatureInput("value", "Value", "secret")),
                ),
                Feature("secure_get", "Get item", "Reads a secret.", "Secure: get item", "9.0", listOf(FeatureInput("key", "Key", "showcase_token"))),
                Feature(
                    "secure_restore", "Restore item", "Restores a secret that existed on this device.", "Secure: restore item", "9.0",
                    listOf(FeatureInput("key", "Key", "showcase_token")),
                ),
                Feature("secure_remove", "Remove item", "Removes a secret.", "Secure: remove item", "9.0", listOf(FeatureInput("key", "Key", "showcase_token"))),
                Feature("secure_clear", "Clear", "Removes all secrets.", "Secure: clear", "9.0"),
            ),
        ),
        FeatureSection(
            id = "biometrics",
            title = "Biometrics",
            description = "Fingerprint or face authentication with a token in secure storage.",
            features = listOf(
                Feature("bio_init", "Initialize", "Initializes the biometric manager and shows its state.", "Biometrics: init", "7.2"),
                Feature(
                    "bio_access", "Request access", "Asks for permission to use biometrics.", "Biometrics: request access", "7.2",
                    listOf(FeatureInput("reason", "Reason", "Sign in to the showcase")),
                ),
                Feature(
                    "bio_authenticate", "Authenticate", "Authenticates the user and returns the stored token.", "Biometrics: authenticate", "7.2",
                    listOf(FeatureInput("reason", "Reason", "Confirm it's you")),
                ),
                Feature(
                    "bio_token", "Update token", "Stores a token; an empty value removes it.", "Biometrics: update token", "7.2",
                    listOf(FeatureInput("token", "Token", "showcase-token")),
                ),
                Feature("bio_settings", "Open settings", "Opens the biometric settings for bots.", "Biometrics: open settings", "7.2"),
            ),
        ),
        FeatureSection(
            id = "location",
            title = "Location",
            description = "Device location with the user's permission.",
            features = listOf(
                Feature("location_init", "Initialize", "Initializes the location manager and shows its state.", "Location: init", "8.0"),
                Feature("location_get", "Get location", "Requests the current location.", "Location: get", "8.0"),
                Feature("location_settings", "Open settings", "Opens the location settings for bots.", "Location: open settings", "8.0"),
            ),
        ),
        FeatureSection(
            id = "sensors",
            title = "Motion sensors",
            description = "Accelerometer, gyroscope and device orientation. Live values are on the Launch data screen.",
            features = listOf(
                Feature(
                    "accelerometer", "Accelerometer", "Tracks acceleration in m/s².", "Apply accelerometer", "8.0",
                    listOf(FeatureInput("action", "Action", "start", listOf("start", "stop")), FeatureInput("refresh_rate", "Refresh rate, ms", "200")),
                ),
                Feature(
                    "gyroscope", "Gyroscope", "Tracks rotation rate in rad/s.", "Apply gyroscope", "8.0",
                    listOf(FeatureInput("action", "Action", "start", listOf("start", "stop")), FeatureInput("refresh_rate", "Refresh rate, ms", "200")),
                ),
                Feature(
                    "device_orientation", "Device orientation", "Tracks orientation in radians.", "Apply device orientation", "8.0",
                    listOf(
                        FeatureInput("action", "Action", "start", listOf("start", "stop")),
                        FeatureInput("refresh_rate", "Refresh rate, ms", "200"),
                        FeatureInput("absolute", "Absolute", "off", onOff),
                    ),
                ),
            ),
        ),
    )

    val features: List<Feature> = sections.flatMap { it.features }

    init {
        check(features.map { it.id }.toSet().size == features.size) { "Feature ids must be unique" }
        check(features.map { it.actionLabel }.toSet().size == features.size) { "Action labels must be unique" }
    }
}
