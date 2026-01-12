package com.fsa_profgroep_4.vroomly.ui.screens.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyDatePickerField
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyFormButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun RegisterForm(
    uiState: RegisterUiState.Content,
    onFirstnameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onDobChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onVerifyPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        VroomlyTextField(
            value = uiState.firstname.value,
            onValueChange = onFirstnameChange,
            label = stringResource(R.string.firstname),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.firstname.error
        )

        VroomlyTextField(
            value = uiState.middleName.value,
            onValueChange = onMiddleNameChange,
            label = stringResource(R.string.middle_name),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            enabled = !uiState.isLoading,
            errorText = uiState.middleName.error
        )

        VroomlyTextField(
            value = uiState.lastname.value,
            onValueChange = onLastnameChange,
            label = stringResource(R.string.lastname),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.lastname.error
        )

        VroomlyDatePickerField(
            value = uiState.dob.value,
            onValueChange = onDobChange,
            label = stringResource(R.string.date_of_birth),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.dob.error
        )

        VroomlyTextField(
            value = uiState.email.value,
            onValueChange = onEmailChange,
            label = stringResource(R.string.email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.email.error
        )

        VroomlyTextField(
            value = uiState.username.value,
            onValueChange = onUsernameChange,
            label = stringResource(R.string.username),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.username.error
        )

        VroomlyTextField(
            value = uiState.password.value,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.password.error
        )

        VroomlyTextField(
            value = uiState.passwordVerify.value,
            onValueChange = onVerifyPasswordChange,
            label = stringResource(R.string.verify_password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.small),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.passwordVerify.error
        )

        VroomlyFormButton(
            text = stringResource(R.string.register),
            onClick = onRegisterClick,
            isLoading = uiState.isLoading,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.screenPadding)
        )
    }
}
