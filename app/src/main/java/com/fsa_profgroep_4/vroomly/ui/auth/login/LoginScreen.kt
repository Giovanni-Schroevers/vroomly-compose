package com.fsa_profgroep_4.vroomly.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.screenPadding),
        topBar = { VroomlyBackButton(onBackClicked = { viewModel.goBack() }) },
        content = { padding ->
            Column(modifier = Modifier.padding(padding))  {
                Image(
                    painter = painterResource(R.drawable.logo_small),
                    contentDescription = "Vroomly",
                )

                Text(
                    text = "Let’s sign you in.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.small)
                )
                Text(
                    text = "Welcome back to Vroomly.",
                    fontSize = 18.sp
                )
                Text(
                    text = "Hit the road. Not the hassle.",
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
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = "Email",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.small),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.emailError
                )

                VroomlyTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = "Password",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.medium),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    required = true,
                    enabled = !uiState.isLoading,
                    errorText = uiState.passwordError
                )

                Box(contentAlignment = Alignment.Center) {
                    VroomlyButton(
                        text = stringResource(R.string.login),
                        onClick = { viewModel.login() },
                        modifier = Modifier,
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
