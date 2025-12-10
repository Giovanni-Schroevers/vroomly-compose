package com.fsa_profgroep_4.vroomly.ui.loginScreen

import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.di.Navigator

class LoginViewModel(private val navigator: Navigator) : ViewModel() {
    fun goBack() {
        navigator.goBack()
    }
}