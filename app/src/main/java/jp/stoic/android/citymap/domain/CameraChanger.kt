package jp.stoic.android.citymap.domain

import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.maps.MapboxMap
import jp.stoic.android.citymap.vo.TrackingMode

class CameraChanger(private val mapboxMap: MapboxMap) : Observer<TrackingMode> {
    private var trackingMode = TrackingMode.NONE

    override fun onChanged(mode: TrackingMode?) {
        if (mode == null) {
            return
        }

        if (!mapboxMap.locationComponent.isLocationComponentActivated ||
            !mapboxMap.locationComponent.isLocationComponentEnabled
        ) {
            return
        }

        if (mode != trackingMode) {
            mapboxMap.locationComponent.cameraMode = mode.cameraMode
            mapboxMap.locationComponent.renderMode = mode.renderMode
            trackingMode = mode
        }
    }
}