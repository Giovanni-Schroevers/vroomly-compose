package com.fsa_profgroep_4.vroomly.di

import com.fsa_profgroep_4.vroomly.navigation.Account
import com.fsa_profgroep_4.vroomly.navigation.AccountEdit
import com.fsa_profgroep_4.vroomly.ui.screens.account.AccountEditScreen
import com.fsa_profgroep_4.vroomly.ui.screens.account.AccountEditViewModel
import com.fsa_profgroep_4.vroomly.ui.screens.account.AccountScreen
import com.fsa_profgroep_4.vroomly.ui.screens.account.AccountViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val accountModule = module {
    activityRetainedScope {
        viewModel { AccountViewModel(get(), get()) }
        viewModel { AccountEditViewModel(get(), get()) }

        navigation<Account> { AccountScreen(viewModel = get()) }
        navigation<AccountEdit> { AccountEditScreen(viewModel = get()) }
    }
}
