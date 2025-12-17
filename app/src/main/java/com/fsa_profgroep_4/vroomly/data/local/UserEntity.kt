package com.fsa_profgroep_4.vroomly.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val email: String,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val username: String?,
    val dateOfBirth: String?,
    val token: String
)
