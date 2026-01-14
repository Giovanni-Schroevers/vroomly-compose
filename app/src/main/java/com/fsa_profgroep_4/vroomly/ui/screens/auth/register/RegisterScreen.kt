package com.fsa_profgroep_4.vroomly.ui.screens.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.screens.auth.components.VroomlyAuthError
import com.fsa_profgroep_4.vroomly.ui.screens.auth.components.VroomlyAuthHeader
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
                VroomlyAuthHeader(
                    title = stringResource(R.string.lets_get_started),
                    subtitles = listOf(
                        stringResource(R.string.welcome_to_vroomly),
                        stringResource(R.string.lets_get_you_started_with_an_account),
                        stringResource(R.string.join_the_vroom_where_cars_move_people)
                    )
                )

                VroomlyAuthError(error = uiState.generalError)

                RegisterForm(
                    uiState = uiState,
                    onFirstnameChange = viewModel::onFirstnameChange,
                    onMiddleNameChange = viewModel::onMiddleNameChange,
                    onLastnameChange = viewModel::onLastnameChange,
                    onDobChange = viewModel::onDobChange,
                    onEmailChange = viewModel::onEmailChange,
                    onUsernameChange = viewModel::onUsernameChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onVerifyPasswordChange = viewModel::onVerifyPasswordChange,
                    onRegisterClick = viewModel::register
                )
            }
        }
    )
}