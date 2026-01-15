package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.ReservationsOverview
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview.ReservationOverviewScreen
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview.ReservationViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val reservationModule = module {
    activityRetainedScope {
        viewModel { ReservationViewModel(get(), get(),get(), androidApplication()) }

        navigation<ReservationsOverview> { ReservationOverviewScreen(viewModel = get()) }
    }
}