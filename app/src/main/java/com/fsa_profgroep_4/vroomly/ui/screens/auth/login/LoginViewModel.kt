package com.fsa_profgroep_4.vroomly.ui.screens.auth.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepository
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data class Content(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val generalError: String? = null,
        val isSuccess: Boolean = false
    ) : LoginUiState()
}

class LoginViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState.Content())
    val uiState: StateFlow<LoginUiState.Content> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun login() {
        val currentState = _uiState.value
        val email = currentState.email
        val password = currentState.password

        val emailError = if (email.isBlank()) application.getString(R.string.email_is_required) else null
        val passwordError = if (password.isBlank()) application.getString(R.string.password_is_required) else null

        if (emailError != null || passwordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError,
                generalError = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, emailError = null, passwordError = null, generalError = null)
            val result = authRepository.login(email, password)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                navigator.goTo(Home)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = error.message?.split(" : ")?.last() ?: application.getString(R.string.login_failed)
                )
            }
        }
    }

    fun goBack() {
        navigator.goBack()
    }
}
