package com.fsa_profgroep_4.vroomly.ui.screens.drive.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton

@Composable
fun DriveTrackingContent(
    elapsedTime: String,
    onStopTracking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Drive in Progress",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.we_are_tracking_your_location))
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = elapsedTime,
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        VroomlyButton(
            onClick = onStopTracking,
            text = stringResource(R.string.stop_tracking)
        )
    }
}
