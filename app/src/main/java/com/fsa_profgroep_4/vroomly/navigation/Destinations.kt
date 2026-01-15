package com.fsa_profgroep_4.vroomly.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Start : NavKey

@Serializable
object Login : NavKey

@Serializable
object Register : NavKey

@Serializable
object Home : NavKey

@Serializable
object Account : NavKey

@Serializable
object AccountEdit : NavKey

@Serializable
object RegisterCar : NavKey

@Serializable
object VehiclesOverview : NavKey

@Serializable
object OwnerCarOverview : NavKey

@Serializable
object ReservationsOverview : NavKey