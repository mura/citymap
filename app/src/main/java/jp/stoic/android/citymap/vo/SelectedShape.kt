package jp.stoic.android.citymap.vo

data class SelectedShape(val code: String, val name: String, val mode: Mode) {
    companion object {
        fun city(cityFeature: CityFeature) = SelectedShape(
            cityFeature.code, cityFeature.name, Mode.CITY
        )

        fun bigCity(cityFeature: CityFeature) = SelectedShape(
            cityFeature.bigCity, cityFeature.bigCityName, Mode.BIG_CITY
        )

        fun pref(cityFeature: CityFeature) = SelectedShape(
            cityFeature.pref, cityFeature.prefName, Mode.PREF
        )
    }

    enum class Mode {
        CITY,
        BIG_CITY,
        PREF
    }
}