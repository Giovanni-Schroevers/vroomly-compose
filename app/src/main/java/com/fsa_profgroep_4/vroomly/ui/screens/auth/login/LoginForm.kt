package com.fsa_profgroep_4.vroomly.ui.screens.auth.login

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
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyFormButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun LoginForm(
    uiState: LoginUiState.Content,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        VroomlyTextField(
            value = uiState.email.value,
            onValueChange = onEmailChange,
            label = stringResource(R.string.email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.small),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.email.error
        )

        VroomlyTextField(
            value = uiState.password.value,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.medium),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            required = true,
            enabled = !uiState.isLoading,
            errorText = uiState.password.error
        )

        VroomlyFormButton(
            text = stringResource(R.string.login),
            onClick = onLoginClick,
            isLoading = uiState.isLoading,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
