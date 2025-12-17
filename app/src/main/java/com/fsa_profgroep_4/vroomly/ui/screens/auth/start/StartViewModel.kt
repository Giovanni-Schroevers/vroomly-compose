package com.fsa_profgroep_4.vroomly.ui.screens.auth.start

import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.Login

class StartViewModel(private val navigator: Navigator) : ViewModel() {
    fun onLoginClicked() {
        navigator.goTo(Login)
    }
}
