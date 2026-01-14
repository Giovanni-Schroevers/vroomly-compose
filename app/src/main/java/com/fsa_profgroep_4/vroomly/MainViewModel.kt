package com.fsa_profgroep_4.vroomly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import com.fsa_profgroep_4.vroomly.utils.JwtUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            userDao.getCurrentUser().collect { user ->
                _authState.value = when {
                    user == null -> AuthState.Unauthenticated
                    user.token.isBlank() -> AuthState.Unauthenticated
                    JwtUtils.isTokenExpired(user.token) -> {
                        userDao.clearTable()
                        AuthState.Unauthenticated
                    }
                    else -> AuthState.Authenticated
                }
            }
        }
    }
}

sealed class AuthState {
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
}
