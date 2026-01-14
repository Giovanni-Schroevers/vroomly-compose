package com.fsa_profgroep_4.vroomly.data.vehicle

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.AvailableVehiclesQuery
import com.example.rocketreserver.GetVehicleByIdQuery
import com.example.rocketreserver.type.VehicleFilterInput
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

interface VehicleRepository {
    suspend fun searchVehicles(
        filters: Optional<VehicleFilterInput>,
        paginationAmount: Int,
        paginationPage: Int
    ): Result<List<VehicleCardUi>>
}

private const val TAG = "VehicleRepository"

class VehicleRepositoryImpl(
    private val apolloClient: ApolloClient
) : VehicleRepository {

    // simple in-memory cache (id -> first image url)
    private val imageUrlCache = mutableMapOf<Int, String>()

    // limit parallel detail calls (prevents network thrash)
    private val detailsSemaphore = Semaphore(4)

    override suspend fun searchVehicles(
        filters: Optional<VehicleFilterInput>,
        paginationAmount: Int,
        paginationPage: Int
    ): Result<List<VehicleCardUi>> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(
                    AvailableVehiclesQuery(
                        filters = filters,
                        paginationAmount = paginationAmount,
                        paginationPage = paginationPage
                    )
                ).execute()
            }

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown vehicle error")
            }

            val basics = response.data?.searchVehicles.orEmpty()

            coroutineScope {
                basics.map { vehicle ->
                    async(Dispatchers.IO) {
                        val id = vehicle.id ?: return@async null

                        // fast path: cached image url
                        val cachedUrl = imageUrlCache[id]

                        val details = detailsSemaphore.withPermit {
                            apolloClient.query(GetVehicleByIdQuery(vehicleId = id))
                                .execute()
                                .data
                                ?.getVehicleById
                        }

                        val imageUrl = cachedUrl
                            ?: details?.images
                                ?.filter { it.url.isNotBlank() }
                                ?.minByOrNull { it.number }
                                ?.url
                                ?.also { imageUrlCache[id] = it }
                            ?: "error"

                        VehicleCardUi(
                            imageUrl = imageUrl,
                            title = vehicle.brand,
                            location = details?.location?.address ?: "-",
                            owner = "Owner #${vehicle.ownerId}",
                            tagText = vehicle.engineType.name,
                            badgeText = String.format("%.1f", vehicle.reviewStars),
                            costPerDay = vehicle.costPerDay
                        )
                    }
                }.awaitAll().filterNotNull()
            }
        }.also { result ->
            result.onFailure { e -> Log.e(TAG, "searchVehicles failed", e) }
        }
    }
}
