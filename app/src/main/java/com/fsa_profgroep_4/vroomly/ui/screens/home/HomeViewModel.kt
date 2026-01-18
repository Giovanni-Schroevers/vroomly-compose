package com.fsa_profgroep_4.vroomly.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.GetVehicleByIdQuery
import com.fsa_profgroep_4.vroomly.data.local.ReservationEntity
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.CreateReservation
import com.fsa_profgroep_4.vroomly.navigation.Drive
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.ReviewReservation
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import com.fsa_profgroep_4.vroomly.ui.components.ReservationCardData
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardHomeUi
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val vehiclesToRent: List<VehicleCardHomeUi> = emptyList(),
    val vehiclesRented: List<ReservationCardData> = emptyList(),
    val myVehicles: List<VehicleCardHomeUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val pageSize: Int = 4
)

class HomeViewModel(
    override val navigator: Navigator,
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val application: Application
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var userBoundJob: Job? = null

    init {
        observeUserBoundData()
        loadFirstPage()
    }

    fun loadFirstPage() {
        _uiState.update { it.copy(page = 1) }
        loadPage(append = false)
    }

    fun loadNextPage() {
        _uiState.update { it.copy(page = it.page + 1) }
        loadPage(append = true)
    }

    private fun loadPage(append: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val state = _uiState.value
            vehicleRepository.searchVehicles(
                filters = Optional.absent(),
                paginationAmount = state.pageSize,
                paginationPage = state.page
            ).onSuccess { newItems ->
                val castedItems = newItems.toVehicleCardHomeUiList();
                _uiState.update {
                    it.copy(
                        vehiclesRented = it.vehiclesRented,
                        myVehicles = it.myVehicles,
                        vehiclesToRent = (if (append) it.vehiclesToRent + castedItems else castedItems),
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private fun observeUserBoundData() {
        userBoundJob?.cancel()
        userBoundJob = viewModelScope.launch {
            userRepository.getCurrentUser()
                .filterNotNull()
                .distinctUntilChangedBy { it.id }
                .collectLatest { user ->
                    // Refresh my vehicles once when user changes
                    loadMyVehicles(ownerId = user.id)

                    // Observe rented vehicles continuously
                    observeRentedVehicles(renterId = user.id)
                }
        }
    }

    private suspend fun loadMyVehicles(ownerId: Int) {
        vehicleRepository.getVehiclesByOwnerId(ownerId)
            .onSuccess { vehicles ->
                val my: List<VehicleCardHomeUi> = when {
                    vehicles.isEmpty() -> emptyList()

                    else -> {
                        // Unknown type: don't crash the app; show placeholders
                        vehicles.mapIndexed { index, _ ->
                            VehicleCardHomeUi(
                                vehicleId = -(index + 1), // negative id so it won't collide with real ids
                                imageUrl = "",
                                title = "Unknown vehicle",
                                location = "-",
                                owner = "$ownerId",
                                costPerDay = 0.0
                            )
                        }
                    }
                }

                _uiState.update { it.copy(myVehicles = my) }
            }
            .onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Unknown error") }
            }
    }


    private suspend fun observeRentedVehicles(renterId: Int) {
        reservationRepository.getReservationsByRenterId(renterId)
            .collectLatest { reservations ->
                val cards = coroutineScope {
                    reservations.map { reservation ->
                        async {
                            val vehicle = vehicleRepository
                                .getVehicleById(reservation.vehicleId)
                                .getOrNull()
                                ?.toVehicleCardHomeUi()
                                ?: VehicleCardHomeUi(
                                    vehicleId = reservation.vehicleId,
                                    imageUrl = "",
                                    title = "Unknown vehicle",
                                    location = "-",
                                    owner = "-",
                                    costPerDay = 0.0
                                )

                            reservation.toCardData(vehicle)
                        }
                    }.awaitAll()
                }

                _uiState.update { it.copy(vehiclesRented = cards) }
            }
    }

    fun onCancel() {
        navigator.goBack()
    }

    fun onVehicleSelected(vehicleId: Int) {
        navigator.goTo(CreateReservation(vehicleId))
    }

    fun onReservationSelected(reservationId: Int) {
        navigator.goTo(ReviewReservation(reservationId))
    }

    fun onTrackDrive() {
        navigator.goTo(Drive)
    }
}

private fun ReservationEntity.toCardData(vehicle: VehicleCardHomeUi): ReservationCardData =
    ReservationCardData(
        reservationId = id,
        status = status,
        totalCost = totalCost,
        imageUrl = vehicle.imageUrl,
        title = vehicle.title,
        location = vehicle.location,
        vehicleId = vehicleId,
    )

private fun GetVehicleByIdQuery.GetVehicleById.toVehicleCardHomeUi() = VehicleCardHomeUi(
    vehicleId = requireNotNull(id),
    imageUrl = images.firstOrNull()?.url ?: "",
    title = "$brand $model",
    location = location.address,
    owner = "Owner #${ownerId}",
    costPerDay = costPerDay
)

private fun VehicleCardUi.toVehicleCardHomeUi() = VehicleCardHomeUi(
    vehicleId = vehicleId,
    imageUrl = imageUrl,
    title = title,
    location = location,
    owner = owner,
    costPerDay = costPerDay
)

private fun List<VehicleCardUi>.toVehicleCardHomeUiList(): List<VehicleCardHomeUi> =
    map { it.toVehicleCardHomeUi() }

