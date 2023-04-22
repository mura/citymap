package jp.stoic.android.citymap.domain

import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.maps.MapboxMap
import jp.stoic.android.citymap.vo.TrackingMode

class CameraChanger(private val mapboxMap: MapboxMap) : Observer<TrackingMode> {
    private var trackingMode = TrackingMode.NONE

    override fun onChanged(value: TrackingMode) {
        if (!mapboxMap.locationComponent.isLocationComponentActivated ||
            !mapboxMap.locationComponent.isLocationComponentEnabled
        ) {
            return
        }

        if (value != trackingMode) {
            mapboxMap.locationComponent.cameraMode = value.cameraMode
            mapboxMap.locationComponent.renderMode = value.renderMode
            trackingMode = value
        }
    }
}