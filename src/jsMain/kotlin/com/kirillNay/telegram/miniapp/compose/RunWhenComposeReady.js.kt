package com.kirillNay.telegram.miniapp.compose

import org.jetbrains.skiko.wasm.onWasmReady

internal actual fun runWhenComposeReady(block: () -> Unit) = onWasmReady(block)
