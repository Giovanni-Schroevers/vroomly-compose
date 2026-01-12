package com.fsa_profgroep_4.vroomly.data.vehicle

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.CreateVehicleQuery
import com.example.rocketreserver.type.EngineType
import com.example.rocketreserver.type.VehicleCategory
import com.example.rocketreserver.type.VehicleInput
import com.example.rocketreserver.type.VehicleLocationInput
import com.example.rocketreserver.type.VehicleStatus
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.apollographql.apollo.api.Optional

private const val TAG = "VehicleRepository"

interface VehicleRepository {
    suspend fun createVehicle(
        licensePlate: String,
        brand: String,
        model: String,
        year: Int,
        color: String,
        category: String,
        engineType: String,
        seats: Int,
        costPerDay: Double,
        odometerKm: Double,
        motValidTill: String,
        vin: String,
        zeroToHundred: Double,
        address: String,
        latitude: Double,
        longitude: Double
    ): Result<CreateVehicleQuery.CreateVehicle>
}

class VehicleRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDao: UserDao
) : VehicleRepository {

    override suspend fun createVehicle(
        licensePlate: String,
        brand: String,
        model: String,
        year: Int,
        color: String,
        category: String,
        engineType: String,
        seats: Int,
        costPerDay: Double,
        odometerKm: Double,
        motValidTill: String,
        vin: String,
        zeroToHundred: Double,
        address: String,
        latitude: Double,
        longitude: Double
    ): Result<CreateVehicleQuery.CreateVehicle> {
        return runCatching {
            val currentUser = userDao.getCurrentUser().first()
                ?: throw Exception("User not logged in")

            val vehicleCategory = VehicleCategory.entries.find { it.name == category }
                ?: VehicleCategory.SEDAN
            val vehicleEngineType = EngineType.entries.find { it.name == engineType }
                ?: EngineType.PETROL

            val vehicleInput = VehicleInput(
                licensePlate = licensePlate,
                brand = brand,
                model = model,
                year = year,
                color = color,
                category = vehicleCategory,
                engineType = vehicleEngineType,
                seats = seats,
                costPerDay = costPerDay,
                odometerKm = odometerKm,
                motValidTill = motValidTill,
                vin = vin,
                zeroToHundred = zeroToHundred,
                ownerId = currentUser.id,
                status = VehicleStatus.ACTIVE,
                reviewStars = 0.0,
                location = VehicleLocationInput(
                    address = address,
                    latitude = latitude,
                    longitude = longitude
                ),
                images = emptyList(),
                id = Optional.absent(),
                vehicleModelId = Optional.absent()
            )

            val response = withContext(Dispatchers.IO) {
                apolloClient.query(CreateVehicleQuery(vehicle = vehicleInput)).execute()
            }

            if (response.hasErrors()) {
                val errorMsg = response.errors?.first()?.message ?: "Unknown error creating vehicle"
                throw Exception(errorMsg)
            }

            response.data?.createVehicle ?: throw Exception("Vehicle data is null")
        }.also { result ->
            result.onFailure { e ->
                Log.e(TAG, "createVehicle() failed with exception", e)
            }
        }
    }
}
