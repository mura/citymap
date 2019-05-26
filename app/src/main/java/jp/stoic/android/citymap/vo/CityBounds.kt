package jp.stoic.android.citymap.vo

import androidx.annotation.Keep
import com.mapbox.mapboxsdk.geometry.LatLngBounds

@Keep
data class CityBounds(val code: String, val bounds: List<Double>) {
    val latLngBounds: LatLngBounds
        get() = LatLngBounds.from(bounds[3], bounds[2], bounds[1], bounds[0])
}