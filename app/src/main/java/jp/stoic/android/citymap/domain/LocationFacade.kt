package jp.stoic.android.citymap.domain

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import jp.stoic.android.citymap.viewmodel.MainViewModel
import jp.stoic.android.citymap.vo.TrackingMode

class LocationFacade(private val activity: AppCompatActivity) {
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private var style: Style? = null
    private val mainViewModel: MainViewModel by activity.viewModels()

    fun onResume() {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) return

        permissionsManager = PermissionsManager(object : PermissionsListener {
            override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
            }

            override fun onPermissionResult(granted: Boolean) {
            }

        })
        permissionsManager?.requestLocationPermissions(activity)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        enableLocationComponent()
    }

    fun onStyleLoaded(mapboxMap: MapboxMap, style: Style) {
        this.locationComponent = mapboxMap.locationComponent
        this.style = style
        enableLocationComponent()
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(): Boolean {
        if (!PermissionsManager.areLocationPermissionsGranted(activity)) {
            return false
        }

        val locationComponent = locationComponent ?: return false
        val style = style ?: return false

        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(activity, style).build()
        )
        locationComponent.isLocationComponentEnabled = true

        if (mainViewModel.trackingMode.value == null) {
            mainViewModel.trackingMode.value = TrackingMode.TRACKING
        }
        return true
    }
}