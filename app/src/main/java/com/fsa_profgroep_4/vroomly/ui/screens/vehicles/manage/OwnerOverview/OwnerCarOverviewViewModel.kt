package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerOverview

import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.OwnerCarDetail
import com.fsa_profgroep_4.vroomly.navigation.RegisterCar
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class OwnerCarOverviewUiState(
    val items: List<VehicleCardUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OwnerCarOverviewViewModel(
    override val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(OwnerCarOverviewUiState())
    val uiState: StateFlow<OwnerCarOverviewUiState> = _uiState.asStateFlow()

    init {
        loadUserVehicles()
    }

    fun loadUserVehicles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val currentUser = userRepository.getCurrentUser().first()
            if (currentUser == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "User not logged in"
                )
                return@launch
            }

            vehicleRepository.getVehiclesByOwnerId(currentUser.id)
                .onSuccess { vehicles ->
                    _uiState.value = _uiState.value.copy(
                        items = vehicles,
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

    fun onRegisterCar() {
        navigator.goTo(RegisterCar)
    }

    fun onCarClicked(vehicleId: Int) {
        navigator.goTo(OwnerCarDetail(vehicleId))
    }

    fun onBackClicked() {
        navigator.goBack()
    }
}
