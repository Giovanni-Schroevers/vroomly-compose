package com.fsa_profgroep_4.vroomly.ui.screens.reservation.reserve

import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.user.IdentityProvider
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class CreateReservationUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val vehicleId: Int = -1,
    val licensePlate: String = "",
    val imageUrls: List<String> = emptyList(),

    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,

    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)

class CreateReservationViewModel(
    override val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val reservationRepository: ReservationRepository,
    private val identityProvider: IdentityProvider
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(CreateReservationUiState())
    val uiState: StateFlow<CreateReservationUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalTime::class)
    fun start(vehicleId: Int) {
        // avoid reloading on recomposition
        if (_uiState.value.vehicleId == vehicleId && !_uiState.value.isLoading) return

        _uiState.value = CreateReservationUiState(isLoading = true, vehicleId = vehicleId)

        viewModelScope.launch {
            vehicleRepository.getVehicleById(vehicleId)
                .onSuccess { v ->
                    val today = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null,
                        licensePlate = v.licensePlate.orEmpty(),
                        imageUrls = v.images
                            ?.mapNotNull { it.url.takeIf { u -> u.isNotBlank() } }
                            .orEmpty(),
                        startDate = today,
                        endDate = today
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

    fun setStartDate(date: LocalDate) {
        val end = _uiState.value.endDate
        _uiState.value = _uiState.value.copy(
            startDate = date,
            endDate = if (end != null && end < date) date else end
        )
    }

    fun setEndDate(date: LocalDate) {
        val start = _uiState.value.startDate
        _uiState.value = _uiState.value.copy(
            endDate = if (start != null && date < start) start else date
        )
    }

    fun submit() {
        val s = _uiState.value
        val start = s.startDate ?: return
        val end = s.endDate ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null, submitSuccess = false)

            val renterId = identityProvider.requireUserId()

            reservationRepository.createReservation(
                vehicleId = s.vehicleId,
                renterId = renterId,
                startDate = start,
                endDate = end
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isSubmitting = false, submitSuccess = true)
                navigator.goBack()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = e.message ?: "Failed to create reservation"
                )
            }
        }
    }

    fun onBack() = navigator.goBack()
}