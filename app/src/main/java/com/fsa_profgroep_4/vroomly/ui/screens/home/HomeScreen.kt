package com.fsa_profgroep_4.vroomly.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    var currentRoute by remember { mutableStateOf("home") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            VroomlyBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    viewModel.onNavigate(route)
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = MaterialTheme.spacing.screenPadding)
            ) {
                Text("Home")
                VroomlyButton(
                    text = stringResource(R.string.track_your_drive),
                    onClick = { viewModel.onTrackDrive() }
                )
            }
        }
    )
}