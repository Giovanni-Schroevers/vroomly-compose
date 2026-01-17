package com.fsa_profgroep_4.vroomly.ui.screens.drive.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import com.example.rocketreserver.type.LocationSnapshotInput
import org.maplibre.android.geometry.LatLng
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle

@Composable
fun DriveRouteMap(
    routePoints: List<LocationSnapshotInput>,
    modifier: Modifier = Modifier
) {
    if (routePoints.isEmpty()) return

    val points = remember(routePoints) {
        routePoints.map { LatLng(it.latitude, it.longitude) }
    }

    val cameraState = rememberCameraState()

    val boundingBox = remember(points) {
        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }

        BoundingBox(
            west = minLon,
            south = minLat,
            east = maxLon,
            north = maxLat
        )
    }

    LaunchedEffect(boundingBox) {
        cameraState.animateTo(
            boundingBox = boundingBox,
            padding = PaddingValues(50.dp)
        )
    }

    MaplibreMap(
        modifier = modifier.fillMaxSize(),
        cameraState = cameraState,
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
    ) {
        val routeGeoJson = remember(points) {
            LineString(
                coordinates = points.map { Position(it.longitude, it.latitude) }
            )
        }

        val routeSource = rememberGeoJsonSource(
            data = GeoJsonData.Features(
                geoJson = Feature(geometry = routeGeoJson, properties = null)
            )
        )

        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        LineLayer(
            id = "route-layer",
            source = routeSource,
            color = const(Color.Blue),
            width = const(5.dp),
        )
    }
}
