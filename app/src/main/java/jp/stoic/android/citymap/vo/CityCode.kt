package jp.stoic.android.citymap.vo

import com.mapbox.geojson.Feature

data class CityCode(
    val code: String,
    val bigCity: String,
    val pref: String,
    val name: String,
    val bigCityName: String,
    val prefName: String
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
            val name = feature.getProperty("N03_004").asString ?: ""
            val bigCityName = if (codec.isNotEmpty()) {
                if (codep == "01" && codec != "01100") {
                    // 北海道の札幌市以外は振興局
                    feature.getProperty("N03_002").asString ?: ""
                } else if (codec == "13100") {
                    // 東京23区
                    "特別区部"
                } else {
                    // 他は政令指定都市 or 支庁
                    feature.getProperty("N03_003").asString ?: ""
                }
            } else {
                ""
            }
            val prefName = feature.getProperty("N03_001").asString ?: ""

            feature.getProperty("N03_003").asString ?: ""
            return CityCode(code, codec, codep, name, bigCityName, prefName)
        }
    }
}