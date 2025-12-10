package com.fsa_profgroep_4.vroomly.ui.startScreen

import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.di.Navigator

import com.fsa_profgroep_4.vroomly.di.Login

class StartViewModel(private val navigator: Navigator) : ViewModel() {
    fun onLoginClicked() {
        navigator.goTo(Login)
    }
}
