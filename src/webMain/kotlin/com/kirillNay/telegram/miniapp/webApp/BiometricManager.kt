package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.BiometricManagerJs
import com.kirillNay.telegram.miniapp.webApp.internal.awaitValue
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.jsObject
import com.kirillNay.telegram.miniapp.webApp.internal.asStringOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Bot API 7.2+
 *
 * Controls biometrics on the device. Call [init] before the first use.
 */
class BiometricManager internal constructor(
    private val js: BiometricManagerJs,
) {

    /** Shows whether the biometrics object is initialized. */
    val isInited: Boolean get() = js.isInited

    /** Shows whether biometrics is available on the current device. */
    val isBiometricAvailable: Boolean get() = js.isBiometricAvailable

    /** The type of biometrics currently available on the device. */
    val biometricType: BiometricType get() = BiometricType.from(js.biometricType)

    /** Shows whether permission to use biometrics has been requested. */
    val isAccessRequested: Boolean get() = js.isAccessRequested

    /** Shows whether permission to use biometrics has been granted. */
    val isAccessGranted: Boolean get() = js.isAccessGranted

    /** Shows whether the token is saved in secure storage on the device. */
    val isBiometricTokenSaved: Boolean get() = js.isBiometricTokenSaved

    /** A unique device identifier that can be used to match the token to the device. */
    val deviceId: String get() = js.deviceId.orEmpty()

    /** Bot API 7.2+ Initializes the object. Resumes when initialization is finished. */
    suspend fun init(): Unit = suspendCoroutine { continuation ->
        js.init { continuation.resume(Unit) }
    }

    /**
     * Bot API 7.2+ Requests permission to use biometrics.
     *
     * @param reason the text shown to the user explaining why the bot needs access to biometrics, 0-128 characters.
     * @return whether the user granted access.
     */
    suspend fun requestAccess(reason: String? = null): Boolean =
        awaitValue({ js.requestAccess(jsObject { put("reason", reason) }, it) }) { it.isTrue() }

    /**
     * Bot API 7.2+ Authenticates the user using biometrics.
     *
     * @param reason the text shown to the user explaining why you ask them to authenticate, 0-128 characters.
     */
    suspend fun authenticate(reason: String? = null): BiometricAuthResult = suspendCoroutine { continuation ->
        js.authenticate(jsObject { put("reason", reason) }) { isAuthenticated, token ->
            continuation.resume(BiometricAuthResult(isAuthenticated.isTrue(), token.asStringOrNull()))
        }
    }

    /**
     * Bot API 7.2+ Updates the biometric token in secure storage on the device. Pass an empty string to remove the token.
     *
     * @return whether the token was updated.
     */
    suspend fun updateBiometricToken(token: String): Boolean =
        awaitValue({ js.updateBiometricToken(token, it) }) { it.isTrue() }

    /**
     * Bot API 7.2+ Opens the biometric access settings for bots.
     *
     * Can be called only in response to user interaction with the Mini App interface.
     */
    fun openSettings(): BiometricManager = apply { js.openSettings() }
}

/** The type of biometrics available on the device. */
enum class BiometricType(val value: String) {

    /** Fingerprint-based biometrics. */
    FINGER("finger"),

    /** Face-based biometrics. */
    FACE("face"),

    /** Biometrics of an unknown type. */
    UNKNOWN("unknown");

    internal companion object {

        fun from(value: String?): BiometricType = entries.find { it.value == value } ?: UNKNOWN
    }
}

/**
 * Result of [BiometricManager.authenticate] and the [WebAppEvent.BiometricAuthRequested] event.
 *
 * @param biometricToken the token stored in secure storage on the device, if the user was authenticated.
 */
data class BiometricAuthResult(
    val isAuthenticated: Boolean,
    val biometricToken: String?,
)
