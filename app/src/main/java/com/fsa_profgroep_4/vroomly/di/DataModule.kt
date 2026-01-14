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
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf

val dataModule = module {
    single {
        ApolloClient.Builder()
            .serverUrl("https://dzxrnnq4til8g.cloudfront.net/graphql")
            .addCustomScalarAdapter(Date.type, DateAdapter)
            .build()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "vroomly-db"
        ).build()
    }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().vehicleDao() }

    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::VehicleRepositoryImpl) { bind<VehicleRepository>() }
}