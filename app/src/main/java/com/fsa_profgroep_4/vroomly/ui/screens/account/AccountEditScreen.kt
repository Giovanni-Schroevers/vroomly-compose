package com.fsa_profgroep_4.vroomly.ui.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyDatePickerField
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyFormButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun AccountEditScreen(
    viewModel: AccountEditViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            VroomlyBackButton(
                onBackClicked = { viewModel.onCancel() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = MaterialTheme.spacing.screenPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            VroomlyTextField(
                value = uiState.firstName,
                onValueChange = { viewModel.onFirstNameChange(it) },
                label = stringResource(R.string.firstname),
                modifier = Modifier.fillMaxWidth(),
                required = true,
                errorText = uiState.fieldErrors["firstname"]
            )

            VroomlyTextField(
                value = uiState.middleName,
                onValueChange = { viewModel.onMiddleNameChange(it) },
                label = stringResource(R.string.middle_name),
                modifier = Modifier.fillMaxWidth(),
                errorText = uiState.fieldErrors["middleName"]
            )

            VroomlyTextField(
                value = uiState.lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = stringResource(R.string.lastname),
                modifier = Modifier.fillMaxWidth(),
                required = true,
                errorText = uiState.fieldErrors["lastname"]
            )

            VroomlyTextField(
                value = uiState.email,
                onValueChange = { },
                label = stringResource(R.string.email),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                helperText = "Email cannot be changed"
            )

            VroomlyTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = stringResource(R.string.username),
                modifier = Modifier.fillMaxWidth(),
                required = true,
                errorText = uiState.fieldErrors["username"]
            )

            VroomlyDatePickerField(
                value = uiState.dob?.toString() ?: "",
                onValueChange = { viewModel.onDobChange(it) },
                label = stringResource(R.string.date_of_birth),
                modifier = Modifier.fillMaxWidth(),
                errorText = uiState.fieldErrors["dob"]
            )

            VroomlyFormButton(
                text = stringResource(R.string.save_changes),
                onClick = { viewModel.onSave() },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
