package com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.type.ReservationStatus
import com.example.rocketreserver.type.ReservationUpdateInput
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.CreateReservation
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class ReviewReservationUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val reservationId: Int = -1,
    val vehicleId: Int = -1,

    val licensePlate: String = "",
    val imageUrls: List<String> = emptyList(),
    val status: ReservationStatus = ReservationStatus.PENDING,

    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,

    val costPerDay: Double? = null,
    val totalCost: Double? = null,

    val isSubmitting: Boolean = false
)

private const val TAG = "ReviewReservationViewModel"

class ReviewReservationViewModel(
    override val navigator: Navigator,
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(ReviewReservationUiState())
    val uiState: StateFlow<ReviewReservationUiState> = _uiState.asStateFlow()

    fun start(reservationId: Int) {
        if (_uiState.value.reservationId == reservationId && !_uiState.value.isLoading) return

        _uiState.value = ReviewReservationUiState(isLoading = true, reservationId = reservationId)

        viewModelScope.launch {
            val reservation = reservationRepository.getReservation(reservationId)
                .filterNotNull()
                .first()

            val vehicleId = reservation.vehicleId

            vehicleRepository.getVehicleById(vehicleId)
                .onSuccess { v ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null,
                        vehicleId = vehicleId,
                        licensePlate = v.licensePlate.orEmpty(),
                        imageUrls = v.images?.mapNotNull { it.url.takeIf { u -> u.isNotBlank() } }.orEmpty(),
                        startDate = LocalDate.parse(reservation.startDate),
                        endDate = LocalDate.parse(reservation.endDate),
                        status =  ReservationStatus.valueOf(reservation.status),
                        costPerDay = v.costPerDay,
                        totalCost = recalcTotal(
                            LocalDate.parse(reservation.startDate),
                            LocalDate.parse(reservation.endDate),
                            v.costPerDay)
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

    fun confirmReservation() = updateStatus(ReservationStatus.CONFIRMED)
    fun cancelReservation() = updateStatus(ReservationStatus.CANCELLED)

    private fun updateStatus(status: ReservationStatus) {
        val s = _uiState.value
        if (s.reservationId <= 0) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)

            val input = ReservationUpdateInput(
                id = s.reservationId,
                status = Optional.present(status)
            )

            reservationRepository.updateReservation(input)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSubmitting = false)
                    navigator.goBack()
                }
                .onFailure { e ->
                    Log.d(TAG, "updateReservation failed", e)
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = e.message ?: "Failed to update reservation"
                    )
                }
        }
    }

    fun onCancel() = navigator.goBack()


    private fun recalcTotal(start: LocalDate?, end: LocalDate?, cpd: Double?): Double? {
        if (start == null || end == null || cpd == null) return null
        val days = (end.toEpochDays() - start.toEpochDays() + 1).coerceAtLeast(1) // inclusive
        return days * cpd
    }
}