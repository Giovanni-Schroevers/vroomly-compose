package com.fsa_profgroep_4.vroomly.di

import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.dsl.module

val appModule = module {
    includes(dataModule, viewModelModule, authModule)

    activityRetainedScope {
        scoped {
            Navigator(startDestination = Start)
        }
    }
}
