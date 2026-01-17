package com.fsa_profgroep_4.vroomly.ui.screens.drive

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.drive.DriveRepository
import com.fsa_profgroep_4.vroomly.data.drive.DriveState
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.VehiclesOverview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class DriveUiState(
    val hasLocationPermission: Boolean = false,
    val showRationale: Boolean = false,
    val driveState: DriveState = DriveState.Idle,
    val elapsedTime: String = "00:00:00",
    val error: String? = null,
    val isLoading: Boolean = true,
    val hasActiveReservation: Boolean = false
)

class DriveViewModel(
    private val navigator: Navigator,
    private val application: Application,
    private val driveRepository: DriveRepository,
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriveUiState())
    val uiState: StateFlow<DriveUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        checkPermissions()
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    checkActiveReservation(user.id)
                }
            }
        }
        viewModelScope.launch {
            driveRepository.driveState.collect { state ->
                val oldState = _uiState.value.driveState
                _uiState.value = _uiState.value.copy(driveState = state)

                if (state is DriveState.Finished && oldState is DriveState.Tracking && _uiState.value.error == null) {
                    saveDrive()
                }
                
                handleTimer(state)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun checkActiveReservation(userId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = reservationRepository.getReservationsByRenterId(userId)
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val hasActive = result.getOrNull()?.any { reservation ->
            val start = reservation.startDate
            val end = reservation.endDate
            today in start..end
        } ?: false
        
        _uiState.value = _uiState.value.copy(
            hasActiveReservation = hasActive,
            isLoading = false
        )
    }

    @SuppressLint("DefaultLocale")
    @OptIn(ExperimentalTime::class)
    private fun handleTimer(state: DriveState) {
        timerJob?.cancel()
        if (state is DriveState.Tracking) {
            timerJob = viewModelScope.launch {
                while (true) {
                    val duration = Clock.System.now() - state.startTime
                    val hours = duration.inWholeHours
                    val minutes = duration.inWholeMinutes % 60
                    val seconds = duration.inWholeSeconds % 60
                    val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    
                    _uiState.value = _uiState.value.copy(elapsedTime = timeString)
                    delay(1000)
                }
            }
        }
    }

    fun saveDrive() {
        viewModelScope.launch {
            val result = driveRepository.saveDriveReport()
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun checkPermissions() {
        val isGranted = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        _uiState.value = _uiState.value.copy(hasLocationPermission = isGranted)
    }

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = isGranted,
            showRationale = !isGranted
        )
    }

    fun onCancel() {
        driveRepository.resetDrive()
        navigator.goBack()
    }

    fun goHome() {
        driveRepository.resetDrive()
        navigator.resetTo(Home)
    }

    fun goToSearch() {
        navigator.resetTo(VehiclesOverview)
    }
}
