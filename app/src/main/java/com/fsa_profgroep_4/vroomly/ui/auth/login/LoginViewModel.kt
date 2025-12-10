package com.fsa_profgroep_4.vroomly.ui.auth.login

import androidx.lifecycle.ViewModel
import com.fsa_profgroep_4.vroomly.navigation.Navigator

class LoginViewModel(private val navigator: Navigator) : ViewModel() {
    fun goBack() {
        navigator.goBack()
    }
}
