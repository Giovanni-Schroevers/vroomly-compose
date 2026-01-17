package com.fsa_profgroep_4.vroomly.ui.screens.drive.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R

@Composable
fun DrivePermissionContent(
    showRationale: Boolean,
    onOpenSettings: () -> Unit,
    onGrantPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showRationale) {
            Text(
                text = stringResource(R.string.drive_tracking_requires_location_permission_to_work_accurately_in_the_background),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onOpenSettings) {
                Text(stringResource(R.string.open_settings))
            }
        } else {
            Button(onClick = onGrantPermission) {
                Text(stringResource(R.string.grant_location_permission))
            }
        }
    }
}
