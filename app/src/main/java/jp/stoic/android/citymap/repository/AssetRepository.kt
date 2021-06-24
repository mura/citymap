package jp.stoic.android.citymap.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.stoic.android.citymap.vo.CityBounds

class AssetRepository(
    private val context: Context
) {

    @WorkerThread
    fun readBounds(): List<CityBounds> {
        val typeToken = object : TypeToken<List<CityBounds>>() {}
        val reader = context.assets.open("bounds.json").bufferedReader()
        return Gson().fromJson(reader, typeToken.type)
    }
}