package com.fsa_profgroep_4.vroomly.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val id: Int,
    val startDate: String,
    val endDate: String,
    val status: String,
    val totalCost: Double,
    val paid: Boolean,
    val createdAt: String,
    val vehicleId: Int,
    val renterId: Int
)