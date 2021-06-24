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

        Timber.tag("ShapeSelector").d("$cityFeature")
        val nextShape = nextShape(cityFeature)
        when (nextShape.mode) {
            SelectedShape.Mode.CITY -> {
                officeLayer?.setFilter(eqCode(cityFeature.code))
                cityShapeLayer?.setFilter(eqCode(cityFeature.code))
            }
            SelectedShape.Mode.BIG_CITY -> {
                officeLayer?.setFilter(
                    Expression.any(eqCode(cityFeature.bigCity), eqCodeC(cityFeature.bigCity))
                )
                cityShapeLayer?.setFilter(eqCodeC(cityFeature.bigCity))
            }
            SelectedShape.Mode.PREF -> {
                officeLayer?.setFilter(eqCodeP(cityFeature.pref))
                cityShapeLayer?.setFilter(eqCodeP(cityFeature.pref))
            }
        }
        shapeViewModel.setShape(nextShape)
        return true
    }

    private fun eqCode(code: String) = Expression.eq(Expression.get("CODE"), code)
    private fun eqCodeC(code: String) = Expression.eq(Expression.get("CODE_C"), code)
    private fun eqCodeP(code: String) = Expression.eq(Expression.get("CODE_P"), code)

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