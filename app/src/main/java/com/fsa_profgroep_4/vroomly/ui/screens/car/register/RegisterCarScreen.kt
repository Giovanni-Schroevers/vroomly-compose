package com.fsa_profgroep_4.vroomly.ui.screens.car.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun RegisterCarScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterCarViewModel
) {
    var licensePlate by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.screenPadding)
    ) {
        VroomlyBackButton(onBackClicked = { viewModel.onBackClicked() })
        Text(
            text = stringResource(R.string.register_car_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        VroomlyTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            label = stringResource(R.string.license_plate),
            modifier = Modifier.fillMaxWidth()
        )
        VroomlyButton(
            text = stringResource(R.string.add_pictures),
            onClick = { }
        )
        VroomlyButton(
            text = stringResource(R.string.save),
            onClick = {  }
        )
    }
}
