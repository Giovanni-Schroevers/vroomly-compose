package com.fsa_profgroep_4.vroomly.data.vehicle

import android.os.SystemClock
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.AddImageToVehicleQuery
import com.example.rocketreserver.AvailableVehiclesQuery
import com.example.rocketreserver.CreateVehicleQuery
import com.example.rocketreserver.DeleteVehicleMutation
import com.example.rocketreserver.UpdateVehicleMutation
import com.example.rocketreserver.type.VehicleUpdateInput
import com.example.rocketreserver.GetReservationsByVehicleIdQuery
import com.example.rocketreserver.GetVehicleByIdQuery
import com.example.rocketreserver.GetVehiclesByOwnerIdQuery
import com.fsa_profgroep_4.vroomly.data.storage.ImageStorageService
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface VehicleRepository {
    suspend fun searchVehicles(
        filters: Optional<VehicleFilterInput>,
        paginationAmount: Int,
        paginationPage: Int
    ): Result<List<VehicleCardUi>>

    suspend fun getVehiclesByOwnerId(ownerId: Int): Result<List<VehicleCardUi>>

    suspend fun getVehicleById(vehicleId: Int): Result<GetVehicleByIdQuery.GetVehicleById>

    suspend fun getReservationsByVehicleId(vehicleId: Int): Result<List<GetReservationsByVehicleIdQuery.GetReservationsByVehicleId>>

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

    suspend fun deleteVehicleById(vehicleId: Int): Result<DeleteVehicleMutation.DeleteVehicle>

    suspend fun updateVehicle(
        vehicleId: Int,
        licensePlate: String?,
        brand: String?,
        model: String?,
        year: Int?,
        color: String?,
        category: String?,
        engineType: String?,
        seats: Int?,
        costPerDay: Double?,
        odometerKm: Double?,
        motValidTill: String?,
        vin: String?,
        zeroToHundred: Double?,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<UpdateVehicleMutation.UpdateVehicle>

    suspend fun uploadAndAddImageToVehicle(
        vehicleId: Int,
        imageBytes: ByteArray,
        imageNumber: Int?
    ): Result<String>
}

private const val TAG = "VehicleRepository"

class VehicleRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDao: UserDao,
    private val imageStorageService: ImageStorageService
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
                                vehicleId = id,
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
                                vehicleId = id,
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
                            vehicleId = id,
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

            vehicles.mapNotNull { vehicle ->
                val id = vehicle.id ?: return@mapNotNull null
                VehicleCardUi(
                    vehicleId = id,
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

    override suspend fun getVehicleById(vehicleId: Int): Result<GetVehicleByIdQuery.GetVehicleById> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(GetVehicleByIdQuery(vehicleId = vehicleId)).execute()
            }

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown error")
            }

            response.data?.getVehicleById ?: throw Exception("Vehicle not found")
        }.also { r ->
            r.onFailure { e -> Log.e(TAG, "getVehicleById failed", e) }
        }
    }

    override suspend fun getReservationsByVehicleId(vehicleId: Int): Result<List<GetReservationsByVehicleIdQuery.GetReservationsByVehicleId>> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(GetReservationsByVehicleIdQuery(vehicleId = vehicleId)).execute()
            }

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown error")
            }

            response.data?.getReservationsByVehicleId.orEmpty()
        }.also { r ->
            r.onFailure { e -> Log.e(TAG, "getReservationsByVehicleId failed", e) }
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

    override suspend fun deleteVehicleById(vehicleId: Int): Result<DeleteVehicleMutation.DeleteVehicle> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.mutation(DeleteVehicleMutation(vehicleId = vehicleId)).execute()
            }

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown error")
            }

            response.data?.deleteVehicle ?: throw Exception("Failed to delete vehicle")
        }.also { r ->
            r.onFailure { e -> Log.e(TAG, "deleteVehicleById failed", e) }
        }
    }

    override suspend fun updateVehicle(
        vehicleId: Int,
        licensePlate: String?,
        brand: String?,
        model: String?,
        year: Int?,
        color: String?,
        category: String?,
        engineType: String?,
        seats: Int?,
        costPerDay: Double?,
        odometerKm: Double?,
        motValidTill: String?,
        vin: String?,
        zeroToHundred: Double?,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<UpdateVehicleMutation.UpdateVehicle> {
        return runCatching {
            val vehicleCategory = category?.let {
                VehicleCategory.entries.find { c -> c.name == it }
            }
            val vehicleEngineType = engineType?.let {
                EngineType.entries.find { e -> e.name == it }
            }

            val locationInput = if (address != null && latitude != null && longitude != null) {
                Optional.present(VehicleLocationInput(
                    address = address,
                    latitude = latitude,
                    longitude = longitude
                ))
            } else {
                Optional.absent()
            }

            val vehicleUpdateInput = VehicleUpdateInput(
                id = vehicleId,
                licensePlate = Optional.presentIfNotNull(licensePlate),
                brand = Optional.presentIfNotNull(brand),
                model = Optional.presentIfNotNull(model),
                year = Optional.presentIfNotNull(year),
                color = Optional.presentIfNotNull(color),
                category = Optional.presentIfNotNull(vehicleCategory),
                engineType = Optional.presentIfNotNull(vehicleEngineType),
                seats = Optional.presentIfNotNull(seats),
                costPerDay = Optional.presentIfNotNull(costPerDay),
                odometerKm = Optional.presentIfNotNull(odometerKm),
                motValidTill = Optional.presentIfNotNull(motValidTill),
                vin = Optional.presentIfNotNull(vin),
                zeroToHundred = Optional.presentIfNotNull(zeroToHundred),
                location = locationInput
            )

            val response = withContext(Dispatchers.IO) {
                apolloClient.mutation(UpdateVehicleMutation(vehicle = vehicleUpdateInput)).execute()
            }

            if (response.hasErrors()) {
                val errorMsg = response.errors?.first()?.message ?: "Unknown error updating vehicle"
                throw Exception(errorMsg)
            }

            response.data?.updateVehicle ?: throw Exception("Vehicle data is null")
        }.also { result ->
            result.onFailure { e ->
                Log.e(TAG, "updateVehicle() failed with exception", e)
            }
        }
    }

    override suspend fun uploadAndAddImageToVehicle(
        vehicleId: Int,
        imageBytes: ByteArray,
        imageNumber: Int?
    ): Result<String> {
        Log.d(TAG, "uploadAndAddImageToVehicle: vehicleId=$vehicleId, bytes=${imageBytes.size}, number=$imageNumber")
        return runCatching {
            // Upload image to Supabase Storage
            Log.d(TAG, "Starting Supabase upload...")
            val uploadResult = imageStorageService.uploadVehicleImage(vehicleId, imageBytes)
            val imageUrl = uploadResult.getOrThrow()
            Log.d(TAG, "Supabase upload successful: $imageUrl")

            // Add image URL to vehicle via GraphQL
            Log.d(TAG, "Adding image to vehicle via GraphQL...")
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(
                    AddImageToVehicleQuery(
                        vehicleId = vehicleId,
                        imageUrl = imageUrl,
                        number = Optional.presentIfNotNull(imageNumber)
                    )
                ).execute()
            }

            if (response.hasErrors()) {
                val errorMsg = response.errors?.first()?.message ?: "Unknown error adding image"
                Log.e(TAG, "GraphQL error: $errorMsg")
                throw Exception(errorMsg)
            }

            Log.d(TAG, "Image added to vehicle successfully")
            imageUrl
        }.also { result ->
            result.onFailure { e ->
                Log.e(TAG, "uploadAndAddImageToVehicle() failed with exception", e)
            }
        }
    }
}