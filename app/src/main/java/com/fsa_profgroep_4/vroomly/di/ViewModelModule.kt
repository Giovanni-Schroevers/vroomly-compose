package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.ui.loginScreen.LoginViewModel
import com.fsa_profgroep_4.vroomly.ui.startScreen.StartViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    activityRetainedScope {
        viewModel { StartViewModel(get()) }
        viewModel { LoginViewModel(get()) }
    }
}
