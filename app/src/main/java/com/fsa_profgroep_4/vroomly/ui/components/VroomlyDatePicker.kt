package com.fsa_profgroep_4.vroomly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun VroomlyDatePickerDialog(
    initial: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val initialMillis = remember(initial) { initial?.toEpochMillisUtc() }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val ms = pickerState.selectedDateMillis ?: return@TextButton
                    onConfirm(ms.toLocalDateUtc())
                }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = pickerState)
    }
}

@Composable
fun DateField(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        color = Color.White
    ) {
        Box(Modifier.fillMaxSize().padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
            Text(text)
        }
    }
}

@Composable
fun DateRow(
    label: String,
    start: LocalDate?,
    end: LocalDate?,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DateField(
                text = start?.toString() ?: "Select",
                modifier = Modifier.weight(1f),
                onClick = onStartClick
            )
            DateField(
                text = end?.toString() ?: "Select",
                modifier = Modifier.weight(1f),
                onClick = onEndClick
            )
        }
    }
}

private fun LocalDate.toEpochMillisUtc(): Long {
    val d = java.time.LocalDate.parse(this.toString())
    return d.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
}

private fun Long.toLocalDateUtc(): LocalDate {
    val date = java.time.LocalDate.ofEpochDay(this / 86_400_000L)
    return LocalDate(date.year, date.monthValue, date.dayOfMonth)
}