package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.stoic.android.citymap.vo.TrackingMode

class CameraViewModel : ViewModel() {
    val trackingMode = MutableLiveData<TrackingMode>()

    fun invertTrackingMode() {
        if (trackingMode.value == TrackingMode.NONE) {
            trackingMode.value = TrackingMode.TRACKING
        } else {
            trackingMode.value = TrackingMode.NONE
        }
    }
}