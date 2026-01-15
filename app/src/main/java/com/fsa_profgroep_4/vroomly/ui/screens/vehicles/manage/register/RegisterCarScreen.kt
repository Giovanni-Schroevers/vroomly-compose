package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rocketreserver.type.EngineType
import com.example.rocketreserver.type.VehicleCategory
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyDatePickerField
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
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

                // License Plate (full width at top)
                VroomlyTextField(
                    value = uiState.licensePlate.value,
                    onValueChange = { viewModel.onLicensePlateChange(it) },
                    label = stringResource(R.string.license_plate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.small),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.licensePlate.error
                )

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
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                // Add image button
                // TODO add add image functionality
                VroomlyButton(
                    text = stringResource(R.string.add_image),
                    onClick = { /* No functionality yet */ },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Row 1: Brand + Model
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    VroomlyTextField(
                        value = uiState.brand.value,
                        onValueChange = { viewModel.onBrandChange(it) },
                        label = stringResource(R.string.brand),
                        modifier = Modifier.weight(1f),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.brand.error
                    )

                    VroomlyTextField(
                        value = uiState.model.value,
                        onValueChange = { viewModel.onModelChange(it) },
                        label = stringResource(R.string.model),
                        modifier = Modifier.weight(1f),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.model.error
                    )
                }

                // Row 2: Year + Color
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    VroomlyTextField(
                        value = uiState.year.value,
                        onValueChange = { viewModel.onYearChange(it) },
                        label = stringResource(R.string.year),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.year.error
                    )

                    VroomlyTextField(
                        value = uiState.color.value,
                        onValueChange = { viewModel.onColorChange(it) },
                        label = stringResource(R.string.color),
                        modifier = Modifier.weight(1f),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.color.error
                    )
                }

                // Row 3: Category + Engine Type
                var categoryExpanded by remember { mutableStateOf(false) }
                var engineTypeExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        VroomlyTextField(
                            value = uiState.category.value.ifEmpty { stringResource(R.string.select_category) },
                            onValueChange = {},
                            label = stringResource(R.string.category),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            enabled = !uiState.isLoading
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            VehicleCategory.entries.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.onCategoryChange(category.name)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = engineTypeExpanded,
                        onExpandedChange = { engineTypeExpanded = !engineTypeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        VroomlyTextField(
                            value = uiState.engineType.value.ifEmpty { stringResource(R.string.select_engine_type) },
                            onValueChange = {},
                            label = stringResource(R.string.engine_type),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = engineTypeExpanded) },
                            enabled = !uiState.isLoading
                        )
                        ExposedDropdownMenu(
                            expanded = engineTypeExpanded,
                            onDismissRequest = { engineTypeExpanded = false }
                        ) {
                            EngineType.entries.forEach { engineType ->
                                DropdownMenuItem(
                                    text = { Text(engineType.name) },
                                    onClick = {
                                        viewModel.onEngineTypeChange(engineType.name)
                                        engineTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Row 4: Seats + Cost per Day
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    VroomlyTextField(
                        value = uiState.seats.value,
                        onValueChange = { viewModel.onSeatsChange(it) },
                        label = stringResource(R.string.seats),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.seats.error
                    )

                    VroomlyTextField(
                        value = uiState.costPerDay.value,
                        onValueChange = { viewModel.onCostPerDayChange(it) },
                        label = stringResource(R.string.cost_per_day),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.costPerDay.error
                    )
                }

                // Row 5: Odometer + MOT Valid Till
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    VroomlyTextField(
                        value = uiState.odometerKm.value,
                        onValueChange = { viewModel.onOdometerKmChange(it) },
                        label = stringResource(R.string.odometer_km),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.odometerKm.error
                    )

                    VroomlyDatePickerField(
                        value = uiState.motValidTill.value,
                        onValueChange = { viewModel.onMotValidTillChange(it) },
                        label = stringResource(R.string.mot_valid_till),
                        modifier = Modifier.weight(1f),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.motValidTill.error
                    )
                }

                // Row 6: VIN + Zero to Hundred (0-100 km/h)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    VroomlyTextField(
                        value = uiState.vin.value,
                        onValueChange = { viewModel.onVinChange(it) },
                        label = stringResource(R.string.vin),
                        modifier = Modifier.weight(1f),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.vin.error
                    )

                    VroomlyTextField(
                        value = uiState.zeroToHundred.value,
                        onValueChange = { viewModel.onZeroToHundredChange(it) },
                        label = stringResource(R.string.zero_to_hundred),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        required = true,
                        enabled = !uiState.isLoading,
                        errorText = uiState.zeroToHundred.error
                    )
                }

                // Address (full width)
                VroomlyTextField(
                    value = uiState.address.value,
                    onValueChange = { viewModel.onAddressChange(it) },
                    label = stringResource(R.string.address),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.small),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.address.error
                )

                Box(contentAlignment = Alignment.Center) {
                    VroomlyButton(
                        text = stringResource(R.string.save),
                        onClick = { viewModel.saveVehicle() },
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.screenPadding),
                        enabled = !uiState.isLoading
                    )
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}
