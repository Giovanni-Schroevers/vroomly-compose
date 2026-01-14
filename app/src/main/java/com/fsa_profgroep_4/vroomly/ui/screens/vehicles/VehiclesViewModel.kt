package com.fsa_profgroep_4.vroomly.ui.screens.vehicles

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.type.VehicleFilterInput
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VehiclesUiState(
    val items: List<VehicleCardUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val pageSize: Int = 25,
    val filters: Optional<VehicleFilterInput> = Optional.Absent,
    val hasMore: Boolean = false
)

class VehiclesViewModel(
    private val navigator: Navigator,
    private val vehicleRepository: VehicleRepository,
    private val application: Application
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(VehiclesUiState())
    val uiState: StateFlow<VehiclesUiState> = _uiState.asStateFlow()

    init {
        loadFirstPage()
    }

    fun setFilters(newFilters: Optional<VehicleFilterInput>) {
        _uiState.value = _uiState.value.copy(filters = newFilters, page = 1)
        loadPage(append = false)
    }

    fun loadFirstPage() {
        _uiState.value = _uiState.value.copy(page = 1)
        loadPage(append = false)
    }

    fun loadNextPage() {
        _uiState.value = _uiState.value.copy(page = _uiState.value.page + 1)
        loadPage(append = true)
    }

    private fun loadPage(append: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val state = _uiState.value
            vehicleRepository.searchVehicles(
                filters = state.filters,
                paginationAmount = state.pageSize,
                paginationPage = state.page
            ).onSuccess { newItems ->
                val more = newItems.size == state.pageSize
                _uiState.value = _uiState.value.copy(
                    items = if (append) _uiState.value.items + newItems else newItems,
                    isLoading = false,
                    hasMore = more
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun goBack() = navigator.goBack()
}
