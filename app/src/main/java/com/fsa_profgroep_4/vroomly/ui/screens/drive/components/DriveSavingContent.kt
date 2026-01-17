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

@Composable
fun DriveSavingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Saving Drive",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.uploading_your_drive_data_to_the_server))
    }
}
