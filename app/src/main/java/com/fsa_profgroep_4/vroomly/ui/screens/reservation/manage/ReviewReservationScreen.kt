package com.fsa_profgroep_4.vroomly.ui.screens.reservation.manage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rocketreserver.type.ReservationStatus
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.DateRow
import com.fsa_profgroep_4.vroomly.ui.components.LicensePlateCard
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else if (state.imageUrls.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.ic_directions_car),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.no_images),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.imageUrls) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillParentMaxHeight()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.ic_directions_car),
                                error = painterResource(R.drawable.ic_directions_car)
                            )
                        }
                    }
                }
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

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Cost per day:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text("Total cost:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(44.dp).weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) { Text(formatEuroNl(state.costPerDay)) }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(44.dp).weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) { Text(formatEuroNl(state.totalCost)) }
                    }
                }
            }

            if (state.status == ReservationStatus.CONFIRMED
                || state.status == ReservationStatus.ACTIVE){

                Column(Modifier.padding(16.dp)) {
                    Button(onClick = {
                        viewModel.onOpenMap(reservationId)
                    }) { Text("Show car on map") }

                    Spacer(Modifier.height(12.dp))
                }
            }

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

private fun formatEuroNl(value: Double?): String {
    if (value == null) return "-"
    val nf = NumberFormat.getCurrencyInstance(Locale("nl", "NL"))
    return nf.format(value)
}