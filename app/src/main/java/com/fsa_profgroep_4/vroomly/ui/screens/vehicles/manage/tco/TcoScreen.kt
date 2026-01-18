package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.tco

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyFormButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun TcoScreen(viewModel: TcoViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            VroomlyBackButton(
                onBackClicked = { viewModel.onBack() }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = MaterialTheme.spacing.screenPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = stringResource(R.string.tco_data),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.large)
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.medium)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.tco_is))
                    val tcoText = when {
                        uiState.isLoading -> stringResource(R.string.loading)
                        uiState.tCOResult != null -> uiState.tCOResult.toString()
                        else -> stringResource(R.string.not_yet_available)
                    }
                    Text(tcoText)
                }

                Row(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.medium)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.cost_per_km))
                    val costPerDay = when {
                        uiState.isLoading -> stringResource(R.string.loading)
                        uiState.tCOResult != null -> uiState.costPerKm.toString()
                        else -> stringResource(R.string.not_yet_available)
                    }
                    Text(costPerDay)
                }

                VroomlyTextField(
                    value = uiState.acquisitionCost.value,
                    errorText = uiState.acquisitionCost.error,
                    label = stringResource(R.string.acquisition_cost),
                    onValueChange = { viewModel.onAcquisitionCostChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.currentMarketValue.value,
                    errorText = uiState.currentMarketValue.error,
                    label = stringResource(R.string.current_market_value),
                    onValueChange = { viewModel.onCurrentMarketValueChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.fuelConsumptionPer100Km.value,
                    errorText = uiState.fuelConsumptionPer100Km.error,
                    label = stringResource(R.string.fuel_consumption_per_100_km),
                    onValueChange = { viewModel.onFuelConsumptionPer100KmChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.fuelPricePerLiter.value,
                    errorText = uiState.fuelPricePerLiter.error,
                    label = stringResource(R.string.fuel_price_per_liter),
                    onValueChange = { viewModel.onFuelPricePerLiterChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.insuranceCostsPerYear.value,
                    errorText = uiState.insuranceCostsPerYear.error,
                    label = stringResource(R.string.insurance_costs_per_year),
                    onValueChange = { viewModel.onInsuranceCostsPerYearChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.maintenanceCosts.value,
                    errorText = uiState.maintenanceCosts.error,
                    label = stringResource(R.string.maintenance_costs),
                    onValueChange = { viewModel.onMaintenanceCostsChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.taxAndRegistrationPerYear.value,
                    errorText = uiState.taxAndRegistrationPerYear.error,
                    label = stringResource(R.string.tax_and_registration_per_year),
                    onValueChange = { viewModel.onTaxAndRegistrationPerYearChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyTextField(
                    value = uiState.yearsOwned.value,
                    errorText = uiState.yearsOwned.error,
                    label = stringResource(R.string.years_owned),
                    onValueChange = { viewModel.onYearsOwnedChange(it) },
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.extraSmall)
                        .fillMaxWidth()
                )

                VroomlyFormButton(
                    text = stringResource(R.string.save_tco_data),
                    onClick = { viewModel.saveTCOData() },
                    isLoading = uiState.isLoading
                )
            }
        }

    )
}
