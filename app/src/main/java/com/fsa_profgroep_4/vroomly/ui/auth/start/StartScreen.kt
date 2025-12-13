package com.fsa_profgroep_4.vroomly.ui.auth.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartScreen(viewModel: StartViewModel = koinViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
        Text(
            stringResource(R.string.welcome_to_vroomly),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            stringResource(R.string.slogan),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
        VroomlyButton(
            text = stringResource(R.string.login),
            onClick = { viewModel.onLoginClicked() },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )
        VroomlyButton(
            text = stringResource(R.string.register),
            onClick = {},
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        )
    }
}
