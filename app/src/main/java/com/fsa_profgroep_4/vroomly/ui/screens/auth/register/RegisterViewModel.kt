package com.fsa_profgroep_4.vroomly.ui.screens.auth.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepository
import com.fsa_profgroep_4.vroomly.data.auth.ValidationException
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.models.FormField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

sealed class RegisterUiState {
    data class Content(
        val firstname: FormField = FormField(),
        val middleName: FormField = FormField(),
        val lastname: FormField = FormField(),
        val dob: FormField = FormField(),
        val email: FormField = FormField(),
        val username: FormField = FormField(),
        val password: FormField = FormField(),
        val passwordVerify: FormField = FormField(),
        val isLoading: Boolean = false,
        val generalError: String? = null
    ) : RegisterUiState() {
        fun hasErrors() = listOf(firstname, middleName, lastname, dob, email, username, password, passwordVerify)
            .any { it.error != null }
    }
}
class RegisterViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthRepository,
    private val application: Application
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState.Content())
    val uiState: StateFlow<RegisterUiState.Content> = _uiState.asStateFlow()
    fun onFirstnameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstname = _uiState.value.firstname.copy(value = value, error = null))
    }

    fun onMiddleNameChange(value: String) {
        _uiState.value = _uiState.value.copy(middleName = _uiState.value.middleName.copy(value = value, error = null))
    }

    fun onLastnameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastname = _uiState.value.lastname.copy(value = value, error = null))
    }

    fun onDobChange(value: String) {
        _uiState.value = _uiState.value.copy(dob = _uiState.value.dob.copy(value = value, error = null))
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = _uiState.value.email.copy(value = value, error = null))
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = _uiState.value.username.copy(value = value, error = null))
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = _uiState.value.password.copy(value = value, error = null))
    }

    fun onVerifyPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(passwordVerify = _uiState.value.passwordVerify.copy(value = value, error = null))
    }

    fun register() {
        val currentState = _uiState.value

        val validatedState = currentState.copy(
            firstname = currentState.firstname.validateRequired(application.getString(R.string.firstname_is_required)),
            lastname = currentState.lastname.validateRequired(application.getString(R.string.lastname_is_required)),
            dob = currentState.dob.validateRequired(application.getString(R.string.dob_is_required)),
            email = currentState.email.validateRequired(application.getString(R.string.email_is_required)),
            username = currentState.username.validateRequired(application.getString(R.string.username_is_required)),
            password = currentState.password.validateRequired(application.getString(R.string.password_is_required)),
            passwordVerify = when {
                currentState.passwordVerify.value.isBlank() -> 
                    currentState.passwordVerify.copy(error = application.getString(R.string.password_is_required))
                currentState.passwordVerify.value != currentState.password.value -> 
                    currentState.passwordVerify.copy(error = application.getString(R.string.passwords_do_not_match))
                else -> currentState.passwordVerify.copy(error = null)
            }
        )

        _uiState.value = validatedState
        if (validatedState.hasErrors()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalError = null)
            
            val result = authRepository.register(
                firstName = validatedState.firstname.value,
                middleName = validatedState.middleName.value,
                lastName = validatedState.lastname.value,
                email = validatedState.email.value,
                userName = validatedState.username.value,
                dob = LocalDate.parse(validatedState.dob.value),
                password = validatedState.password.value
            )

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                navigator.resetTo(Home)
            }.onFailure { error ->
                if (error is ValidationException) {
                    val currentState = _uiState.value
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        firstname = error.fieldErrors["firstname"]?.let {
                            currentState.firstname.copy(error = it)
                        } ?: currentState.firstname,
                        lastname = error.fieldErrors["lastname"]?.let {
                            currentState.lastname.copy(error = it)
                        } ?: currentState.lastname,
                        dob = error.fieldErrors["dob"]?.let {
                            currentState.dob.copy(error = it)
                        } ?: currentState.dob,
                        email = error.fieldErrors["email"]?.let {
                            currentState.email.copy(error = it)
                        } ?: currentState.email,
                        username = error.fieldErrors["userName"]?.let {
                            currentState.username.copy(error = it)
                        } ?: currentState.username,
                        password = error.fieldErrors["password"]?.let {
                            currentState.password.copy(error = it)
                        } ?: currentState.password,
                        generalError = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = error.message?.split(" : ")?.last() 
                            ?: application.getString(R.string.registration_failed)
                    )
                }
            }
        }
    }

    fun goBack() {
        navigator.goBack()
    }
}