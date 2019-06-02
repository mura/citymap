package jp.stoic.android.citymap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import jp.stoic.android.citymap.domain.CameraChanger
import jp.stoic.android.citymap.domain.ShapeChanger
import jp.stoic.android.citymap.viewmodel.BoundsViewModel
import jp.stoic.android.citymap.viewmodel.CameraViewModel
import jp.stoic.android.citymap.vo.TrackingMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var permissionsManager: PermissionsManager? = null
    private var mapboxMap: MapboxMap? = null
    private var currentStyle: Style? = null

    private var shapeChanger: ShapeChanger? = null

    private lateinit var boundsViewModel: BoundsViewModel
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)
        job = Job()

        boundsViewModel = ViewModelProviders.of(this).get(BoundsViewModel::class.java)
        boundsViewModel.cityBounds.observe(this, Observer {
            if (shapeChanger?.currentCode == it.code) {
                easeCameraWithBounds(it.latLngBounds)
            }
        })
        cameraViewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            cameraViewModel.trackingMode.observe(this, CameraChanger(mapboxMap))
            mapboxMap.setStyle("mapbox://styles/muracchi/cjw2rwfof0by31cox2yuck1j6") { style ->
                this.currentStyle = style
                enableLocationComponent(mapboxMap, style)
                //mapboxMap.addOnMoveListener(createOnMoveListener())
                val shapeChanger = ShapeChanger(mapboxMap, style, boundsViewModel)
                mapboxMap.addOnMapClickListener(shapeChanger)
                boundsViewModel.searchBounds(shapeChanger.currentCode)
                this.shapeChanger = shapeChanger
            }
        }

        myLocationImageButton.setOnClickListener {
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                return@setOnClickListener
            }

            cameraViewModel.invertTrackingMode()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(mapboxMap: MapboxMap, style: Style) {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            return
        }

        mapboxMap.locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(this, style).build()
        )
        mapboxMap.locationComponent.isLocationComponentEnabled = true
        cameraViewModel.trackingMode.value = TrackingMode.TRACKING
    }

    private fun enableLocationComponent() {
        val mapboxMap = this.mapboxMap ?: return
        val style = this.currentStyle ?: return
        enableLocationComponent(mapboxMap, style)
    }

    private fun easeCameraWithBounds(bounds: LatLngBounds) {
        cameraViewModel.trackingMode.value = TrackingMode.NONE
        mapboxMap?.let {
            it.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        enableLocationComponent()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                }

                override fun onPermissionResult(granted: Boolean) {
                }

            })
            permissionsManager?.requestLocationPermissions(this)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        job.cancel()
        shapeChanger?.let {
            mapboxMap?.removeOnMapClickListener(it)
        }
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState ?: Bundle())
    }
}
