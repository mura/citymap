package jp.stoic.android.citymap.viewmodel

import android.view.View
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.android.core.permissions.PermissionsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.stoic.android.citymap.domain.Analytics
import jp.stoic.android.citymap.repository.BoundsRepository
import jp.stoic.android.citymap.repository.HistoryRepository
import jp.stoic.android.citymap.vo.CityBounds
import jp.stoic.android.citymap.vo.SelectedShape
import jp.stoic.android.citymap.vo.TrackingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val boundsRepository: BoundsRepository,
    private val historyRepository: HistoryRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val _cityBounds = MutableLiveData<CityBounds>()
    val trackingMode = MutableLiveData<TrackingMode>()

    val cityBounds: LiveData<CityBounds>
        get() = _cityBounds

    @Synchronized
    fun searchBounds(code: String) {
        viewModelScope.launch {
            val cityBounds = withContext(Dispatchers.Default) {
                boundsRepository.search(code)
            }
            cityBounds?.let {
                _cityBounds.value = it
            }
        }
    }

    @MainThread
    fun invertTrackingMode() {
        if (trackingMode.value == TrackingMode.NONE) {
            trackingMode.value = TrackingMode.TRACKING
        } else {
            trackingMode.value = TrackingMode.NONE
        }
    }

    fun onMyLocationClick(view: View) {
        if (!PermissionsManager.areLocationPermissionsGranted(view.context)) {
            return
        }

        invertTrackingMode()
    }

    fun onShapeSelected(selectedShape: SelectedShape) {
        analytics.clickShape(selectedShape)
        searchBounds(selectedShape.code)
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                historyRepository.insert(selectedShape)
            }
        }
    }
}