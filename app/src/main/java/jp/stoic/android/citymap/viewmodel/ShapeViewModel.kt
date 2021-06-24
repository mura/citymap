package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import jp.stoic.android.citymap.vo.SelectedShape

class ShapeViewModel : ViewModel() {
    private val _selectedShape = MutableLiveData<SelectedShape>()

    val selectedShape = _selectedShape.distinctUntilChanged()

    fun setShape(selectedShape: SelectedShape) {
        _selectedShape.value = selectedShape
    }
}