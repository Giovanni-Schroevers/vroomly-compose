package com.fsa_profgroep_4.vroomly.ui.base

import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.navigation.Account
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.ReservationsOverview
import com.fsa_profgroep_4.vroomly.navigation.VehiclesOverview

abstract class BaseViewModel(
    protected open val navigator: Navigator
) : ViewModel() {

    open fun onNavigate(route: String) {
        when (route) {
            "home" -> navigator.goTo(Home)
            "account" -> navigator.goTo(Account)
            "search" -> navigator.goTo(VehiclesOverview)
            "reservation" -> navigator.goTo(ReservationsOverview)
        }
    }
}
