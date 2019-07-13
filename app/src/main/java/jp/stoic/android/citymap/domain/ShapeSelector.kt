package jp.stoic.android.citymap.domain

import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import jp.stoic.android.citymap.viewmodel.ShapeViewModel
import jp.stoic.android.citymap.vo.CityFeature
import jp.stoic.android.citymap.vo.SelectedShape
import timber.log.Timber

class ShapeSelector(
    private val mapboxMap: MapboxMap,
    style: Style,
    private val shapeViewModel: ShapeViewModel
) : MapboxMap.OnMapClickListener {
    companion object {
        private const val SHAPE_LAYER_ID = "city-shape"
        private const val SHAPE_DATA_LAYER_ID = "city-shape-data"
    }

    private var cityShapeLayer: FillLayer? = style.getLayerAs(SHAPE_LAYER_ID)
    private var officeLayer: SymbolLayer? = style.getLayerAs("office")

    override fun onMapClick(point: LatLng): Boolean {
        val screenPoint = mapboxMap.projection.toScreenLocation(point)
        val features = mapboxMap.queryRenderedFeatures(screenPoint, SHAPE_DATA_LAYER_ID)
        if (features.size == 0) {
            return false
        }
        val cityFeature = CityFeature.from(features)

        Timber.tag("onMapClick").d("$cityFeature")
        val nextShape = nextShape(cityFeature)
        when (nextShape.mode) {
            SelectedShape.Mode.CITY -> {
                officeLayer?.setFilter(Expression.eq(Expression.get("CODE"), cityFeature.code))
                cityShapeLayer?.setFilter(Expression.eq(Expression.get("CODE"), cityFeature.code))
            }
            SelectedShape.Mode.BIG_CITY -> {
                officeLayer?.setFilter(
                    Expression.any(
                        Expression.eq(Expression.get("CODE"), cityFeature.bigCity),
                        Expression.eq(Expression.get("CODE_C"), cityFeature.bigCity)
                    )
                )
                cityShapeLayer?.setFilter(Expression.eq(Expression.get("CODE_C"), cityFeature.bigCity))
            }
            SelectedShape.Mode.PREF -> {
                officeLayer?.setFilter(Expression.eq(Expression.get("CODE_P"), cityFeature.pref))
                cityShapeLayer?.setFilter(Expression.eq(Expression.get("CODE_P"), cityFeature.pref))
            }
        }
        shapeViewModel.selectedShape.value = nextShape
        return true
    }

    private fun nextShape(cityFeature: CityFeature): SelectedShape {
        val currentShape = shapeViewModel.selectedShape.value
        return if (currentShape?.code == cityFeature.code) {
            if (cityFeature.bigCity.isNotEmpty()) {
                SelectedShape.bigCity(cityFeature)
            } else {
                SelectedShape.pref(cityFeature)
            }
        } else if (currentShape?.code == cityFeature.bigCity) {
            SelectedShape.pref(cityFeature)
        } else {
            SelectedShape.city(cityFeature)
        }
    }
}