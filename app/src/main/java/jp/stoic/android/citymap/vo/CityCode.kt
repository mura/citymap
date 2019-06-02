package jp.stoic.android.citymap.vo

import com.mapbox.geojson.Feature

data class CityCode(
    val code: String,
    val bigCity: String,
    val pref: String
) {
    companion object {
        fun from(features: List<Feature>): CityCode {
            val feature = features
                .filter { feature ->
                    val index = feature.geometry()?.type()?.indexOf("Polygon") ?: -1
                    index >= 0
                }
                .first { feature ->
                    feature.getProperty("CODE") != null
                }

            val code = feature.getProperty("CODE").asString
            val codec = feature.getProperty("CODE_C")?.asString ?: ""
            val codep = feature.getProperty("CODE_P")?.asString ?: ""
            return CityCode(code, codec, codep)
        }
    }
}