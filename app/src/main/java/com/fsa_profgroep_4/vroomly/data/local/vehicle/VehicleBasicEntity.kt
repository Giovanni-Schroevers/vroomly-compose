package com.fsa_profgroep_4.vroomly.data.local.vehicle

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle")
data class VehicleBasicEntity(
    @PrimaryKey
    val id: Int,
    val ownerId: Int,
    val brand: String?,
    val costPerDay: String?,
    val engineType: String?,
    val reviewStars: Double?
)
