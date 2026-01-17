package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.data.drive.DriveRepository
import com.fsa_profgroep_4.vroomly.data.drive.DriveRepositoryImpl
import com.fsa_profgroep_4.vroomly.navigation.Drive
import com.fsa_profgroep_4.vroomly.ui.screens.drive.DriveScreen
import com.fsa_profgroep_4.vroomly.ui.screens.drive.DriveViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val driveModule = module {
    single<DriveRepository> { DriveRepositoryImpl(get()) }
    
    viewModel { DriveViewModel(get(), androidApplication(), get(), get(), get()) }
    activityRetainedScope {
        navigation<Drive> { DriveScreen(get()) }
    }
}
