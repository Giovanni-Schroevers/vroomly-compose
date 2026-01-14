package com.fsa_profgroep_4.vroomly.ui.base

import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.navigation.Account
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Navigator

abstract class BaseViewModel(
    protected val navigator: Navigator
) : ViewModel() {

    open fun onNavigate(route: String) {
        when (route) {
            "home" -> navigator.goTo(Home)
            "account" -> navigator.goTo(Account)
            // @TODO: Add other routes here as they are implemented
            // "search" -> navigator.goTo(Search)
            // "reservations" -> navigator.goTo(Reservations)
        }
    }
}
