package com.fsa_profgroep_4.vroomly.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.fsa_profgroep_4.vroomly.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.ui.components.ReservationCardData
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCard
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardHomeUi
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import com.fsa_profgroep_4.vroomly.ui.components.VehicleListItem
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton
import com.fsa_profgroep_4.vroomly.ui.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    var currentRoute by remember { mutableStateOf("home") }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            VroomlyBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    viewModel.onNavigate(route)
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            HomeHeader()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.screenPadding)
            ) {
                Spacer(Modifier.height(12.dp))

                SectionTitle("Find vehicles")
                VehicleRow(
                    items = state.vehiclesToRent,
                    onCardClick = { vehicleId -> viewModel.onVehicleSelected(vehicleId) }
                )

                Spacer(Modifier.height(10.dp))

                SectionTitle("My vehicles")
                VehicleRow(
                    items = state.myVehicles,
                    onCardClick = { vehicleId -> viewModel.onVehicleSelected(vehicleId) }
                )

                Spacer(Modifier.height(10.dp))

                SectionTitle("My reservations")
                ReservationRow(
                    items = state.vehiclesRented,
                    onReservationClick = { reservationId -> viewModel.onReservationSelected(reservationId) }
                )

                Spacer(Modifier.height(16.dp))

                VroomlyButton(
                    text = stringResource(R.string.start_drive),
                    onClick = { viewModel.onTrackDrive() }
                )

                if (state.isLoading && state.vehiclesToRent.isEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error?.let { msg ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2E86FF),
            Color(0xFF4FC3F7),
            Color(0xFFFFA24A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(gradient)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "From here to anywhere.",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = "Center"
            )


        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "→",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun VehicleRow(
    items: List<VehicleCardHomeUi>,
    onCardClick: (Int) -> Unit,
    singleRowStyle: Boolean = false
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = if (singleRowStyle) 0.dp else 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            VehicleCard(
                data = item,
                onClick = { onCardClick(item.vehicleId) }
            )
        }
    }
}

@Composable
private fun ReservationRow(
    items: List<ReservationCardData>,
    onReservationClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        items(items) { item ->
            ReservationHomeCard(
                data = item,
                onClick = { onReservationClick(item.reservationId) }
            )
        }
    }
}

@Composable
private fun ReservationHomeCard(
    data: ReservationCardData,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = data.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "€${data.totalCost}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}


@Composable
fun VehicleCards(
    items: List<VehicleCardUi>,
    hasMore: Boolean,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    onVehicleClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(items.size, isLoading, hasMore) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .collect { last ->
                if (hasMore && !isLoading && last >= items.size - 3) onLoadMore()
            }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.testTag("vehicle_list")
    ) {
        items(items) { item ->
            VehicleListItem(
                data = item,
                onClick = { onVehicleClick(item.vehicleId) }
            )
        }

        if (isLoading && items.isNotEmpty()) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    }
}
