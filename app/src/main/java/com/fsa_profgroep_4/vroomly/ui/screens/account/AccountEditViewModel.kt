package com.fsa_profgroep_4.vroomly.ui.screens.account

import androidx.lifecycle.viewModelScope
import com.fsa_profgroep_4.vroomly.data.auth.ValidationException
import com.fsa_profgroep_4.vroomly.data.user.UserRepository
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate


data class AccountEditUiState(
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = "",
    val dob: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class AccountEditViewModel(
    private val userRepository: UserRepository,
    navigator: Navigator
) : BaseViewModel(navigator) {

    private val _uiState = MutableStateFlow(AccountEditUiState())
    val uiState: StateFlow<AccountEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            firstName = it.firstName,
                            middleName = it.middleName ?: "",
                            lastName = it.lastName,
                            email = it.email,
                            username = it.username,
                            dob = LocalDate.parse(it.dateOfBirth)
                        )
                    }
                }
            }
        }
    }

    fun onFirstNameChange(value: String) = _uiState.update { 
        it.copy(firstName = value, fieldErrors = it.fieldErrors - "firstname") 
    }
    fun onMiddleNameChange(value: String) = _uiState.update { 
        it.copy(middleName = value, fieldErrors = it.fieldErrors - "middleName") 
    }
    fun onLastNameChange(value: String) = _uiState.update { 
        it.copy(lastName = value, fieldErrors = it.fieldErrors - "lastname") 
    }
    fun onUsernameChange(value: String) = _uiState.update { 
        it.copy(username = value, fieldErrors = it.fieldErrors - "username") 
    }
    fun onDobChange(value: LocalDate?) = _uiState.update { 
        it.copy(dob = value, fieldErrors = it.fieldErrors - "dob") 
    }

    fun onSave() {
        if (_uiState.value.isLoading) return
        
        _uiState.update { it.copy(isLoading = true, error = null, fieldErrors = emptyMap()) }
        
        viewModelScope.launch {
            val state = _uiState.value
            val result = userRepository.updateUser(
                firstname = state.firstName,
                middleName = state.middleName.ifBlank { null },
                lastname = state.lastName,
                username = state.username,
                dob = state.dob
            )
            
            result.onSuccess {
                navigator.goBack()
            }.onFailure { e ->
                if (e is ValidationException) {
                    _uiState.update { it.copy(isLoading = false, fieldErrors = e.fieldErrors) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun onCancel() {
        navigator.goBack()
    }
}
