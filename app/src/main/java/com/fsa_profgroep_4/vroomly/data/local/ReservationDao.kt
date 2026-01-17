package com.fsa_profgroep_4.vroomly.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reservation: ReservationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reservations: List<ReservationEntity>)

    @Query("SELECT * FROM reservations WHERE id = :reservationId LIMIT 1")
    fun observeById(reservationId: Int): Flow<ReservationEntity?>

    @Query("SELECT * FROM reservations WHERE id = :reservationId LIMIT 1")
    suspend fun getById(reservationId: Int): ReservationEntity?

    @Query("SELECT * FROM reservations WHERE vehicleId = :vehicleId ORDER BY startDate ASC")
    fun observeByVehicleId(vehicleId: Int): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE renterId = :renterId ORDER BY startDate ASC")
    fun observeByRenterId(renterId: Int): Flow<List<ReservationEntity>>

    @Query("DELETE FROM reservations WHERE id = :reservationId")
    suspend fun deleteById(reservationId: Int)

    @Query("DELETE FROM reservations")
    suspend fun clearTable()
}