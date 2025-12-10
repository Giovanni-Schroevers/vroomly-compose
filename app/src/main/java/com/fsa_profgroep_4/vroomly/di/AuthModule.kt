package com.fsa_profgroep_4.vroomly.di

import androidx.navigation3.runtime.NavKey
import com.fsa_profgroep_4.vroomly.ui.loginScreen.LoginScreen
import com.fsa_profgroep_4.vroomly.ui.startScreen.StartScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@Serializable
object Start : NavKey

@Serializable
object Login : NavKey

@OptIn(KoinExperimentalAPI::class)
val authModule = module {
    activityRetainedScope {
        navigation<Start> { StartScreen(viewModel = get()) }
        navigation<Login> { LoginScreen(viewModel = get()) }
    }
}
