package jp.stoic.android.citymap.repository

import androidx.annotation.WorkerThread
import jp.stoic.android.citymap.vo.CityBounds
import javax.inject.Inject

class BoundsRepository @Inject constructor(
    private val assetRepository: AssetRepository
) {
    private val boundsMap: HashMap<String, CityBounds> = hashMapOf()

    @WorkerThread
    fun search(code: String): CityBounds? {
        if (boundsMap.isEmpty()) {
            assetRepository.readBounds().forEach {
                boundsMap[it.code] = it
            }
        }

        return boundsMap[code]
    }
}