package jp.stoic.android.citymap

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.scalebar.scalebar
import dagger.hilt.android.AndroidEntryPoint
import jp.stoic.android.citymap.databinding.ActivityMainBinding
import jp.stoic.android.citymap.domain.CameraChanger
import jp.stoic.android.citymap.domain.LocationFacade
import jp.stoic.android.citymap.domain.ShapeSelector
import jp.stoic.android.citymap.lifecycle.MapLifecycleOwner
import jp.stoic.android.citymap.viewmodel.MainViewModel
import jp.stoic.android.citymap.viewmodel.ShapeViewModel
import jp.stoic.android.citymap.vo.TrackingMode

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mapboxMap: MapboxMap? = null
    private var currentStyle: Style? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapLifecycleOwner: MapLifecycleOwner

    private val locationFacade by lazy { LocationFacade(this) }

    private val viewModel: MainViewModel by viewModels()
    private val shapeViewModel: ShapeViewModel by viewModels()

    private val navHostController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapLifecycleOwner = MapLifecycleOwner().also {
            lifecycle.addObserver(it)
        }

        shapeViewModel.selectedShape.observe(this, viewModel::onShapeSelected)

        val mapboxMap = binding.mapView.mapboxMap
        this.mapboxMap = mapboxMap

        val density = resources.displayMetrics.density
        binding.mapView.scalebar.updateSettings {
            isMetricUnits = true
            position = android.view.Gravity.BOTTOM or android.view.Gravity.END
            marginRight = 60f * density
            marginBottom = 24f * density
        }

        binding.mapView.compass.updateSettings {
            marginTop = 16f * density
            marginRight = 16f * density
        }

        mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(com.mapbox.geojson.Point.fromLngLat(136.8818636, 35.1695319))
                .zoom(11.0)
                .build()
        )

        viewModel.trackingMode.observe(mapLifecycleOwner, CameraChanger(binding.mapView))
        viewModel.cityBounds.observe(mapLifecycleOwner) {
            viewModel.trackingMode.value = TrackingMode.NONE
            val cameraOptions = mapboxMap.cameraForCoordinates(
                coordinates = listOf(it.coordinateBounds.southwest, it.coordinateBounds.northeast),
                camera = CameraOptions.Builder().build(),
                coordinatesPadding = EdgeInsets(50.0, 50.0, 50.0, 50.0),
                maxZoom = null,
                offset = null
            )
            binding.mapView.camera.easeTo(cameraOptions)
        }

        mapLifecycleOwner.onStartStyleLoad()

        mapboxMap.loadStyle("mapbox://styles/muracchi/cjw2rwfof0by31cox2yuck1j6") { style ->
            this.currentStyle = style
            mapLifecycleOwner.onStyleLoaded()
            locationFacade.onStyleLoaded(binding.mapView)
            binding.mapView.gestures.addOnMapClickListener(ShapeSelector(mapboxMap, shapeViewModel))
            viewModel.searchBounds("23100")
        }

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.let {
            val navController = it.findNavController()
            binding.toolbarLayout.toolbar.setupWithNavController(navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.mainFragment -> binding.hideToolbar()
                    R.id.historyFragment -> binding.showToolbar(R.string.fragment_history)
                }
            }
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.activity_license_title))

        viewModel.drawerIsOpen.observe(this) {
            if (binding.drawerLayout.isOpen == it) {
                return@observe
            }
            when (it) {
                true -> binding.drawerLayout.open()
                else -> binding.drawerLayout.close()
            }
            viewModel.drawerIsOpen.value = !it
        }

        onBackPressedDispatcher.addCallback {
            if (binding.drawerLayout.isOpen) {
                binding.drawerLayout.close()
            } else {
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationFacade.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navHostController.navigate(R.id.action_main_to_history)
            }

            R.id.nav_license -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        locationFacade.onResume()
    }

    private fun ActivityMainBinding.hideToolbar() {
        toolbarLayout.root.visibility = View.GONE
    }

    private fun ActivityMainBinding.showToolbar(@StringRes resId: Int) {
        toolbarLayout.root.visibility = View.VISIBLE
        toolbarLayout.toolbar.setTitle(resId)
    }
}
