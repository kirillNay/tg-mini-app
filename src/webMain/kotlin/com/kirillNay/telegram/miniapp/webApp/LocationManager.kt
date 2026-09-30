package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.LocationDataJs
import com.kirillNay.telegram.miniapp.webApp.internal.LocationManagerJs
import com.kirillNay.telegram.miniapp.webApp.internal.awaitValue
import com.kirillNay.telegram.miniapp.webApp.internal.isNullOrUndefined
import com.kirillNay.telegram.miniapp.webApp.internal.asDoubleOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Bot API 8.0+
 *
 * Controls location access on the device. Call [init] before the first use.
 */
class LocationManager internal constructor(
    private val js: LocationManagerJs,
) {

    /** Shows whether the object has been initialized. */
    val isInited: Boolean get() = js.isInited

    /** Shows whether location services are available on the current device. */
    val isLocationAvailable: Boolean get() = js.isLocationAvailable

    /** Shows whether permission to use location has been requested. */
    val isAccessRequested: Boolean get() = js.isAccessRequested

    /** Shows whether permission to use location has been granted. */
    val isAccessGranted: Boolean get() = js.isAccessGranted

    /** Bot API 8.0+ Initializes the object. Resumes when initialization is finished. */
    suspend fun init(): Unit = suspendCoroutine { continuation ->
        js.init { continuation.resume(Unit) }
    }

    /** Bot API 8.0+ Requests location data. Returns `null` if access to location was not granted. */
    suspend fun getLocation(): LocationData? = awaitValue({ js.getLocation(it) }) { LocationData.fromOrNull(it) }

    /**
     * Bot API 8.0+ Opens the location access settings for bots.
     *
     * Can be called only in response to user interaction with the Mini App interface.
     */
    fun openSettings(): LocationManager = apply { js.openSettings() }
}

/**
 * Bot API 8.0+
 *
 * Data about the current location. Optional values are `null` if the device doesn't provide them.
 */
data class LocationData(
    /** Latitude in degrees. */
    val latitude: Double,
    /** Longitude in degrees. */
    val longitude: Double,
    /** Altitude above sea level in meters. */
    val altitude: Double?,
    /** The direction the device is moving in degrees (0 = North, 90 = East, 180 = South, 270 = West). */
    val course: Double?,
    /** The speed of the device in m/s. */
    val speed: Double?,
    /** Accuracy of the latitude and longitude values in meters. */
    val horizontalAccuracy: Double?,
    /** Accuracy of the altitude value in meters. */
    val verticalAccuracy: Double?,
    /** Accuracy of the course value in degrees. */
    val courseAccuracy: Double?,
    /** Accuracy of the speed value in m/s. */
    val speedAccuracy: Double?,
) {

    internal companion object {

        fun fromOrNull(raw: JsAny?): LocationData? {
            if (raw.isNullOrUndefined()) return null
            val js = raw!!.unsafeCast<LocationDataJs>()
            return LocationData(
                latitude = js.latitude.asDoubleOrNull() ?: return null,
                longitude = js.longitude.asDoubleOrNull() ?: return null,
                altitude = js.altitude.asDoubleOrNull(),
                course = js.course.asDoubleOrNull(),
                speed = js.speed.asDoubleOrNull(),
                horizontalAccuracy = js.horizontal_accuracy.asDoubleOrNull(),
                verticalAccuracy = js.vertical_accuracy.asDoubleOrNull(),
                courseAccuracy = js.course_accuracy.asDoubleOrNull(),
                speedAccuracy = js.speed_accuracy.asDoubleOrNull(),
            )
        }
    }
}
