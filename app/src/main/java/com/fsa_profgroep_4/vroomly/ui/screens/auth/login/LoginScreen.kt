package com.fsa_profgroep_4.vroomly.ui.screens.auth.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.collectAsState
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.screens.auth.components.VroomlyAuthError
import com.fsa_profgroep_4.vroomly.ui.screens.auth.components.VroomlyAuthHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
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
                    .padding(bottom = MaterialTheme.spacing.screenPadding)
            ) {
                VroomlyAuthHeader(
                    title = stringResource(R.string.let_s_sign_you_in),
                    subtitles = listOf(
                        stringResource(R.string.welcome_back_to_vroomly),
                        stringResource(R.string.hit_the_road_not_the_hassle)
                    )
                )

                VroomlyAuthError(error = uiState.generalError)

                LoginForm(
                    uiState = uiState,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onLoginClick = viewModel::login
                )
            }
        }
    )
}
