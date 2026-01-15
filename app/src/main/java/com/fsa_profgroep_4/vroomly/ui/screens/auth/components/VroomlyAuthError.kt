package com.fsa_profgroep_4.vroomly.ui.screens.auth.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun VroomlyAuthError(
    error: String?,
    modifier: Modifier = Modifier
) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier.padding(bottom = MaterialTheme.spacing.small)
        )
    }
}
