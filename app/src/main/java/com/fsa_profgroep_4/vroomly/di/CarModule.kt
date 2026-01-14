package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.VehiclesOverview
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.VehiclesOverviewScreen
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.VehiclesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val carModule = module {
    activityRetainedScope {
        viewModel { VehiclesViewModel(get(), get(), androidApplication()) }

        navigation<VehiclesOverview> { VehiclesOverviewScreen(viewModel = get()) }
    }
}
