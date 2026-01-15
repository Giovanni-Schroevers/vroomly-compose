package com.fsa_profgroep_4.vroomly.data.reservation

import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.CreateReservationQuery
import com.example.rocketreserver.DeleteReservationQuery
import com.example.rocketreserver.GetReservationQuery
import com.example.rocketreserver.GetReservationsByRenterIdQuery
import com.example.rocketreserver.GetReservationsByVehicleIdQuery
import com.example.rocketreserver.UpdateReservationQuery
import com.example.rocketreserver.type.ReservationUpdateInput
import com.fsa_profgroep_4.vroomly.data.local.ReservationDao
import com.fsa_profgroep_4.vroomly.data.local.ReservationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface ReservationRepository {
    fun getReservation(reservationId: Int): Flow<ReservationEntity?>
    fun getReservationsByVehicleId(vehicleId: Int): Flow<List<ReservationEntity>>
    fun getReservationsByRenterId(renterId: Int): Flow<List<ReservationEntity>>
    suspend fun createReservation(
        vehicleId: Int,
        renterId: Int,
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate
    ): Result<ReservationEntity>
    suspend fun updateReservation(input: ReservationUpdateInput): Result<ReservationEntity>
    suspend fun deleteReservation(reservationId: Int): Result<ReservationEntity>
}

class ReservationRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val reservationDao: ReservationDao
) : ReservationRepository {

    override suspend fun createReservation(
        vehicleId: Int,
        renterId: Int,
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate
    ): Result<ReservationEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apolloClient.query(
                CreateReservationQuery(
                    vehicleId = vehicleId,
                    renterId = renterId,
                    startDate = startDate,
                    endDate = endDate
                )
            ).execute()

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown createReservation error")
            }

            val r = response.data?.createReservation
                ?: throw Exception("createReservation returned null data")

            val entity = r.toEntity()
            reservationDao.upsert(entity)
            entity
        }
    }

    override fun getReservation(reservationId: Int): Flow<ReservationEntity?> {
        return reservationDao.observeById(reservationId)
            .onStart {
                // cache-first: if missing locally, fetch once
                val local = withContext(Dispatchers.IO) { reservationDao.getById(reservationId) }
                if (local == null) {
                    withContext(Dispatchers.IO) {
                        val response = apolloClient
                            .query(GetReservationQuery(reservationId = reservationId))
                            .execute()

                        if (response.hasErrors()) return@withContext

                        val remote = response.data?.getReservation ?: return@withContext
                        reservationDao.upsert(remote.toEntity())
                    }
                }
            }
    }

    override fun getReservationsByVehicleId(vehicleId: Int): Flow<List<ReservationEntity>> {
        return reservationDao.observeByVehicleId(vehicleId)
            .onStart {
                withContext(Dispatchers.IO){
                    val response = apolloClient
                        .query(GetReservationsByVehicleIdQuery(vehicleId = vehicleId))
                        .execute()

                    if (response.hasErrors()) return@withContext

                    val entities = response.data?.getReservationsByVehicleId
                        ?.map { it.toEntity() }
                        ?: emptyList()

                    reservationDao.upsertAll(entities)
                }
            }
    }

    override fun getReservationsByRenterId(renterId: Int): Flow<List<ReservationEntity>> {
        return reservationDao.observeByRenterId(renterId)
            .onStart {
                withContext(Dispatchers.IO) {
                    val response = apolloClient
                        .query(GetReservationsByRenterIdQuery(renterId = renterId))
                        .execute()

                    if (response.hasErrors()) return@withContext

                    val entities = response.data?.getReservationsByRenterId
                        ?.map { it.toEntity() }
                        ?: emptyList()

                    reservationDao.upsertAll(entities)
                }
            }
    }

    override suspend fun updateReservation(
        input: ReservationUpdateInput
    ): Result<ReservationEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apolloClient.query(
                UpdateReservationQuery(input = input)
            ).execute()

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown updateReservation error")
            }

            val r = response.data?.updateReservation
                ?: throw Exception("updateReservation returned null data")

            val entity = r.toEntity()
            reservationDao.upsert(entity)
            entity
        }
    }

    override suspend fun deleteReservation(
        reservationId: Int
    ): Result<ReservationEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apolloClient.query(
                DeleteReservationQuery(reservationId = reservationId)
            ).execute()

            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown deleteReservation error")
            }

            val r = response.data?.deleteReservation
                ?: throw Exception("deleteReservation returned null data")

            // Keep local cache in sync:
            reservationDao.deleteById(reservationId)

            r.toEntity()
        }
    }
}

private fun CreateReservationQuery.CreateReservation.toEntity() = ReservationEntity(
    id = requireNotNull(id) { "Reservation id is null (server didn't return an id)" },
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    status = status.name,
    totalCost = totalCost,
    paid = paid,
    createdAt = createdAt.toString(),
    vehicleId = vehicleId,
    renterId = renterId
)

private fun GetReservationQuery.GetReservation.toEntity() = ReservationEntity(
    id = requireNotNull(id) { "Reservation id is null (server didn't return an id)" },
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    status = status.name,
    totalCost = totalCost,
    paid = paid,
    createdAt = createdAt.toString(),
    vehicleId = vehicleId,
    renterId = renterId
)

private fun GetReservationsByVehicleIdQuery.GetReservationsByVehicleId.toEntity() = ReservationEntity(
    id = requireNotNull(id) { "Reservation id is null (server didn't return an id)" },
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    status = status.name,
    totalCost = totalCost,
    paid = paid,
    createdAt = createdAt.toString(),
    vehicleId = vehicleId,
    renterId = renterId
)

private fun GetReservationsByRenterIdQuery.GetReservationsByRenterId.toEntity() = ReservationEntity(
    id = requireNotNull(id) { "Reservation id is null (server didn't return an id)" },
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    status = status.name,
    totalCost = totalCost,
    paid = paid,
    createdAt = createdAt.toString(),
    vehicleId = vehicleId,
    renterId = renterId
)

private fun UpdateReservationQuery.UpdateReservation.toEntity() = ReservationEntity(
    id = requireNotNull(id) { "Reservation id is null (server didn't return an id)" },
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    status = status.name,
    totalCost = totalCost,
    paid = paid,
    createdAt = createdAt.toString(),
    vehicleId = vehicleId,
    renterId = renterId
)

private fun DeleteReservationQuery.DeleteReservation.toEntity() = ReservationEntity(
    id = requireNotNull(id) { "Reservation id is null (server didn't return an id)" },
    startDate = startDate.toString(),
    endDate = endDate.toString(),
    status = status.name,
    totalCost = totalCost,
    paid = paid,
    createdAt = createdAt.toString(),
    vehicleId = vehicleId,
    renterId = renterId
)
