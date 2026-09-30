package com.kirillNay.telegram.miniapp

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import kotlinx.coroutines.test.TestResult

/** Runs a Compose UI test once the Skia runtime is loaded (the js target loads it asynchronously). */
@OptIn(ExperimentalTestApi::class)
internal expect fun composeUiTest(block: suspend ComposeUiTest.() -> Unit): TestResult
