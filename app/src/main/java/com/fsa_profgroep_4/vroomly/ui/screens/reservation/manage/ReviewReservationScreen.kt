package com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rocketreserver.type.ReservationStatus
import com.fsa_profgroep_4.vroomly.ui.components.DateRow
import com.fsa_profgroep_4.vroomly.ui.components.LicensePlateCard
import com.fsa_profgroep_4.vroomly.ui.components.VehicleImagesBlock
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReviewReservationScreen(
    reservationId: Int,
    modifier: Modifier = Modifier,
    viewModel: ReviewReservationViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(reservationId) { viewModel.start(reservationId) }

    Scaffold(
        topBar = { VroomlyBackButton(onBackClicked = { viewModel.onCancel() }) },
        modifier = modifier.fillMaxSize()
    ) { padding ->
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
                onStartClick = { },
                onEndClick = {  }
            )

            Spacer(Modifier.weight(1f))

            if (state.status == ReservationStatus.PENDING) {
                Button(
                    onClick = { viewModel.confirmReservation() },
                    enabled = !state.isLoading && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text("Confirm reservation")
                }

                OutlinedButton(
                    onClick = { viewModel.cancelReservation() },
                    enabled = !state.isLoading && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel reservation")
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}