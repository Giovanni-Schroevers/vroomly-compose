package com.fsa_profgroep_4.vroomly.ui.screens.auth.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepository
import com.fsa_profgroep_4.vroomly.ui.models.FormField
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data class Content(
        val email: FormField = FormField(),
        val password: FormField = FormField(),
        val isLoading: Boolean = false,
        val generalError: String? = null,
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
        _uiState.value = _uiState.value.copy(
            email = _uiState.value.email.copy(value = email, error = null)
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = _uiState.value.password.copy(value = password, error = null)
        )
    }

    fun login() {
        val currentState = _uiState.value

        val validatedState = currentState.copy(
            email = currentState.email.validateRequired(application.getString(R.string.email_is_required)),
            password = currentState.password.validateRequired(application.getString(R.string.password_is_required))
        )

        if (validatedState.email.error != null || validatedState.password.error != null) {
            _uiState.value = validatedState.copy(generalError = null)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                email = _uiState.value.email.copy(error = null),
                password = _uiState.value.password.copy(error = null),
                generalError = null
            )
            val result = authRepository.login(
                validatedState.email.value,
                validatedState.password.value
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                navigator.resetTo(Home)
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
