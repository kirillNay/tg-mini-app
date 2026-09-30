package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * Bot API 7.8+
 *
 * Additional sharing settings for the native story editor, used by [WebApp.shareToStory].
 *
 * @param text the caption to be added to the media, 0-200 characters for regular users and 0-2048 characters for premium subscribers.
 * @param widgetLink a widget link to be included in the story. Only premium subscribers can post stories with links.
 */
data class StoryShareParams(
    val text: String? = null,
    val widgetLink: StoryWidgetLink? = null,
) {

    internal fun toJs(): JsAny = jsObject {
        put("text", text)
        put("widget_link", widgetLink?.toJs())
    }
}

/**
 * Bot API 7.8+
 *
 * A widget link to be included in a story.
 *
 * @param url the URL to be included in the story.
 * @param name the name to be displayed for the widget link, 0-48 characters.
 */
data class StoryWidgetLink(
    val url: String,
    val name: String? = null,
) {

    internal fun toJs(): JsAny = jsObject {
        put("url", url)
        put("name", name)
    }
}
