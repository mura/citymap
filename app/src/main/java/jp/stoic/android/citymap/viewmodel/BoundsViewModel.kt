package jp.stoic.android.citymap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.stoic.android.citymap.repository.AssetRepository
import jp.stoic.android.citymap.vo.CityBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class BoundsViewModel @Inject constructor(
    private val assetRepository: AssetRepository
) : ViewModel() {
    private val boundsMap: HashMap<String, CityBounds> = hashMapOf()

    private val _cityBounds = MutableLiveData<CityBounds>()
    val cityBounds: LiveData<CityBounds>
        get() = _cityBounds

    @Synchronized
    fun searchBounds(code: String) {
        if (boundsMap.isNotEmpty()) {
            boundsMap[code]?.let {
                _cityBounds.value = it
            }
            return
        }

        viewModelScope.launch {
            val cityBounds: List<CityBounds> = withContext(Dispatchers.Default) {
                assetRepository.readBounds()
            }

            cityBounds.forEach {
                boundsMap[it.code] = it
            }

            boundsMap[code]?.let {
                _cityBounds.value = it
            }
        }
    }
}