package com.fsa_profgroep_4.vroomly.ui.screens.reservation.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.ui.components.ReservationCardData
import com.fsa_profgroep_4.vroomly.ui.components.ReservationListItem
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReservationOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: ReservationViewModel = koinViewModel()
){
    var currentRoute by remember { mutableStateOf("reservations") }
    val state by viewModel.uiState.collectAsState()

    var displayItems by remember { mutableStateOf<List<ReservationCardData>>(emptyList()) }

    LaunchedEffect(state.items) {
        displayItems = state.items
    }

    Scaffold(
        topBar = {
            VroomlyBackButton(
                onBackClicked = { viewModel.onCancel() }
            )
        },
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            VroomlyBottomBar(
                currentRoute = currentRoute,
                onNavigate = {
                    currentRoute = it
                    viewModel.onNavigate(it)
                }
            )
        }

    ){ padding ->
        Text(
            text = "Reservations",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(Modifier.fillMaxSize()) {

                ReservationList(
                    items = displayItems,
                    hasMore = state.hasMore,
                    isLoading = state.isLoading,
                    onLoadMore = { viewModel.onLoadMore() },
                )
                if (state.isLoading && displayItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                }
            }
        }
}


@Composable
fun ReservationList(
    items: List<ReservationCardData>,
    hasMore: Boolean,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(isLoading, hasMore) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .collect { if (hasMore) onLoadMore() }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.testTag("reservation_list")
    ) {
        items(items) { ReservationListItem(data = it) }
    }
}
