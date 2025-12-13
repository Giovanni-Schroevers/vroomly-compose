package com.fsa_profgroep_4.vroomly.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R

@Composable
fun VroomlyBackButton(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit
) {
    var isEnabled by remember { mutableStateOf(true) }

    IconButton(
        onClick = {
            if (isEnabled) {
                isEnabled = false
                onBackClicked()
            }
        },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.back_button_text),
            modifier = Modifier.size(32.dp)
        )
    }
}
