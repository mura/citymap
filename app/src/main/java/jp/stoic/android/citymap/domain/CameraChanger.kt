package jp.stoic.android.citymap.domain

import androidx.lifecycle.Observer
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.viewport.viewport
import jp.stoic.android.citymap.vo.TrackingMode

class CameraChanger(private val mapView: MapView) : Observer<TrackingMode> {
    private var trackingMode = TrackingMode.NONE

    override fun onChanged(value: TrackingMode) {
        if (value != trackingMode) {
            when (value) {
                TrackingMode.TRACKING -> {
                    mapView.viewport.transitionTo(
                        mapView.viewport.makeFollowPuckViewportState()
                    )
                }
                TrackingMode.NONE -> {
                    mapView.viewport.idle()
                }
            }
            trackingMode = value
        }
    }
}