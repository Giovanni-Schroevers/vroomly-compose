package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.tco

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rocketreserver.UpdateVehicleTcoDataMutation
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.models.FormField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TCOUiState(
    val acquisitionCost: FormField = FormField(),
    val currentMarketValue: FormField = FormField(),
    val fuelConsumptionPer100Km: FormField = FormField(),
    val fuelPricePerLiter: FormField = FormField(),
    val insuranceCostsPerYear: FormField = FormField(),
    val maintenanceCosts: FormField = FormField(),
    val taxAndRegistrationPerYear: FormField = FormField(),
    val yearsOwned: FormField = FormField(),
    val tCOResult: Double? = null,
    val costPerKm: Double? = null,
    val isLoading: Boolean = true,
    val generalError: String? = null
)

class TcoViewModel(
    private val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val vehicleId: Int
): ViewModel() {
    private val _uiState = MutableStateFlow(TCOUiState())
    val uiState: StateFlow<TCOUiState> = _uiState.asStateFlow()

    init {
        loadTCOData()
    }

    private fun loadTCOData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalError = null)

            vehicleRepository.getVehicleTCOData(vehicleId =  vehicleId).onSuccess { tCOData ->
                _uiState.value = _uiState.value.copy(
                    acquisitionCost = FormField(value = tCOData.acquisitionCost.toString()),
                    currentMarketValue = FormField(value = tCOData.currentMarketValue.toString()),
                    fuelConsumptionPer100Km = FormField(value = tCOData.fuelConsumptionPer100Km.toString()),
                    fuelPricePerLiter = FormField(value = tCOData.fuelPricePerLiter.toString()),
                    insuranceCostsPerYear = FormField(value = tCOData.insuranceCostsPerYear.toString()),
                    maintenanceCosts = FormField(value = tCOData.maintenanceCosts.toString()),
                    taxAndRegistrationPerYear = FormField(value = tCOData.taxAndRegistrationPerYear.toString()),
                    yearsOwned = FormField(value = tCOData.yearsOwned.toString()),
                    generalError = null
                )

                getTCOResult()
            }.onFailure {
                vehicleRepository.saveVehicleTCOData(vehicleId = vehicleId)
                _uiState.value = _uiState.value.copy(isLoading = false, generalError = it.message ?: "Unknown error")
            }
        }
    }

    fun saveTCOData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalError = null)

            val state = _uiState.value
            vehicleRepository.updateVehicleTCOData(
                vehicleId = vehicleId,
                acquisitionCost = state.acquisitionCost.value.toDoubleOrNull(),
                currentMarketValue = state.currentMarketValue.value.toDoubleOrNull(),
                fuelConsumptionPer100Km = state.fuelConsumptionPer100Km.value.toDoubleOrNull(),
                fuelPricePerLiter = state.fuelPricePerLiter.value.toDoubleOrNull(),
                insuranceCostsPerYear = state.insuranceCostsPerYear.value.toDoubleOrNull(),
                maintenanceCosts = state.maintenanceCosts.value.toDoubleOrNull(),
                taxAndRegistrationPerYear = state.taxAndRegistrationPerYear.value.toDoubleOrNull(),
                yearsOwned = state.yearsOwned.value.toIntOrNull() ?: 0
            ).onSuccess {
                getTCOResult()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, generalError = it.message ?: "Unknown error")
            }
        }
    }

    private fun getTCOResult() {
        viewModelScope.launch {
            vehicleRepository.vehicleTcoById(vehicleId = vehicleId).onSuccess { tcoResult ->
                vehicleRepository.vehicleConsumptionById(vehicleId = vehicleId).onSuccess { vehicleConsumption ->
                    _uiState.value = _uiState.value.copy(
                        tCOResult = tcoResult.tcoValue,
                        costPerKm = vehicleConsumption.costPerKm,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onAcquisitionCostChange(value: String) {
        _uiState.value = _uiState.value.copy(acquisitionCost = _uiState.value.acquisitionCost.copy(value = value, error = null))
    }

    fun onCurrentMarketValueChange(value: String) {
        _uiState.value = _uiState.value.copy(currentMarketValue = _uiState.value.currentMarketValue.copy(value = value, error = null))
    }

    fun onFuelConsumptionPer100KmChange(value: String) {
        _uiState.value = _uiState.value.copy(fuelConsumptionPer100Km = _uiState.value.fuelConsumptionPer100Km.copy(value = value, error = null))
    }

    fun onFuelPricePerLiterChange(value: String) {
        _uiState.value = _uiState.value.copy(fuelPricePerLiter = _uiState.value.fuelPricePerLiter.copy(value = value, error = null))
    }

    fun onInsuranceCostsPerYearChange(value: String) {
        _uiState.value = _uiState.value.copy(insuranceCostsPerYear = _uiState.value.insuranceCostsPerYear.copy(value = value, error = null))
    }

    fun onMaintenanceCostsChange(value: String) {
        _uiState.value = _uiState.value.copy(maintenanceCosts = _uiState.value.maintenanceCosts.copy(value = value, error = null))
    }

    fun onTaxAndRegistrationPerYearChange(value: String) {
        _uiState.value = _uiState.value.copy(taxAndRegistrationPerYear = _uiState.value.taxAndRegistrationPerYear.copy(value = value, error = null))
    }

    fun onYearsOwnedChange(value: String) {
        _uiState.value = _uiState.value.copy(yearsOwned = _uiState.value.yearsOwned.copy(value = value, error = null))
    }

    fun onBack() {
        navigator.goBack()
    }
}
