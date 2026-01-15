package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerDetail

import androidx.lifecycle.viewModelScope
import com.example.rocketreserver.GetReservationsByVehicleIdQuery
import com.example.rocketreserver.GetVehicleByIdQuery
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OwnerCarDetailUiState(
    val vehicle: GetVehicleByIdQuery.GetVehicleById? = null,
    val reservations: List<GetReservationsByVehicleIdQuery.GetReservationsByVehicleId> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val error: String? = null
)

class OwnerCarDetailViewModel(
    override val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val vehicleId: Int
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(OwnerCarDetailUiState())
    val uiState: StateFlow<OwnerCarDetailUiState> = _uiState.asStateFlow()

    init {
        loadVehicleDetails()
    }

    private fun loadVehicleDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            vehicleRepository.getVehicleById(vehicleId)
                .onSuccess { vehicle ->
                    _uiState.value = _uiState.value.copy(vehicle = vehicle)
                    loadReservations()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
        }
    }

    private fun loadReservations() {
        viewModelScope.launch {
            vehicleRepository.getReservationsByVehicleId(vehicleId)
                .onSuccess { reservations ->
                    _uiState.value = _uiState.value.copy(
                        reservations = reservations,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
        }
    }

    fun onBackClicked() {
        navigator.goBack()
    }

    fun showDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = true)
    }

    fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
    }

    fun deleteVehicle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                showDeleteConfirmation = false
            )

            vehicleRepository.deleteVehicleById(vehicleId)
                .onSuccess {
                    navigator.goBack()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        error = e.message ?: "Failed to delete vehicle"
                    )
                }
        }
    }
}
