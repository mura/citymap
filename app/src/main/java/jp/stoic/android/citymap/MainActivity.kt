package jp.stoic.android.citymap

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.play.core.splitcompat.SplitCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import jp.stoic.android.citymap.viewmodel.BoundsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mapview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        private const val SHAPE_LAYER_ID = "city-shape"
        private const val SHAPE_DATA_LAYER_ID = "city-shape-data"
    }

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var permissionsManager: PermissionsManager? = null
    private var mapboxMap: MapboxMap? = null
    private var currentStyle: Style? = null

    private var cityShapeLayer: FillLayer? = null
    private var officeLayer: SymbolLayer? = null
    private var currentCode: String = "23100"

    private lateinit var boundsViewModel: BoundsViewModel

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)
        job = Job()

        boundsViewModel = ViewModelProviders.of(this).get(BoundsViewModel::class.java)
        boundsViewModel.cityBound.observe(this, Observer {
            if (currentCode == it.code) {
                easeCameraWithBounds(it.latLngBounds)
            }
        })

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle("mapbox://styles/muracchi/cjw2rwfof0by31cox2yuck1j6") { style ->
                this.currentStyle = style
                enableLocationComponent(mapboxMap, style)
                cityShapeLayer = style.getLayerAs(SHAPE_LAYER_ID)
                officeLayer = style.getLayerAs("office")
                //mapboxMap.addOnMoveListener(createOnMoveListener())
                mapboxMap.addOnMapClickListener(createOnMapCLickListener())
                boundsViewModel.searchBounds(currentCode)
            }
        }

        myLocationImageButton.setOnClickListener {
            if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                return@setOnClickListener
            }

            mapboxMap?.let {
                if (!it.locationComponent.isLocationComponentEnabled) {
                    return@let
                }
                if (it.locationComponent.cameraMode == CameraMode.TRACKING) {
                    it.locationComponent.cameraMode = CameraMode.NONE
                    it.locationComponent.renderMode = RenderMode.NORMAL
                } else {
                    it.locationComponent.cameraMode = CameraMode.TRACKING
                    it.locationComponent.renderMode = RenderMode.COMPASS
                }
            }
        }

        menuImageButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun createOnMapCLickListener(): MapboxMap.OnMapClickListener {
        return MapboxMap.OnMapClickListener { latLng ->
            mapboxMap?.let {
                val point = it.projection.toScreenLocation(latLng)
                val features = it.queryRenderedFeatures(point, SHAPE_DATA_LAYER_ID)
                if (features.size == 0) {
                    return@let
                }
                val feature = features
                    .filter { feature ->
                        val index = feature.geometry()?.type()?.indexOf("Polygon") ?: -1
                        index >= 0
                    }
                    .first { feature ->
                        feature.getProperty("CODE") != null
                    }

                val code = feature.getProperty("CODE").asString
                val codec = feature.getProperty("CODE_C").asString
                val codep = feature.getProperty("CODE_P").asString
                Timber.tag("onMapClick").d("CODE: $code, CODE_P: $codep CODE_C: $codec")
                if (currentCode == code) {
                    if (codec.isNotEmpty()) {
                        officeLayer?.setFilter(any(eq(get("CODE"), codec), eq(get("CODE_C"), codec)))
                        cityShapeLayer?.setFilter(eq(get("CODE_C"), codec))
                        currentCode = codec
                    } else {
                        officeLayer?.setFilter(eq(get("CODE_P"), codep))
                        cityShapeLayer?.setFilter(eq(get("CODE_P"), codep))
                        currentCode = codep
                    }
                    boundsViewModel.searchBounds(currentCode)
                    return@let
                } else if (currentCode == codec) {
                    officeLayer?.setFilter(eq(get("CODE_P"), codep))
                    cityShapeLayer?.setFilter(eq(get("CODE_P"), codep))
                    currentCode = codep
                    boundsViewModel.searchBounds(currentCode)
                    return@let
                }
                currentCode = code
                officeLayer?.setFilter(eq(get("CODE"), code))
                cityShapeLayer?.setFilter(eq(get("CODE"), code))
                boundsViewModel.searchBounds(currentCode)
                return@OnMapClickListener true
            }

            return@OnMapClickListener false
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
        mapboxMap.locationComponent.cameraMode = CameraMode.TRACKING
        mapboxMap.locationComponent.renderMode = RenderMode.COMPASS
    }

    private fun enableLocationComponent() {
        val mapboxMap = this.mapboxMap ?: return
        val style = this.currentStyle ?: return
        enableLocationComponent(mapboxMap, style)
    }

    private fun easeCameraWithBounds(bounds: LatLngBounds) {
        mapboxMap?.let {
            if (it.locationComponent.isLocationComponentEnabled) {
                it.locationComponent.cameraMode = CameraMode.NONE
                it.locationComponent.renderMode = RenderMode.NORMAL
            }
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
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState ?: Bundle())
    }
}
