package com.fsa_profgroep_4.vroomly.ui.screens.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyDatePickerField
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(viewModel: RegisterViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.screenPadding),
        topBar = { VroomlyBackButton(onBackClicked = { viewModel.goBack() }) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            )  {
                Image(
                    painter = painterResource(R.drawable.logo_small),
                    contentDescription = "Vroomly",
                )

                Text(
                    text = stringResource(R.string.lets_get_started),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.small)
                )
                Text(
                    text = stringResource(R.string.welcome_to_vroomly),
                    fontSize = 18.sp
                )
                Text(
                    text = stringResource(R.string.lets_get_you_started_with_an_account),
                    fontSize = 18.sp
                )
                Text(
                    text = stringResource(R.string.join_the_vroom_where_cars_move_people),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                )

                if (uiState.generalError != null) {
                    Text(
                        text = uiState.generalError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                    )
                }

                VroomlyTextField(
                    value = uiState.firstname.value,
                    onValueChange = { viewModel.onFirstnameChange(it) },
                    label = stringResource(R.string.firstname),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.firstname.error
                )

                VroomlyTextField(
                    value = uiState.middleName.value,
                    onValueChange = { viewModel.onMiddleNameChange(it) },
                    label = stringResource(R.string.middle_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !uiState.isLoading,
                    errorText = uiState.middleName.error
                )

                VroomlyTextField(
                    value = uiState.lastname.value,
                    onValueChange = { viewModel.onLastnameChange(it) },
                    label = stringResource(R.string.lastname),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.lastname.error
                )

                VroomlyDatePickerField(
                    value = uiState.dob.value,
                    onValueChange = { viewModel.onDobChange(it) },
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
                    onValueChange = { viewModel.onEmailChange(it) },
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
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = stringResource(R.string.username),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.extraSmall),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.username.error
                )

                VroomlyTextField(
                    value = uiState.password.value,
                    onValueChange = { viewModel.onPasswordChange(it) },
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
                    onValueChange = { viewModel.onVerifyPasswordChange(it) },
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

                Box(contentAlignment = Alignment.Center) {
                    VroomlyButton(
                        text = stringResource(R.string.register),
                        onClick = { viewModel.register() },
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.screenPadding),
                        enabled = !uiState.isLoading
                    )
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}