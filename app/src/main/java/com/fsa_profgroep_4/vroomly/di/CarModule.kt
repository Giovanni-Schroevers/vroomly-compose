package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.OwnerCarDetail
import com.fsa_profgroep_4.vroomly.navigation.OwnerCarOverview
import com.fsa_profgroep_4.vroomly.navigation.VehiclesOverview
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.Overview.VehiclesOverviewScreen
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.Overview.VehiclesViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerDetail.OwnerCarDetailScreen
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerDetail.OwnerCarDetailViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerOverview.OwnerCarOverviewScreen
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.OwnerOverview.OwnerCarOverviewViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val carModule = module {
    activityRetainedScope {
        viewModel { VehiclesViewModel(get(), get(), androidApplication()) }
        viewModel { OwnerCarOverviewViewModel(get(), get(), get()) }
        viewModel { (vehicleId: Int) -> OwnerCarDetailViewModel(get(), get(), vehicleId) }

        navigation<VehiclesOverview> { VehiclesOverviewScreen(viewModel = get()) }
        navigation<OwnerCarOverview> { OwnerCarOverviewScreen(viewModel = get()) }
        navigation<OwnerCarDetail> { destination ->
            OwnerCarDetailScreen(viewModel = get { parametersOf(destination.vehicleId) })
        }
    }
}
