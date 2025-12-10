package com.fsa_profgroep_4.vroomly.ui.auth.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { backButton(onBackClicked = { viewModel.goBack() }) }
    ) {
        Text("Login Screen")
    }
}

@Composable
fun backButton(modifier: Modifier = Modifier, onBackClicked: () -> Unit) {
    Button(onClick = onBackClicked) {
        Text("Back")
    }
}
