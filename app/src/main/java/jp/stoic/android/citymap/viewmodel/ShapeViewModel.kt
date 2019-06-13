package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShapeViewModel : ViewModel() {
    val currentCode = object : MutableLiveData<String>() {
        override fun setValue(value: String?) {
            if (this.value == value) {
                return
            }
            super.setValue(value)
        }
    }
}