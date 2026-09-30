package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.DeviceOrientationJs
import com.kirillNay.telegram.miniapp.webApp.internal.MotionSensorJs
import com.kirillNay.telegram.miniapp.webApp.internal.awaitValue
import com.kirillNay.telegram.miniapp.webApp.internal.isTrue
import com.kirillNay.telegram.miniapp.webApp.internal.jsObject
import com.kirillNay.telegram.miniapp.webApp.internal.asDoubleOrNull

/**
 * Bot API 8.0+
 *
 * Access to accelerometer data on the device. Values are in m/s² and are updated after [start];
 * subscribe to [WebAppEvent.AccelerometerChanged] to be notified.
 */
class Accelerometer internal constructor(
    private val js: MotionSensorJs,
) {

    /** Indicates whether accelerometer tracking is currently active. */
    val isStarted: Boolean get() = js.isStarted

    /** The current acceleration in the X-axis, in m/s². */
    val x: Double? get() = js.x.asDoubleOrNull()

    /** The current acceleration in the Y-axis, in m/s². */
    val y: Double? get() = js.y.asDoubleOrNull()

    /** The current acceleration in the Z-axis, in m/s². */
    val z: Double? get() = js.z.asDoubleOrNull()

    /**
     * Bot API 8.0+ Starts tracking accelerometer data.
     *
     * @param refreshRate the refresh rate in milliseconds, 20-1000. 1000 by default. May not be supported on all platforms.
     * @return whether tracking was started.
     */
    suspend fun start(refreshRate: Int? = null): Boolean =
        awaitValue({ js.start(jsObject { put("refresh_rate", refreshRate) }, it) }) { it.isTrue() }

    /** Bot API 8.0+ Stops tracking accelerometer data. Returns whether tracking was stopped. */
    suspend fun stop(): Boolean = awaitValue({ js.stop(it) }) { it.isTrue() }
}

/**
 * Bot API 8.0+
 *
 * Access to gyroscope data on the device. Values are in rad/s and are updated after [start];
 * subscribe to [WebAppEvent.GyroscopeChanged] to be notified.
 */
class Gyroscope internal constructor(
    private val js: MotionSensorJs,
) {

    /** Indicates whether gyroscope tracking is currently active. */
    val isStarted: Boolean get() = js.isStarted

    /** The current rotation rate around the X-axis, in rad/s. */
    val x: Double? get() = js.x.asDoubleOrNull()

    /** The current rotation rate around the Y-axis, in rad/s. */
    val y: Double? get() = js.y.asDoubleOrNull()

    /** The current rotation rate around the Z-axis, in rad/s. */
    val z: Double? get() = js.z.asDoubleOrNull()

    /**
     * Bot API 8.0+ Starts tracking gyroscope data.
     *
     * @param refreshRate the refresh rate in milliseconds, 20-1000. 1000 by default. May not be supported on all platforms.
     * @return whether tracking was started.
     */
    suspend fun start(refreshRate: Int? = null): Boolean =
        awaitValue({ js.start(jsObject { put("refresh_rate", refreshRate) }, it) }) { it.isTrue() }

    /** Bot API 8.0+ Stops tracking gyroscope data. Returns whether tracking was stopped. */
    suspend fun stop(): Boolean = awaitValue({ js.stop(it) }) { it.isTrue() }
}

/**
 * Bot API 8.0+
 *
 * Access to orientation data on the device. Values are in radians and are updated after [start];
 * subscribe to [WebAppEvent.DeviceOrientationChanged] to be notified.
 */
class DeviceOrientation internal constructor(
    private val js: DeviceOrientationJs,
) {

    /** Indicates whether device orientation tracking is currently active. */
    val isStarted: Boolean get() = js.isStarted

    /** Whether the device provides orientation data in absolute values. */
    val absolute: Boolean get() = js.absolute

    /** The rotation around the Z-axis, in radians. */
    val alpha: Double? get() = js.alpha.asDoubleOrNull()

    /** The rotation around the X-axis, in radians. */
    val beta: Double? get() = js.beta.asDoubleOrNull()

    /** The rotation around the Y-axis, in radians. */
    val gamma: Double? get() = js.gamma.asDoubleOrNull()

    /**
     * Bot API 8.0+ Starts tracking device orientation data.
     *
     * @param refreshRate the refresh rate in milliseconds, 20-1000. 1000 by default. May not be supported on all platforms.
     * @param needAbsolute pass true to receive absolute orientation data relative to magnetic north. Some devices may not support it; check [absolute].
     * @return whether tracking was started.
     */
    suspend fun start(refreshRate: Int? = null, needAbsolute: Boolean? = null): Boolean =
        awaitValue({
            js.start(
                jsObject {
                    put("refresh_rate", refreshRate)
                    put("need_absolute", needAbsolute)
                },
                it,
            )
        }) { it.isTrue() }

    /** Bot API 8.0+ Stops tracking device orientation data. Returns whether tracking was stopped. */
    suspend fun stop(): Boolean = awaitValue({ js.stop(it) }) { it.isTrue() }
}
