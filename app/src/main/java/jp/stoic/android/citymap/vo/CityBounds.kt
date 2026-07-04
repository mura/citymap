package jp.stoic.android.citymap.vo

import androidx.annotation.Keep
import com.mapbox.geojson.Point
import com.mapbox.maps.CoordinateBounds

@Keep
data class CityBounds(val code: String, val bounds: List<Double>) {
    val coordinateBounds: CoordinateBounds
        get() = CoordinateBounds(
            Point.fromLngLat(bounds[0], bounds[1]),
            Point.fromLngLat(bounds[2], bounds[3])
        )
}