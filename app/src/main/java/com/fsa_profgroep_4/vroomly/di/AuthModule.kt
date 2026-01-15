package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Login
import com.fsa_profgroep_4.vroomly.navigation.Register
import com.fsa_profgroep_4.vroomly.navigation.RegisterCar
import com.fsa_profgroep_4.vroomly.navigation.Start
import com.fsa_profgroep_4.vroomly.ui.screens.auth.login.LoginScreen
import com.fsa_profgroep_4.vroomly.ui.screens.auth.login.LoginViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.auth.register.RegisterScreen
import com.fsa_profgroep_4.vroomly.ui.screens.auth.register.RegisterViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.auth.start.StartScreen
import com.fsa_profgroep_4.vroomly.ui.screens.auth.start.StartViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.register.RegisterCarScreen
import com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.register.RegisterCarViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.home.HomeScreen
import com.fsa_profgroep_4.vroomly.ui.screens.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val authModule = module {
    activityRetainedScope {
        viewModel { StartViewModel(get()) }
        viewModel { LoginViewModel(get(), get(), androidApplication()) }
        viewModel { RegisterViewModel(get(), get(), androidApplication()) }
        viewModel { HomeViewModel(get()) }
        viewModel { RegisterCarViewModel(get(), get(), androidApplication()) }

        navigation<Start> { StartScreen(viewModel = get()) }
        navigation<Login> { LoginScreen(viewModel = get()) }
        navigation<Register> { RegisterScreen(viewModel = get()) }
        navigation<Home> { HomeScreen(viewModel = get()) }
        navigation<RegisterCar> { RegisterCarScreen(viewModel = get()) }
    }
}
