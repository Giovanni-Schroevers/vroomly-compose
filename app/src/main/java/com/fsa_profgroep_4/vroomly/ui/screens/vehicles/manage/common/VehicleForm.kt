package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rocketreserver.type.EngineType
import com.example.rocketreserver.type.VehicleCategory
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyDatePickerField
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.models.FormField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleForm(
    licensePlate: FormField,
    brand: FormField,
    model: FormField,
    year: FormField,
    color: FormField,
    category: FormField,
    engineType: FormField,
    seats: FormField,
    costPerDay: FormField,
    odometerKm: FormField,
    motValidTill: FormField,
    vin: FormField,
    zeroToHundred: FormField,
    address: FormField,
    isLoading: Boolean,
    onLicensePlateChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onEngineTypeChange: (String) -> Unit,
    onSeatsChange: (String) -> Unit,
    onCostPerDayChange: (String) -> Unit,
    onOdometerKmChange: (String) -> Unit,
    onMotValidTillChange: (String) -> Unit,
    onVinChange: (String) -> Unit,
    onZeroToHundredChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    saveButtonText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        VroomlyTextField(
            value = licensePlate.value,
            onValueChange = onLicensePlateChange,
            label = stringResource(R.string.license_plate),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.small),
            required = true,
            enabled = !isLoading,
            errorText = licensePlate.error
        )

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

        VroomlyButton(
            text = stringResource(R.string.add_image),
            onClick = { /* No functionality yet */ },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            VroomlyTextField(
                value = brand.value,
                onValueChange = onBrandChange,
                label = stringResource(R.string.brand),
                modifier = Modifier.weight(1f),
                required = true,
                enabled = !isLoading,
                errorText = brand.error
            )

            VroomlyTextField(
                value = model.value,
                onValueChange = onModelChange,
                label = stringResource(R.string.model),
                modifier = Modifier.weight(1f),
                required = true,
                enabled = !isLoading,
                errorText = model.error
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            VroomlyTextField(
                value = year.value,
                onValueChange = onYearChange,
                label = stringResource(R.string.year),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                required = true,
                enabled = !isLoading,
                errorText = year.error
            )

            VroomlyTextField(
                value = color.value,
                onValueChange = onColorChange,
                label = stringResource(R.string.color),
                modifier = Modifier.weight(1f),
                required = true,
                enabled = !isLoading,
                errorText = color.error
            )
        }

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
                    value = category.value.ifEmpty { stringResource(R.string.select_category) },
                    onValueChange = {},
                    label = stringResource(R.string.category),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    VehicleCategory.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                onCategoryChange(cat.name)
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
                    value = engineType.value.ifEmpty { stringResource(R.string.select_engine_type) },
                    onValueChange = {},
                    label = stringResource(R.string.engine_type),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = engineTypeExpanded) },
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = engineTypeExpanded,
                    onDismissRequest = { engineTypeExpanded = false }
                ) {
                    EngineType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                onEngineTypeChange(type.name)
                                engineTypeExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            VroomlyTextField(
                value = seats.value,
                onValueChange = onSeatsChange,
                label = stringResource(R.string.seats),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                required = true,
                enabled = !isLoading,
                errorText = seats.error
            )

            VroomlyTextField(
                value = costPerDay.value,
                onValueChange = onCostPerDayChange,
                label = stringResource(R.string.cost_per_day),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                required = true,
                enabled = !isLoading,
                errorText = costPerDay.error
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            VroomlyTextField(
                value = odometerKm.value,
                onValueChange = onOdometerKmChange,
                label = stringResource(R.string.odometer_km),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                required = true,
                enabled = !isLoading,
                errorText = odometerKm.error
            )

            VroomlyDatePickerField(
                value = motValidTill.value,
                onValueChange = onMotValidTillChange,
                label = stringResource(R.string.mot_valid_till),
                modifier = Modifier.weight(1f),
                required = true,
                enabled = !isLoading,
                errorText = motValidTill.error
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            VroomlyTextField(
                value = vin.value,
                onValueChange = onVinChange,
                label = stringResource(R.string.vin),
                modifier = Modifier.weight(1f),
                required = true,
                enabled = !isLoading,
                errorText = vin.error
            )

            VroomlyTextField(
                value = zeroToHundred.value,
                onValueChange = onZeroToHundredChange,
                label = stringResource(R.string.zero_to_hundred),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                required = true,
                enabled = !isLoading,
                errorText = zeroToHundred.error
            )
        }

        VroomlyTextField(
            value = address.value,
            onValueChange = onAddressChange,
            label = stringResource(R.string.address),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.small),
            required = true,
            enabled = !isLoading,
            errorText = address.error
        )

        Box(contentAlignment = Alignment.Center) {
            VroomlyButton(
                text = saveButtonText,
                onClick = onSaveClick,
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.screenPadding),
                enabled = !isLoading
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
