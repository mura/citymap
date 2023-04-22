package jp.stoic.android.citymap

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
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
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapLifecycleOwner = MapLifecycleOwner().also {
            lifecycle.addObserver(it)
        }

        shapeViewModel.selectedShape.observe(this, viewModel::onShapeSelected)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            viewModel.trackingMode.observe(mapLifecycleOwner, CameraChanger(mapboxMap))
            viewModel.cityBounds.observe(mapLifecycleOwner) {
                viewModel.trackingMode.value = TrackingMode.NONE
                mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(it.latLngBounds, 50))
            }
            mapLifecycleOwner.onStartStyleLoad()
            val styleBuilder = Style.Builder()
                .fromUri("mapbox://styles/muracchi/cjw2rwfof0by31cox2yuck1j6")
            mapboxMap.setStyle(styleBuilder) { style ->
                this.currentStyle = style
                mapLifecycleOwner.onStyleLoaded()
                locationFacade.onStyleLoaded(mapboxMap, style)
                mapboxMap.addOnMapClickListener(ShapeSelector(mapboxMap, style, shapeViewModel))
                viewModel.searchBounds("23100")
            }
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

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isOpen) {
                    binding.drawerLayout.close()
                }
            }
        })
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

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        locationFacade.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    private fun ActivityMainBinding.hideToolbar() {
        toolbarLayout.root.visibility = View.GONE
    }

    private fun ActivityMainBinding.showToolbar(@StringRes resId: Int) {
        toolbarLayout.root.visibility = View.VISIBLE
        toolbarLayout.toolbar.setTitle(resId)
    }
}
