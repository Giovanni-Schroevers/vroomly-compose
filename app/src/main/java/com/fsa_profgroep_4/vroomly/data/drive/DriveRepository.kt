package com.fsa_profgroep_4.vroomly.data.drive

import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.SaveDrivingReportMutation
import com.example.rocketreserver.type.DrivingReportInput
import com.example.rocketreserver.type.LocationSnapshotInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed class DriveState {
    data object Idle : DriveState()
    data class Tracking @OptIn(ExperimentalTime::class) constructor(val startTime: Instant) : DriveState()
    data object Saving : DriveState()
    data object Finished : DriveState()
    data class Saved(val routePoints: List<LocationSnapshotInput>) : DriveState()
}

interface DriveRepository {
    val driveState: StateFlow<DriveState>
    fun startDrive()
    fun stopDrive()
    fun resetDrive()
    fun addSnapshot(snapshot: LocationSnapshotInput)
    fun getSnapshots(): List<LocationSnapshotInput>
    fun updateMaxAcceleration(value: Double)
    fun getMaxAcceleration(): Double
    suspend fun saveDriveReport(): Result<String>
}

class DriveRepositoryImpl(
    private val apolloClient: ApolloClient
) : DriveRepository {
    private val _driveState = MutableStateFlow<DriveState>(DriveState.Idle)
    override val driveState: StateFlow<DriveState> = _driveState.asStateFlow()

    private val locationSnapshots = mutableListOf<LocationSnapshotInput>()
    private var maxAcceleration: Double = 0.0

    override fun updateMaxAcceleration(value: Double) {
        if (_driveState.value is DriveState.Tracking && value > maxAcceleration) {
            maxAcceleration = value
        }
    }

    override fun getMaxAcceleration(): Double = maxAcceleration

    override fun addSnapshot(snapshot: LocationSnapshotInput) {
        if (_driveState.value is DriveState.Tracking) {
            locationSnapshots.add(snapshot)
        }
    }

    override fun getSnapshots(): List<LocationSnapshotInput> {
        return locationSnapshots.toList()
    }

    @OptIn(ExperimentalTime::class)
    override fun startDrive() {
        if (_driveState.value is DriveState.Idle) {
            locationSnapshots.clear()
            maxAcceleration = 0.0
            _driveState.value = DriveState.Tracking(Clock.System.now())
        }
    }

    override fun stopDrive() {
        if (_driveState.value is DriveState.Tracking) {
            _driveState.value = DriveState.Finished
        }
    }

    override fun resetDrive() {
        _driveState.value = DriveState.Idle
        locationSnapshots.clear()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun saveDriveReport(): Result<String> {
        val snapshots = getSnapshots()
        if (snapshots.isEmpty()) return Result.failure(Exception("No location data recorded"))

        _driveState.value = DriveState.Saving

        val reportInput = DrivingReportInput(
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            locationSnapshots = snapshots,
            maxAcceleration = maxAcceleration
        )

        return try {
            val response = apolloClient.mutation(SaveDrivingReportMutation(reportInput)).execute()
            if (response.hasErrors()) {
                _driveState.value = DriveState.Finished // Fallback to finished so user can retry
                Result.failure(Exception(response.errors?.firstOrNull()?.message ?: "Unknown error"))
            } else {
                _driveState.value = DriveState.Saved(snapshots)
                Result.success(response.data?.saveDrivingReport ?: "Success")
            }
        } catch (e: Exception) {
            _driveState.value = DriveState.Finished
            Result.failure(e)
        }
    }
}
