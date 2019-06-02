package jp.stoic.android.citymap.vo

import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode

data class TrackingMode(
    @CameraMode.Mode val cameraMode: Int,
    @RenderMode.Mode val renderMode: Int
) {
    companion object {
        val NONE = TrackingMode(CameraMode.NONE, RenderMode.NORMAL)
        val TRACKING = TrackingMode(CameraMode.TRACKING, RenderMode.COMPASS)
    }
}