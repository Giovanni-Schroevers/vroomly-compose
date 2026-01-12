package com.fsa_profgroep_4.vroomly.ui.screens.home

import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.RegisterCar
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel

class HomeViewModel(
    // temporary button logic to move to car register
    private val navigator: Navigator
) : BaseViewModel(navigator) {

    fun onRegisterCarClicked() {
        navigator.goTo(RegisterCar)
    }
}

