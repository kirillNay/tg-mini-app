package com.kirillNay.telegram.miniapp.compose

import androidx.compose.ui.unit.Dp

/**
 * Size of the visible area of the Mini App.
 *
 * @param height the current height of the visible area. Changes in real time during gestures and animations.
 * @param stableHeight the height in the last stable state. Use it to pin interface elements to the bottom.
 */
data class ViewPort(
    val height: Dp,
    val stableHeight: Dp,
)
