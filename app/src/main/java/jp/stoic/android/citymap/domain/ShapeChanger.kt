package jp.stoic.android.citymap.domain

import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import jp.stoic.android.citymap.viewmodel.BoundsViewModel
import jp.stoic.android.citymap.vo.CityCode
import timber.log.Timber

class ShapeChanger(
    private val mapboxMap: MapboxMap,
    style: Style,
    private val boundsViewModel: BoundsViewModel
) : MapboxMap.OnMapClickListener {
    companion object {
        private const val SHAPE_LAYER_ID = "city-shape"
        private const val SHAPE_DATA_LAYER_ID = "city-shape-data"
    }

    private var cityShapeLayer: FillLayer? = style.getLayerAs(SHAPE_LAYER_ID)
    private var officeLayer: SymbolLayer? = style.getLayerAs("office")
    var currentCode: String = "23100"

    private enum class Mode {
        CITY,
        BIG_CITY,
        PREF
    }

    override fun onMapClick(point: LatLng): Boolean {
        val screenPoint = mapboxMap.projection.toScreenLocation(point)
        val features = mapboxMap.queryRenderedFeatures(screenPoint, SHAPE_DATA_LAYER_ID)
        if (features.size == 0) {
            return false
        }
        val cityCode = CityCode.from(features)

        Timber.tag("onMapClick")
            .d("CODE: ${cityCode.code}, CODE_P: ${cityCode.pref} CODE_C: ${cityCode.bigCity}")
        when (nextMode(cityCode.code, cityCode.bigCity)) {
            Mode.CITY -> {
                officeLayer?.setFilter(Expression.eq(Expression.get("CODE"), cityCode.code))
                cityShapeLayer?.setFilter(Expression.eq(Expression.get("CODE"), cityCode.code))
                currentCode = cityCode.code
            }
            Mode.BIG_CITY -> {
                officeLayer?.setFilter(
                    Expression.any(
                        Expression.eq(Expression.get("CODE"), cityCode.bigCity),
                        Expression.eq(Expression.get("CODE_C"), cityCode.bigCity)
                    )
                )
                cityShapeLayer?.setFilter(Expression.eq(Expression.get("CODE_C"), cityCode.bigCity))
                currentCode = cityCode.bigCity
            }
            Mode.PREF -> {
                officeLayer?.setFilter(Expression.eq(Expression.get("CODE_P"), cityCode.pref))
                cityShapeLayer?.setFilter(Expression.eq(Expression.get("CODE_P"), cityCode.pref))
                currentCode = cityCode.pref
            }
        }
        boundsViewModel.searchBounds(currentCode)
        return true
    }

    private fun nextMode(code: String, cityCode: String): Mode {
        return if (currentCode == code) {
            if (cityCode.isNotEmpty()) {
                Mode.BIG_CITY
            } else {
                Mode.PREF
            }
        } else if (currentCode == cityCode) {
            Mode.PREF
        } else {
            Mode.CITY
        }
    }
}