package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Login
import com.fsa_profgroep_4.vroomly.navigation.Start
import com.fsa_profgroep_4.vroomly.ui.screens.home.HomeScreen
import com.fsa_profgroep_4.vroomly.ui.screens.auth.login.LoginScreen
import com.fsa_profgroep_4.vroomly.ui.screens.auth.login.LoginViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.auth.start.StartScreen
import com.fsa_profgroep_4.vroomly.ui.screens.auth.start.StartViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation



@OptIn(KoinExperimentalAPI::class)
val authModule = module {
    activityRetainedScope {
        viewModel { StartViewModel(get()) }
        viewModel { LoginViewModel(get(), get()) }

        navigation<Start> { StartScreen(viewModel = get()) }
        navigation<Login> { LoginScreen(viewModel = get()) }
        navigation<Home> { HomeScreen() }
    }
}
