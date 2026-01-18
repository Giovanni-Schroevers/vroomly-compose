package com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.rocketreserver.GetVehicleByIdQuery
import com.fsa_profgroep_4.vroomly.data.local.ReservationEntity
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.ReviewReservation
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import com.fsa_profgroep_4.vroomly.ui.components.ReservationCardData
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReservationsCardState (
    val items: List<ReservationCardData> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false,
    val hasMore: Boolean = false
)
private const val TAG = "ReservationViewModel"

class ReservationViewModel(
    override val navigator: Navigator,
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(ReservationsCardState())
    val uiState: StateFlow<ReservationsCardState> = _uiState.asStateFlow()

    init {
        loadPage()
    }

    private fun loadPage(){
        Log.d(TAG, "loadPage outside viewModelScope.launch")
        var renterId = 0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            Log.d(TAG, "loadPage inside viewModelScope.launch")
            userRepository.getCurrentUser().collect { user ->
                user?.let {
                    renterId = user.id
                    Log.d(TAG, "renterId = $renterId in let")

                    reservationRepository.getReservationsByRenterId(renterId)
                        .collect { reservations ->
                            Log.d(TAG, "reservationRepository.getReservationsByRenterId(renterId) collect")
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
                                hasMore = true
                            )
                        }
                }
                }
            }
    }

    fun onLoadMore(){
        _uiState.value = _uiState.value.copy(
            hasMore = false
        )
    }

    fun onCancel() {
        navigator.goBack()
    }
    fun onReservationSelected(reservationId: Int) {
        navigator.goTo(ReviewReservation(reservationId))
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
    imageUrl = images
        .filter { it.url.isNotBlank() }
        .minByOrNull { it.number }
        ?.url
        ?: "error",
    title = "$brand $model",
    location = location.address,
    owner = "Owner #${ownerId}",
    tagText = engineType.name,
    badgeText = reviewStars.toString(),
    costPerDay = costPerDay
)