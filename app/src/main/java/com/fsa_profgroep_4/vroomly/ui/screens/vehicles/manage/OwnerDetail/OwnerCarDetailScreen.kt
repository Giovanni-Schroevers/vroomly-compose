package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun OwnerCarDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerCarDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentRoute by remember { mutableStateOf("account") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VroomlyBackButton(onBackClicked = { viewModel.onBackClicked() })
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            VroomlyBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    viewModel.onNavigate(route)
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            val vehicle = uiState.vehicle
            if (vehicle != null) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = MaterialTheme.spacing.screenPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_directions_car),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.no_images),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                    // License Plate
                    Text(
                        text = stringResource(R.string.license_plate),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = vehicle.licensePlate,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                    // Reservations section
                    Text(
                        text = stringResource(R.string.reservations),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    if (uiState.reservations.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_reservations),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.reservations.forEach { reservation ->
                            ReservationItem(
                                startDate = reservation.startDate.toString(),
                                endDate = reservation.endDate.toString(),
                                status = reservation.status.name,
                                totalCost = reservation.totalCost,
                                paid = reservation.paid
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                    // Vehicle details in 50/50 rows
                    Text(
                        text = stringResource(R.string.vehicle_details),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    // Row 1: Brand + Model
                    DetailRow(
                        label1 = stringResource(R.string.brand),
                        value1 = vehicle.brand,
                        label2 = stringResource(R.string.model),
                        value2 = vehicle.model
                    )

                    // Row 2: Year + Color
                    DetailRow(
                        label1 = stringResource(R.string.year),
                        value1 = vehicle.year.toString(),
                        label2 = stringResource(R.string.color),
                        value2 = vehicle.color
                    )

                    // Row 3: Category + Engine Type
                    DetailRow(
                        label1 = stringResource(R.string.category),
                        value1 = vehicle.category.name,
                        label2 = stringResource(R.string.engine_type),
                        value2 = vehicle.engineType.name
                    )

                    // Row 4: Seats + Cost per Day
                    DetailRow(
                        label1 = stringResource(R.string.seats),
                        value1 = vehicle.seats.toString(),
                        label2 = stringResource(R.string.cost_per_day),
                        value2 = "€${vehicle.costPerDay}"
                    )

                    // Row 5: Odometer + MOT Valid Till
                    DetailRow(
                        label1 = stringResource(R.string.odometer_km),
                        value1 = "${vehicle.odometerKm} km",
                        label2 = stringResource(R.string.mot_valid_till),
                        value2 = vehicle.motValidTill
                    )

                    // Row 6: VIN + Zero to Hundred
                    DetailRow(
                        label1 = stringResource(R.string.vin),
                        value1 = vehicle.vin,
                        label2 = stringResource(R.string.zero_to_hundred),
                        value2 = "${vehicle.zeroToHundred}s"
                    )

                    // Row 7: Review Stars + Status
                    DetailRow(
                        label1 = stringResource(R.string.reviews),
                        value1 = "${vehicle.reviewStars}★",
                        label2 = stringResource(R.string.status),
                        value2 = vehicle.status.name
                    )

                    // Address (full width)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = stringResource(R.string.address),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = vehicle.location?.address ?: "-",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                    // Edit button
                    Button(
                        onClick = { viewModel.onEditCarClicked() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.edit_car)
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    // Delete button
                    Button(
                        onClick = { viewModel.showDeleteConfirmation() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = !uiState.isDeleting
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onError
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.delete_vehicle),
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.screenPadding))
                }
            }
        }

        // Delete confirmation dialog
        if (uiState.showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteConfirmation() },
                title = {
                    Text(text = stringResource(R.string.delete_vehicle))
                },
                text = {
                    Text(text = stringResource(R.string.delete_vehicle_confirmation))
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteVehicle() }
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.hideDeleteConfirmation() }
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailRow(
    label1: String,
    value1: String,
    label2: String,
    value2: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label1,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value1,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label2,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value2,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ReservationItem(
    startDate: String,
    endDate: String,
    status: String,
    totalCost: Double,
    paid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(MaterialTheme.spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$startDate - $endDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "€$totalCost",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (paid) "Paid" else "Not paid",
                style = MaterialTheme.typography.labelSmall,
                color = if (paid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
