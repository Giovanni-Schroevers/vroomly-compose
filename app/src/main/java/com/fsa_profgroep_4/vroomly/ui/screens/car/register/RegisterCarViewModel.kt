package com.fsa_profgroep_4.vroomly.ui.screens.car.register

import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel

class RegisterCarViewModel(
    private val navigator: Navigator
) : BaseViewModel(navigator) {

    fun onBackClicked() {
        navigator.goBack()
    }
}


