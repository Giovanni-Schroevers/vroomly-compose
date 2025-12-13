package com.fsa_profgroep_4.vroomly.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

@Composable
fun VroomlyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false,
    errorText: String? = null,
    helperText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = buildAnnotatedString {
                    append(label)
                    if (required) {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                            append(" *")
                        }
                    }
                }
            )
        },
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        readOnly = readOnly,
        singleLine = true,
        isError = errorText != null,
        supportingText = {
            if (errorText != null) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (helperText != null) {
                Text(
                    text = helperText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}
