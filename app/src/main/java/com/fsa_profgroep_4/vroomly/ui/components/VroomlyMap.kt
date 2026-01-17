package com.fsa_profgroep_4.vroomly.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.fsa_profgroep_4.vroomly.data.local.LocationPoint
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property.LINE_CAP_ROUND
import org.maplibre.android.style.layers.Property.LINE_JOIN_ROUND
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.lineCap
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineJoin
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.utils.ColorUtils
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

@Composable
fun RouteMapLibre(
    route: List<LocationPoint>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                getMapAsync { map ->

                    map.setStyle(
                        Style.Builder()
                            .fromUri("https://demotiles.maplibre.org/style.json")
                    ) { style ->

                        drawRoute(style, route)
                        addStartEndMarkers(style, route)
                        fitCameraToRoute(map, route)
                    }
                }
            }
        }
    )
}

private fun drawRoute(
    style: Style,
    route: List<LocationPoint>
) {
    val lineString = LineString.fromLngLats(
        route.map { Point.fromLngLat(it.lng, it.lat) }
    )

    val source = GeoJsonSource("route-source", lineString)
    style.addSource(source)

    val layer = LineLayer("route-layer", "route-source").apply {
        lineColor(ColorUtils.colorToRgbaString(Color.Blue.toArgb()))
        lineWidth(6f)
        lineJoin(LINE_JOIN_ROUND)
        lineCap(LINE_CAP_ROUND)
    }

    style.addLayer(layer)
}

private fun addStartEndMarkers(
    style: Style,
    route: List<LocationPoint>
) {
    val features = listOf(
        Feature.fromGeometry(
            Point.fromLngLat(route.first().lng, route.first().lat)
        ),
        Feature.fromGeometry(
            Point.fromLngLat(route.last().lng, route.last().lat)
        )
    )

    val source = GeoJsonSource(
        "markers-source",
        FeatureCollection.fromFeatures(features)
    )
    style.addSource(source)

    val layer = SymbolLayer("markers-layer", "markers-source").apply {
        iconImage("marker-15")
        iconAllowOverlap(true)
        iconIgnorePlacement(true)
    }

    style.addLayer(layer)
}

private fun fitCameraToRoute(
    map: MapLibreMap,
    route: List<LocationPoint>
) {
    val bounds = LatLngBounds.Builder().apply {
        route.forEach {
            include(LatLng(it.lat, it.lng))
        }
    }.build()

    map.easeCamera(
        CameraUpdateFactory.newLatLngBounds(bounds, 100),
        1200
    )
}