package com.kirillNay.telegram.miniapp

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlinx.coroutines.test.TestResult
import org.jetbrains.skiko.wasm.onWasmReady
import kotlin.js.Promise

@OptIn(ExperimentalTestApi::class)
internal actual fun composeUiTest(block: suspend ComposeUiTest.() -> Unit): TestResult =
    Promise<Unit> { resolve, reject ->
        onWasmReady {
            runComposeUiTest(block = block).unsafeCast<Promise<Unit>>().then(resolve, reject)
        }
    }.unsafeCast<TestResult>()
