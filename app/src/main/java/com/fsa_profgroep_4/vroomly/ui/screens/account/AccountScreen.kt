package com.fsa_profgroep_4.vroomly.ui.screens.account

import androidx.compose.foundation.layout.Column
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
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel
) {
    var currentRoute by remember { mutableStateOf("account") }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
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
                Text("Account")
            }
        }
    )
}
