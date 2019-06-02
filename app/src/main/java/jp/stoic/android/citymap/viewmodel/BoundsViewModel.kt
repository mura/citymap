package jp.stoic.android.citymap.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.stoic.android.citymap.vo.CityBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoundsViewModel(app: Application) : CoroutineViewModel(app) {
    private val boundsMap: MutableMap<String, CityBounds> = hashMapOf()

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

        launch {
            val cityBounds = withContext(Dispatchers.Default) {
                val typeToken = object : TypeToken<List<CityBounds>>() {}
                val reader = getApplication<Application>().assets.open("bounds.json").bufferedReader()
                Gson().fromJson<List<CityBounds>>(reader, typeToken.type)
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