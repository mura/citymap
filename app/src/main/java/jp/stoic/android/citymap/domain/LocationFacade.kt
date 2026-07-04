package jp.stoic.android.citymap.domain

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import jp.stoic.android.citymap.viewmodel.MainViewModel
import jp.stoic.android.citymap.vo.TrackingMode

class LocationFacade(private val activity: AppCompatActivity) {
    private var mapView: MapView? = null
    private val mainViewModel: MainViewModel by activity.viewModels()

    fun onResume() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        activity.requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            0
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationComponent()
        }
    }

    fun onStyleLoaded(mapView: MapView) {
        this.mapView = mapView
        enableLocationComponent()
    }

    private fun enableLocationComponent(): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        val mapView = mapView ?: return false

        mapView.location.updateSettings {
            enabled = true
            locationPuck = createDefault2DPuck(withBearing = true)
        }

        if (mainViewModel.trackingMode.value == null) {
            mainViewModel.trackingMode.value = TrackingMode.TRACKING
        }
        return true
    }
}