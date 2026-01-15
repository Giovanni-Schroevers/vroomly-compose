package com.fsa_profgroep_4.vroomly.ui.screens.account

import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.local.UserEntity
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.navigation.AccountEdit
import com.fsa_profgroep_4.vroomly.navigation.RegisterCar
import com.fsa_profgroep_4.vroomly.navigation.Start
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AccountUiState {
    object Loading : AccountUiState
    data class Success(val user: UserEntity) : AccountUiState
}

class AccountViewModel(
    private val userRepository: UserRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    val uiState: StateFlow<AccountUiState> = userRepository.getCurrentUser()
        .map { user ->
            if (user != null) {
                AccountUiState.Success(user)
            } else {
                AccountUiState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountUiState.Loading
        )

    fun onLogout() {
        viewModelScope.launch {
            userRepository.logout()
            navigator.resetTo(Start)
        }
    }

    fun onDeleteAccount() {
        viewModelScope.launch {
            userRepository.deleteAccount()
            navigator.resetTo(Start)
        }
    }

    fun onEditProfile() {
        navigator.goTo(AccountEdit)
    }

    fun onManageCars() {
        navigator.goTo(RegisterCar)
    }
}
