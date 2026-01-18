package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.CreateReservation
import com.fsa_profgroep_4.vroomly.navigation.ReservationMap
import com.fsa_profgroep_4.vroomly.navigation.ReservationsOverview
import com.fsa_profgroep_4.vroomly.navigation.ReviewReservation
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage.ReservationMapScreen
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage.ReviewReservationScreen
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage.ReviewReservationViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview.ReservationOverviewScreen
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview.ReservationViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.reserve.CreateReservationScreen
import com.fsa_profgroep_4.vroomly.ui.screens.reservation.reserve.CreateReservationViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val reservationModule = module {
    activityRetainedScope {
        viewModel { ReservationViewModel(get(), get(),get(), get()) }
        viewModel { CreateReservationViewModel(get(), get(), get(), get()) }
        viewModel { ReviewReservationViewModel(get(), get(), get()) }

        navigation<ReservationsOverview> { ReservationOverviewScreen(viewModel = get()) }
        navigation<CreateReservation> { key ->
            CreateReservationScreen(
                vehicleId = key.vehicleId,
                viewModel = get()
            )
        }
        navigation<ReviewReservation> { key ->
            ReviewReservationScreen(
                reservationId = key.reservationId,
                viewModel = get()
            )
        }
        navigation<ReservationMap> { key ->
            ReservationMapScreen(
                reservationId = key.reservationId,
                viewModel = get()
            )
        }
    }
}