package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.common.VehicleForm
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun RegisterCarScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterCarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.screenPadding),
        topBar = { VroomlyBackButton(onBackClicked = { viewModel.onBackClicked() }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.register_car_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.medium)
                )

                if (uiState.generalError != null) {
                    Text(
                        text = uiState.generalError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                    )
                }

                VehicleForm(
                    licensePlate = uiState.licensePlate,
                    brand = uiState.brand,
                    model = uiState.model,
                    year = uiState.year,
                    color = uiState.color,
                    category = uiState.category,
                    engineType = uiState.engineType,
                    seats = uiState.seats,
                    costPerDay = uiState.costPerDay,
                    odometerKm = uiState.odometerKm,
                    motValidTill = uiState.motValidTill,
                    vin = uiState.vin,
                    zeroToHundred = uiState.zeroToHundred,
                    address = uiState.address,
                    isLoading = uiState.isLoading,
                    onLicensePlateChange = viewModel::onLicensePlateChange,
                    onBrandChange = viewModel::onBrandChange,
                    onModelChange = viewModel::onModelChange,
                    onYearChange = viewModel::onYearChange,
                    onColorChange = viewModel::onColorChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onEngineTypeChange = viewModel::onEngineTypeChange,
                    onSeatsChange = viewModel::onSeatsChange,
                    onCostPerDayChange = viewModel::onCostPerDayChange,
                    onOdometerKmChange = viewModel::onOdometerKmChange,
                    onMotValidTillChange = viewModel::onMotValidTillChange,
                    onVinChange = viewModel::onVinChange,
                    onZeroToHundredChange = viewModel::onZeroToHundredChange,
                    onAddressChange = viewModel::onAddressChange,
                    onSaveClick = viewModel::saveVehicle,
                    saveButtonText = stringResource(R.string.save)
                )
            }
        }
    )
}
