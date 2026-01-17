package com.fsa_profgroep_4.vroomly.di

import androidx.room.Room
import com.apollographql.apollo.ApolloClient
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepository
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepositoryImpl
import com.fsa_profgroep_4.vroomly.data.local.AppDatabase
import com.fsa_profgroep_4.vroomly.data.adapter.DateAdapter
import com.example.rocketreserver.type.Date
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepositoryImpl
import com.fsa_profgroep_4.vroomly.data.network.AuthorizationInterceptor
import com.fsa_profgroep_4.vroomly.data.network.ApolloAuthorizationInterceptor
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepositoryImpl
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.data.user.UserRepositoryImpl
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepository
import com.fsa_profgroep_4.vroomly.data.reservation.ReservationRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf

val dataModule = module {
    single {
        val userDao: UserDao = get()
        ApolloClient.Builder()
            .serverUrl("https://dzxrnnq4til8g.cloudfront.net/graphql")
            .addCustomScalarAdapter(Date.type, DateAdapter)
            .addHttpInterceptor(AuthorizationInterceptor(userDao))
            .addInterceptor(ApolloAuthorizationInterceptor(userDao))
            .build()
    }

    single {
        Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "vroomly-db"
            ).fallbackToDestructiveMigration(false).build()
    }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().reservationDao() }

    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::VehicleRepositoryImpl) { bind<VehicleRepository>() }
    singleOf(::ReservationRepositoryImpl) { bind<ReservationRepository>() }
}