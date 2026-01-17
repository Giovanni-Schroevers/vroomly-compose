package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.Overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.type.VehicleFilterInput
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import com.fsa_profgroep_4.vroomly.ui.components.VehicleListItem
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBackButton
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyBottomBar

import org.koin.androidx.compose.koinViewModel

@Composable
fun VehiclesOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: VehiclesViewModel = koinViewModel()
) {
    var currentRoute by remember { mutableStateOf("vehicles") }
    val state by viewModel.uiState.collectAsState()

    var filtersOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    var model by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var minCost by remember { mutableStateOf("") }
    var maxCost by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(SortOption.NONE) }

    var displayItems by remember { mutableStateOf<List<VehicleCardUi>>(emptyList()) }

    remember(model, brand, minCost, maxCost) {
        Optional.Present(
            VehicleFilterInput(
                brand = brand.takeIf { it.isNotBlank() }?.let { Optional.present(it) } ?: Optional.Absent,
                model = model.takeIf { it.isNotBlank() }?.let { Optional.present(it) } ?: Optional.Absent,
                minCostPerDay = minCost.toDoubleOrNull()?.let { Optional.present(it) } ?: Optional.Absent,
                maxCostPerDay = maxCost.toDoubleOrNull()?.let { Optional.present(it) } ?: Optional.Absent
            )
        )
    }

    remember(model, brand, minCost, maxCost) { model.isNotBlank() || brand.isNotBlank() || minCost.isNotBlank() || maxCost.isNotBlank() }

    LaunchedEffect(state.items) {
        displayItems = state.items
    }

    val minCostValue = minCost.toDoubleOrNull()
    val maxCostValue = maxCost.toDoubleOrNull()

    // Auto updating when typing
    val shownItems = remember(displayItems, model, brand, minCostValue, maxCostValue, sortOption) {
        val filtered = displayItems.filter { v ->
            (model.isBlank() || v.title.contains(model, ignoreCase = true)) &&
                    (brand.isBlank() || v.title.contains(brand, ignoreCase = true)) &&
                    (minCostValue == null || v.costPerDay >= minCostValue) &&
                    (maxCostValue == null || v.costPerDay <= maxCostValue)
        }

        // Sorting
        when (sortOption) {
            SortOption.NONE -> filtered
            SortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.costPerDay }
            SortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.costPerDay }
            SortOption.STARS_LOW_HIGH -> filtered.sortedBy { it.badgeText.toDoubleOrNull() ?: 0.0 }
            SortOption.STARS_HIGH_LOW -> filtered.sortedByDescending { it.badgeText.toDoubleOrNull() ?: 0.0 }
        }
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
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    placeholder = { Text(stringResource(R.string.search)) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("vehicle_search_field")
                )

                Image(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "Filters",
                    modifier = Modifier
                        .size(width = 36.dp, height = 32.dp)
                        .clickable { filtersOpen = !filtersOpen }
                )
            }
            Box {
                TextButton(onClick = { sortMenuOpen = true }) {
                    Text(stringResource(sortOption.labelRes))
                }

                DropdownMenu(
                    expanded = sortMenuOpen,
                    onDismissRequest = { sortMenuOpen = false }
                ) {
                    SortOption.entries.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(stringResource(opt.labelRes)) },
                            onClick = {
                                sortOption = opt
                                sortMenuOpen = false
                            }
                        )
                    }
                }
            }
            if (filtersOpen) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    placeholder = { Text(stringResource(R.string.brand)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 44.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp),
                        value = minCost,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                minCost = input
                            }
                        },
                        placeholder = { Text("Min €") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp),
                        value = maxCost,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                maxCost = input
                            }
                        },
                        placeholder = { Text("Max €") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }

            Box(Modifier.fillMaxSize()) {

                VehicleList(
                    items = shownItems,
                    hasMore = state.hasMore,
                    isLoading = state.isLoading,
                    onLoadMore = { viewModel.loadNextPage() },
                    onVehicleClick = { id -> viewModel.onVehicleSelected(id) }
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
fun VehicleList(
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
        modifier = Modifier.testTag("vehicle_list")) {
        items(items) { item ->
            VehicleListItem(
                data = item,
                onClick = { onVehicleClick(item.vehicleId) }
        ) }

        // footer spinner for pagination
        if (isLoading && items.isNotEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center

                ) { CircularProgressIndicator() }
            }
        }
    }
}

enum class SortOption(val labelRes: Int) {
    NONE(R.string.sort),
    PRICE_LOW_HIGH(R.string.sort_price_low_high),
    PRICE_HIGH_LOW(R.string.sort_price_high_low),
    STARS_LOW_HIGH(R.string.sort_stars_low_high),
    STARS_HIGH_LOW(R.string.sort_stars_high_low)
}




