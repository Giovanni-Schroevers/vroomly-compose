package com.fsa_profgroep_4.vroomly.data.local

import androidx.room.Dao
import androidx.room.Query
import com.fsa_profgroep_4.vroomly.data.local.vehicle.VehicleBasicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle")
    fun getVehicles(): Flow<VehicleBasicEntity?>
}