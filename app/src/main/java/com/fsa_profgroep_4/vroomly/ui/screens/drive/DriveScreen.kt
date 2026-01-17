package com.fsa_profgroep_4.vroomly.ui.screens.drive

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.ui.screens.drive.components.*
import com.fsa_profgroep_4.vroomly.data.drive.DriveState
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import com.fsa_profgroep_4.vroomly.utils.LocationService
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DriveScreen(
    viewModel: DriveViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.onPermissionResult(permissions.values.all { it })
    }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
        if (!uiState.hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val isTrackingOrSaving = uiState.driveState is DriveState.Tracking || 
                           uiState.driveState is DriveState.Saving ||
                           (uiState.driveState is DriveState.Finished && uiState.error == null)

    BackHandler(enabled = isTrackingOrSaving) {
        // Do nothing, preventing back navigation during active drive
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            if (!isTrackingOrSaving) {
                VroomlyBackButton(
                    onBackClicked = { viewModel.onCancel() }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.screenPadding)
                    .padding(padding)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isLoading) {
                    DriveLoadingContent()
                } else if (!uiState.hasActiveReservation && uiState.driveState == DriveState.Idle) {
                    DriveNoReservationContent(
                        onSearchClick = { viewModel.goToSearch() }
                    )
                } else if (uiState.hasLocationPermission) {
                    when (uiState.driveState) {
                        is DriveState.Idle -> {
                            DriveIdleContent(
                                onStartTracking = {
                                    val intent = Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_START
                                    }
                                    context.startService(intent)
                                }
                            )
                        }
                        is DriveState.Tracking -> {
                            DriveTrackingContent(
                                elapsedTime = uiState.elapsedTime,
                                onStopTracking = {
                                    val intent = Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_STOP
                                    }
                                    context.startService(intent)
                                }
                            )
                        }
                        is DriveState.Saving -> {
                            DriveSavingContent()
                        }
                        is DriveState.Finished -> {
                            DriveFinishedContent(
                                error = uiState.error,
                                onRetrySave = { viewModel.saveDrive() }
                            )
                        }
                        is DriveState.Saved -> {
                            DriveSavedContent(
                                routePoints = (uiState.driveState as DriveState.Saved).routePoints,
                                onGoHome = { viewModel.goHome() }
                            )
                        }
                    }
                } else {
                    DrivePermissionContent(
                        showRationale = uiState.showRationale,
                        onOpenSettings = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        },
                        onGrantPermission = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }
            }
        }
    )
}
