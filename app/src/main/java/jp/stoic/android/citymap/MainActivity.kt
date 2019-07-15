package jp.stoic.android.citymap

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import jp.stoic.android.citymap.domain.Analytics
import jp.stoic.android.citymap.domain.CameraChanger
import jp.stoic.android.citymap.domain.LocationFacade
import jp.stoic.android.citymap.domain.ShapeSelector
import jp.stoic.android.citymap.lifecycle.MapLifecycleOwner
import jp.stoic.android.citymap.viewmodel.BoundsViewModel
import jp.stoic.android.citymap.viewmodel.CameraViewModel
import jp.stoic.android.citymap.viewmodel.HistoryViewModel
import jp.stoic.android.citymap.viewmodel.ShapeViewModel
import jp.stoic.android.citymap.vo.TrackingMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var mapboxMap: MapboxMap? = null
    private var currentStyle: Style? = null

    private lateinit var mapLifecycleOwner: MapLifecycleOwner

    private val locationFacade by lazy { LocationFacade(this) }

    private val boundsViewModel: BoundsViewModel by viewModels()
    private val cameraViewModel: CameraViewModel by viewModels()
    private val shapeViewModel: ShapeViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    private lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)
        job = Job()

        analytics = Analytics(FirebaseAnalytics.getInstance(this))

        mapLifecycleOwner = MapLifecycleOwner().also {
            lifecycle.addObserver(it)
        }

        shapeViewModel.selectedShape.observe(this, Observer {
            analytics.clickShape(it)
            historyViewModel.insert(it)
            boundsViewModel.searchBounds(it.code)
        })

        historyViewModel.history.observe(this, Observer {
            Timber.d("History: $it")
        })

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            cameraViewModel.trackingMode.observe(mapLifecycleOwner, CameraChanger(mapboxMap))
            boundsViewModel.cityBounds.observe(mapLifecycleOwner, Observer {
                cameraViewModel.trackingMode.value = TrackingMode.NONE
                mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(it.latLngBounds, 50))
            })
            mapLifecycleOwner.onStartStyleLoad()
            mapboxMap.setStyle("mapbox://styles/muracchi/cjw2rwfof0by31cox2yuck1j6") { style ->
                this.currentStyle = style
                mapLifecycleOwner.onStyleLoaded()
                locationFacade.onStyleLoaded(mapboxMap, style)
                mapboxMap.addOnMapClickListener(ShapeSelector(mapboxMap, style, shapeViewModel))
                boundsViewModel.searchBounds("23100")
            }
        }

        navigationView.setNavigationItemSelectedListener(this)
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.activity_license_title))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationFacade.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_license -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        locationFacade.onResume()
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
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
