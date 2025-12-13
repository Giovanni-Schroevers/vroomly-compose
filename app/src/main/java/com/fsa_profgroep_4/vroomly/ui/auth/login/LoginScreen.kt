package com.fsa_profgroep_4.vroomly.ui.auth.login


import androidx.compose.foundation.Image
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.screenPadding),
        topBar = { VroomlyBackButton(onBackClicked = { viewModel.goBack() }) },
        content = { padding ->
            Column(modifier = Modifier.padding(padding))  {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Image(
                    painter = painterResource(R.drawable.logo_small),
                    contentDescription = "Vroomly",
                )

                Text(
                    text = "Let’s sign you in.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.small)
                )
                Text(
                    text = "Welcome back to Vroomly.",
                    fontSize = 18.sp
                )
                Text(
                    text = "Hit the road. Not the hassle.",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                )

                VroomlyTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.small),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    required = true,
                )

                VroomlyTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.medium),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    required = true,
                )

                VroomlyButton(
                    text = stringResource(R.string.login),
                    onClick = {},
                    modifier = Modifier
                )
            }
        }
    )
}
