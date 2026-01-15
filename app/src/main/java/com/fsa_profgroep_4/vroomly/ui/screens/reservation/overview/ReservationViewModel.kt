package com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.local.ReservationEntity
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import com.fsa_profgroep_4.vroomly.ui.components.ReservationCardData
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReservationsCardState (
    val items: List<ReservationCardData> = emptyList(),
    val error: String? = null
)

class ReservationViewModel(
    override val navigator: Navigator,
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository,
    private val application: Application
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(ReservationsCardState())
    val uiState: StateFlow<ReservationsCardState> = _uiState.asStateFlow()

    init {
        loadPage()
    }

    private fun loadPage(){
        viewModelScope.launch {
            reservationRepository.getReservationsByRenterId(1)
                .collect { reservations ->
                    val items = reservations.map { reservation ->
                        val vehicle = vehicleRepository
                            .getVehicleById(reservation.vehicleId)
                            .getOrElse {
                                VehicleCardUi(
                                    imageUrl = "",
                                    title = "Unknown vehicle",
                                    location = "-",
                                    owner = "-",
                                    tagText = "",
                                    badgeText = "",
                                    costPerDay = 0.0
                                )
                            }

                        reservation.toCardData(vehicle)
                    }

                    _uiState.value = _uiState.value.copy(
                        items = items,
                        error = null
                    )
                }
        }
    }
    fun onCancel() {
        navigator.goBack()
    }
}

private fun ReservationEntity.toCardData(vehicle: VehicleCardUi): ReservationCardData =
    ReservationCardData(
        reservationId = id,
        status = status,
        totalCost = totalCost,
        imageUrl = vehicle.imageUrl,
        title = vehicle.title,
        location = vehicle.location,
        vehicleId = vehicleId,
    )