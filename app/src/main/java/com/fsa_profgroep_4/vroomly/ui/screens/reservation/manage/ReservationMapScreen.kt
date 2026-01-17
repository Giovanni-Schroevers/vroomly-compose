package com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.screens.drive.components.DriveRouteMap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReservationMapScreen(
    reservationId: Int,
    modifier: Modifier = Modifier,
    viewModel: ReviewReservationViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermission = (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }

    LaunchedEffect(reservationId) { viewModel.start(reservationId) }

    LaunchedEffect(hasPermission, state.isLoading, state.vehicleLatitude, state.vehicleLongitude) {
        if (!hasPermission || state.isLoading) return@LaunchedEffect
        if (state.vehicleLatitude == 0.0 && state.vehicleLongitude == 0.0) return@LaunchedEffect
        if (state.routePoints.isNotEmpty()) return@LaunchedEffect

        getOneShotLocation(fusedClient) { lat, lng ->
            viewModel.setRoutePoints(lat, lng)
        }
    }

    Scaffold(
        topBar = { VroomlyBackButton(onBackClicked = { viewModel.onCancel() }) },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (!hasPermission) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) { Text("Enable location") }
                }
                return@Column
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                DriveRouteMap(routePoints = state.routePoints)
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getOneShotLocation(
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient,
    onResult: (Double, Double) -> Unit
) {
    val token = CancellationTokenSource()
    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
        .addOnSuccessListener { loc ->
            if (loc != null) onResult(loc.latitude, loc.longitude)
        }
}