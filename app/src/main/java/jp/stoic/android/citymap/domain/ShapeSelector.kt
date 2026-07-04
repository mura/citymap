package jp.stoic.android.citymap.domain

import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.extension.style.expressions.dsl.generated.eq
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import jp.stoic.android.citymap.viewmodel.ShapeViewModel
import jp.stoic.android.citymap.vo.CityFeature
import jp.stoic.android.citymap.vo.SelectedShape
import timber.log.Timber

class ShapeSelector(
    private val mapboxMap: MapboxMap,
    private val shapeViewModel: ShapeViewModel
) : OnMapClickListener {
    companion object {
        private const val SHAPE_LAYER_ID = "city-shape"
        private const val SHAPE_DATA_LAYER_ID = "city-shape-data"
    }

    override fun onMapClick(point: Point): Boolean {
        val screenPoint = mapboxMap.pixelForCoordinate(point)
        val geometry = RenderedQueryGeometry(screenPoint)
        val options = RenderedQueryOptions(listOf(SHAPE_DATA_LAYER_ID), null)

        mapboxMap.queryRenderedFeatures(geometry, options) { expected ->
            val features = expected.value
            if (features.isNullOrEmpty()) {
                return@queryRenderedFeatures
            }

            // Extract geojson features
            val geojsonFeatures = features.map { it.queriedFeature.feature }
            val cityFeature = CityFeature.from(geojsonFeatures)

            Timber.tag("ShapeSelector").d("$cityFeature")
            val nextShape = nextShape(cityFeature)

            val style = mapboxMap.style
            val cityShapeLayer = style?.getLayerAs<FillLayer>(SHAPE_LAYER_ID)
            val officeLayer = style?.getLayerAs<SymbolLayer>("office")

            when (nextShape.mode) {
                SelectedShape.Mode.CITY -> {
                    officeLayer?.filter(eqCode(cityFeature.code))
                    cityShapeLayer?.filter(eqCode(cityFeature.code))
                }
                SelectedShape.Mode.BIG_CITY -> {
                    officeLayer?.filter(
                        Expression.any(eqCode(cityFeature.bigCity), eqCodeC(cityFeature.bigCity))
                    )
                    cityShapeLayer?.filter(eqCodeC(cityFeature.bigCity))
                }
                SelectedShape.Mode.PREF -> {
                    officeLayer?.filter(eqCodeP(cityFeature.pref))
                    cityShapeLayer?.filter(eqCodeP(cityFeature.pref))
                }
            }
            shapeViewModel.setShape(nextShape)
        }
        return true
    }

    private fun eqCode(code: String) = Expression.eq(Expression.get("CODE"), Expression.literal(code))
    private fun eqCodeC(code: String) = Expression.eq(Expression.get("CODE_C"), Expression.literal(code))
    private fun eqCodeP(code: String) = Expression.eq(Expression.get("CODE_P"), Expression.literal(code))

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