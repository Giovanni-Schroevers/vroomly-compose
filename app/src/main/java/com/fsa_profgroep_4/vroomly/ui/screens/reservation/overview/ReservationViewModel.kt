package com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.rocketreserver.GetVehicleByIdQuery
import com.fsa_profgroep_4.vroomly.data.local.ReservationEntity
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.user.IdentityProvider
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
    private val identityProvider: IdentityProvider
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(ReservationsCardState())
    val uiState: StateFlow<ReservationsCardState> = _uiState.asStateFlow()

    init {
        loadPage()
    }

    private fun loadPage(){
        viewModelScope.launch {
            val renterId = identityProvider.requireUserId()
            reservationRepository.getReservationsByRenterId(renterId)
                .collect { reservations ->
                    val items = reservations.map { reservation ->
                        var vehicle = vehicleRepository
                            .getVehicleById(reservation.vehicleId)
                            .getOrNull()
                            ?.toVehicleCardUi()

                        if (vehicle == null){
                            vehicle = VehicleCardUi(
                                vehicleId = 0,
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

private fun GetVehicleByIdQuery.GetVehicleById.toVehicleCardUi() = VehicleCardUi(
    vehicleId = requireNotNull(id),
    imageUrl = images.firstOrNull()?.url ?: "",
    title = "$brand $model",
    location = location.address,
    owner = "Owner #${ownerId}",
    tagText = engineType.name,
    badgeText = reviewStars.toString(),
    costPerDay = costPerDay
)