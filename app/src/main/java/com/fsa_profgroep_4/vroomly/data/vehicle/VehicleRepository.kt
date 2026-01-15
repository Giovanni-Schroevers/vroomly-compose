package com.fsa_profgroep_4.vroomly.data.vehicle

import android.os.SystemClock
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.AvailableVehiclesQuery
import com.example.rocketreserver.CreateVehicleQuery
import com.example.rocketreserver.GetVehicleByIdQuery
import com.example.rocketreserver.GetVehiclesByOwnerIdQuery
import com.example.rocketreserver.type.EngineType
import com.example.rocketreserver.type.VehicleCategory
import com.example.rocketreserver.type.VehicleFilterInput
import com.example.rocketreserver.type.VehicleInput
import com.example.rocketreserver.type.VehicleLocationInput
import com.example.rocketreserver.type.VehicleStatus
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

interface VehicleRepository {
    suspend fun searchVehicles(
        filters: Optional<VehicleFilterInput>,
        paginationAmount: Int,
        paginationPage: Int
    ): Result<List<VehicleCardUi>>

    suspend fun getVehiclesByOwnerId(ownerId: Int): Result<List<VehicleCardUi>>

    suspend fun getVehicleById(vehicleId: Int): Result<VehicleCardUi>

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

private const val TAG = "VehicleRepository"

class VehicleRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDao: UserDao
) : VehicleRepository {

    private val imageUrlCache = mutableMapOf<Int, String>()
    private val detailsSemaphore = Semaphore(4)

    override suspend fun searchVehicles(
        filters: Optional<VehicleFilterInput>,
        paginationAmount: Int,
        paginationPage: Int
    ): Result<List<VehicleCardUi>> {
        return runCatching {
            val totalT0 = SystemClock.elapsedRealtime()
            Log.d(TAG, "searchVehicles(page=$paginationPage, size=$paginationAmount) START")

            val responseMs: Long
            val response = withContext(Dispatchers.IO) {
                var tmp: Any? = null
                responseMs = measureTimeMillis {
                    tmp = apolloClient.query(
                        AvailableVehiclesQuery(
                            filters = filters,
                            paginationAmount = paginationAmount,
                            paginationPage = paginationPage
                        )
                    ).execute()
                }
                @Suppress("UNCHECKED_CAST")
                tmp as com.apollographql.apollo.api.ApolloResponse<AvailableVehiclesQuery.Data>
            }

            Log.d(TAG, "AvailableVehiclesQuery took ${responseMs}ms")

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown vehicle error")
            }

            val basics = response.data?.searchVehicles.orEmpty()
            Log.d(TAG, "searchVehicles basics=${basics.size}")

            val prefetchIds = basics.take(4).mapNotNull { it.id }.toSet()

            val result = coroutineScope {
                basics.map { vehicle ->
                    async(Dispatchers.IO) {
                        val id = vehicle.id ?: return@async null

                        // Always use cache if present even when it's "error"
                        imageUrlCache[id]?.let { cached ->
                            return@async VehicleCardUi(
                                imageUrl = cached,
                                title = vehicle.brand,
                                location = "-",
                                owner = "Owner #${vehicle.ownerId}",
                                tagText = vehicle.engineType.name,
                                badgeText = vehicle.reviewStars.toString(),
                                costPerDay = vehicle.costPerDay
                            )
                        }

                        // If not in prefetch set, don't call details yet (fast first paint)
                        if (id !in prefetchIds) {
                            val placeholder = "error"
                            imageUrlCache[id] = placeholder // cache miss as well
                            return@async VehicleCardUi(
                                imageUrl = placeholder,
                                title = vehicle.brand,
                                location = "-",
                                owner = "Owner #${vehicle.ownerId}",
                                tagText = vehicle.engineType.name,
                                badgeText = vehicle.reviewStars.toString(),
                                costPerDay = vehicle.costPerDay
                            )
                        }

                        val (details, detailMs) = detailsSemaphore.withPermit {
                            var d: GetVehicleByIdQuery.GetVehicleById? = null
                            val ms = measureTimeMillis {
                                d = apolloClient.query(GetVehicleByIdQuery(vehicleId = id))
                                    .execute()
                                    .data
                                    ?.getVehicleById
                            }
                            d to ms
                        }
                        Log.d(TAG, "GetVehicleById($id) took ${detailMs}ms")

                        val imageUrl = details?.images
                            ?.filter { it.url.isNotBlank() }
                            ?.minByOrNull { it.number }
                            ?.url
                            ?: "error"

                        // Cache even when it's "error" to avoid re-fetching
                        imageUrlCache[id] = imageUrl

                        VehicleCardUi(
                            imageUrl = imageUrl,
                            title = vehicle.brand,
                            location = details?.location?.address ?: "-",
                            owner = "Owner #${vehicle.ownerId}",
                            tagText = vehicle.engineType.name,
                            badgeText = vehicle.reviewStars.toString(),
                            costPerDay = vehicle.costPerDay
                        )
                    }
                }.awaitAll().filterNotNull()
            }

            val totalMs = SystemClock.elapsedRealtime() - totalT0
            Log.d(
                TAG,
                "searchVehicles DONE items=${result.size} total=${totalMs}ms cacheSize=${imageUrlCache.size}"
            )

            result
        }.also { r ->
            r.onFailure { e -> Log.e(TAG, "searchVehicles failed", e) }
        }

    }

    override suspend fun getVehiclesByOwnerId(ownerId: Int): Result<List<VehicleCardUi>> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(GetVehiclesByOwnerIdQuery(ownerId = ownerId)).execute()
            }

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown error")
            }

            val vehicles = response.data?.getVehiclesByOwnerId.orEmpty()

            vehicles.map { vehicle ->
                VehicleCardUi(
                    imageUrl = "error",
                    title = "${vehicle.brand} ${vehicle.model}",
                    location = vehicle.location?.address ?: "-",
                    owner = "Owner #${vehicle.ownerId}",
                    tagText = vehicle.engineType.name,
                    badgeText = vehicle.reviewStars.toString(),
                    costPerDay = vehicle.costPerDay
                )
            }
        }.also { r ->
            r.onFailure { e -> Log.e(TAG, "getVehiclesByOwnerId failed", e) }
        }
    }

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

    override suspend fun getVehicleById(vehicleId: Int): Result<VehicleCardUi> {
        return runCatching {
        val response = withContext(Dispatchers.IO) {
            apolloClient.query(GetVehicleByIdQuery(vehicleId = vehicleId)).execute()
        }

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown error")
        }

        val vehicle = response.data?.getVehicleById

         VehicleCardUi(
            imageUrl = "error",
            title = "${vehicle?.brand} ${vehicle?.model}",
            location = vehicle?.location?.address ?: "-",
            owner = "Owner #${vehicle?.ownerId}",
            tagText = vehicle?.engineType?.name ?: "",
            badgeText = vehicle?.reviewStars.toString(),
            costPerDay = vehicle?.costPerDay ?: 0.0
        )
        }.also { r ->
            r.onFailure { e -> Log.e(TAG, "getVehicleById failed", e) }
        }
    }
}