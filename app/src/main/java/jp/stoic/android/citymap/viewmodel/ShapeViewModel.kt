package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.stoic.android.citymap.vo.SelectedShape

class ShapeViewModel : ViewModel() {
    val selectedShape = MutableLiveData<SelectedShape>()
}