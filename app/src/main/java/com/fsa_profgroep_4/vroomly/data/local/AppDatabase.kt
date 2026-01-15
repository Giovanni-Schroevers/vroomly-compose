package com.fsa_profgroep_4.vroomly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fsa_profgroep_4.vroomly.data.local.vehicle.VehicleBasicEntity

@Database(entities = [UserEntity::class, VehicleBasicEntity::class, ReservationEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun reservationDao(): ReservationDao
}
