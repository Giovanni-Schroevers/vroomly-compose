package com.fsa_profgroep_4.vroomly.ui.screens.reservation.reserve

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.ui.components.DateRow
import com.fsa_profgroep_4.vroomly.ui.components.LicensePlateCard
import com.fsa_profgroep_4.vroomly.ui.components.VehicleImagesBlock
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyDatePickerDialog
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CreateReservationScreen(
    vehicleId: Int,
    modifier: Modifier = Modifier,
    viewModel: CreateReservationViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(vehicleId) { viewModel.start(vehicleId) }

    var startPickerOpen by remember { mutableStateOf(false) }
    var endPickerOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            VroomlyBackButton(
                onBackClicked = { viewModel.onCancel() }
            )
        },
        modifier = modifier.fillMaxSize()
    ) {
        padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            LicensePlateCard(licencePlate = state.licensePlate)

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                VehicleImagesBlock(imageUrls = state.imageUrls)
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }

            DateRow(
                label = "Dates:",
                start = state.startDate,
                end = state.endDate,
                onStartClick = { startPickerOpen = true },
                onEndClick = { endPickerOpen = true }
            )


            Column(verticalArrangement = Arrangement.spacedBy(6.dp)){
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)){
                    Text("Cost per day:", style = MaterialTheme.typography.titleMedium,  modifier = Modifier.weight(1f),)
                    Text("Total cost:", style = MaterialTheme.typography.titleMedium,  modifier = Modifier.weight(1f),)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)){
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = modifier
                            .height(44.dp)
                            .weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        )
                        {
                            Text(formatEuroNl(state.costPerDay))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = modifier
                            .height(44.dp)
                            .weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(formatEuroNl(state.totalCost))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.submit() },
                enabled = !state.isLoading && !state.isSubmitting && state.startDate != null && state.endDate != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                }
                Text("Book Vehicle")
            }

            Spacer(Modifier.height(6.dp))
        }
    }

    if (startPickerOpen) {
        VroomlyDatePickerDialog(
            initial = state.startDate,
            onDismiss = { startPickerOpen = false },
            onConfirm = { picked ->
                viewModel.setStartDate(picked)
                startPickerOpen = false
            }
        )
    }

    if (endPickerOpen) {
        VroomlyDatePickerDialog(
            initial = state.endDate,
            onDismiss = { endPickerOpen = false },
            onConfirm = { picked ->
                viewModel.setEndDate(picked)
                endPickerOpen = false
            }
        )
    }
}

private fun formatEuroNl(value: Double?): String {
    if (value == null) return "-"
    val nf = NumberFormat.getCurrencyInstance(Locale("nl", "NL"))
    return nf.format(value)
}
