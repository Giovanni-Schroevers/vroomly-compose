package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.example.rocketreserver.type.EngineType
//import com.example.rocketreserver.type.VehicleCategory
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.models.FormField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterCarUiState(
    val licensePlate: FormField = FormField(),
    val brand: FormField = FormField(),
    val model: FormField = FormField(),
    val year: FormField = FormField(),
    val color: FormField = FormField(),
    val category: FormField = FormField(),
    val engineType: FormField = FormField(),
    val seats: FormField = FormField(value = "5"),
    val costPerDay: FormField = FormField(),
    val odometerKm: FormField = FormField(value = "0"),
    val motValidTill: FormField = FormField(),
    val vin: FormField = FormField(),
    val zeroToHundred: FormField = FormField(value = "0"),
    val address: FormField = FormField(),
    val latitude: FormField = FormField(value = "52.3676"),
    val longitude: FormField = FormField(value = "4.9041"),
    val isLoading: Boolean = false,
    val generalError: String? = null
) {
    fun hasErrors() = listOf(
        licensePlate, brand, model, year, color, seats,
        costPerDay, odometerKm, motValidTill, vin, zeroToHundred, address
    ).any { it.error != null }
}

class RegisterCarViewModel(
    private val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterCarUiState())
    val uiState: StateFlow<RegisterCarUiState> = _uiState.asStateFlow()

    fun onLicensePlateChange(value: String) {
        _uiState.value = _uiState.value.copy(licensePlate = _uiState.value.licensePlate.copy(value = value, error = null))
    }

    fun onBrandChange(value: String) {
        _uiState.value = _uiState.value.copy(brand = _uiState.value.brand.copy(value = value, error = null))
    }

    fun onModelChange(value: String) {
        _uiState.value = _uiState.value.copy(model = _uiState.value.model.copy(value = value, error = null))
    }

    fun onYearChange(value: String) {
        _uiState.value = _uiState.value.copy(year = _uiState.value.year.copy(value = value, error = null))
    }

    fun onColorChange(value: String) {
        _uiState.value = _uiState.value.copy(color = _uiState.value.color.copy(value = value, error = null))
    }

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(category = _uiState.value.category.copy(value = value, error = null))
    }

    fun onEngineTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(engineType = _uiState.value.engineType.copy(value = value, error = null))
    }

    fun onSeatsChange(value: String) {
        _uiState.value = _uiState.value.copy(seats = _uiState.value.seats.copy(value = value, error = null))
    }

    fun onCostPerDayChange(value: String) {
        _uiState.value = _uiState.value.copy(costPerDay = _uiState.value.costPerDay.copy(value = value, error = null))
    }

    fun onOdometerKmChange(value: String) {
        _uiState.value = _uiState.value.copy(odometerKm = _uiState.value.odometerKm.copy(value = value, error = null))
    }

    fun onMotValidTillChange(value: String) {
        _uiState.value = _uiState.value.copy(motValidTill = _uiState.value.motValidTill.copy(value = value, error = null))
    }

    fun onVinChange(value: String) {
        _uiState.value = _uiState.value.copy(vin = _uiState.value.vin.copy(value = value, error = null))
    }

    fun onZeroToHundredChange(value: String) {
        _uiState.value = _uiState.value.copy(zeroToHundred = _uiState.value.zeroToHundred.copy(value = value, error = null))
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = _uiState.value.address.copy(value = value, error = null))
    }

    fun onLatitudeChange(value: String) {
        _uiState.value = _uiState.value.copy(latitude = _uiState.value.latitude.copy(value = value, error = null))
    }

    fun onLongitudeChange(value: String) {
        _uiState.value = _uiState.value.copy(longitude = _uiState.value.longitude.copy(value = value, error = null))
    }

    fun saveVehicle() {
        val currentState = _uiState.value

        val validatedState = currentState.copy(
            licensePlate = currentState.licensePlate.validateRequired(application.getString(R.string.license_plate_is_required)),
            brand = currentState.brand.validateRequired(application.getString(R.string.brand_is_required)),
            model = currentState.model.validateRequired(application.getString(R.string.model_is_required)),
            year = currentState.year.validateRequired(application.getString(R.string.year_is_required)),
            color = currentState.color.validateRequired(application.getString(R.string.color_is_required)),
            seats = currentState.seats.validateRequired(application.getString(R.string.seats_is_required)),
            costPerDay = currentState.costPerDay.validateRequired(application.getString(R.string.cost_per_day_is_required)),
            odometerKm = currentState.odometerKm.validateRequired(application.getString(R.string.odometer_is_required)),
            motValidTill = currentState.motValidTill.validateRequired(application.getString(R.string.mot_valid_till_is_required)),
            vin = currentState.vin.validateRequired(application.getString(R.string.vin_is_required)),
            zeroToHundred = currentState.zeroToHundred.validateRequired(application.getString(R.string.zero_to_hundred_is_required)),
            address = currentState.address.validateRequired(application.getString(R.string.address_is_required))
        )

        _uiState.value = validatedState
        if (validatedState.hasErrors()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalError = null)

            val result = vehicleRepository.createVehicle(
                licensePlate = validatedState.licensePlate.value,
                brand = validatedState.brand.value,
                model = validatedState.model.value,
                year = validatedState.year.value.toIntOrNull() ?: 0,
                color = validatedState.color.value,
                category = validatedState.category.value,
                engineType = validatedState.engineType.value,
                seats = validatedState.seats.value.toIntOrNull() ?: 5,
                costPerDay = validatedState.costPerDay.value.toDoubleOrNull() ?: 0.0,
                odometerKm = validatedState.odometerKm.value.toDoubleOrNull() ?: 0.0,
                motValidTill = validatedState.motValidTill.value,
                vin = validatedState.vin.value,
                zeroToHundred = validatedState.zeroToHundred.value.toDoubleOrNull() ?: 0.0,
                address = validatedState.address.value,
                latitude = validatedState.latitude.value.toDoubleOrNull() ?: 52.3676,
                longitude = validatedState.longitude.value.toDoubleOrNull() ?: 4.9041
            )


            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                navigator.goBack()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = error.message?.split(" : ")?.last()
                        ?: application.getString(R.string.vehicle_registration_failed)
                )
            }
        }
    }

    fun onBackClicked() {
        navigator.goBack()
    }
}
