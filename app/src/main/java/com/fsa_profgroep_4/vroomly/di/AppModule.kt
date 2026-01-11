package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.MainViewModel
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.navigation.Start
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    includes(dataModule, authModule, accountModule)

    activityRetainedScope {
        scoped {
            Navigator(startDestination = Start)
        }
        viewModel { MainViewModel(get()) }
    }
}
