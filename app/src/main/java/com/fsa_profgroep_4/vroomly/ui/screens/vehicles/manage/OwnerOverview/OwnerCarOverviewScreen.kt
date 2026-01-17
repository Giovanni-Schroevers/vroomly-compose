package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerOverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.fsa_profgroep_4.vroomly.ui.components.VehicleListItem
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun OwnerCarOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: OwnerCarOverviewViewModel
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = MaterialTheme.spacing.screenPadding)
                .fillMaxSize()
        ) {
            VroomlyButton(
                text = stringResource(R.string.register_car_button),
                onClick = { viewModel.onRegisterCar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.medium),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_directions_car),
                        contentDescription = null
                    )
                }
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_cars_registered),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.items) { vehicle ->
                            VehicleListItem(
                                data = vehicle,
                                onClick = { viewModel.onCarClicked(vehicle.vehicleId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
