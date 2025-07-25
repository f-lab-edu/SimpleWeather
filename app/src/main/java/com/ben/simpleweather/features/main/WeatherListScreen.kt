package com.ben.simpleweather.features.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ben.simpleweather.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListTopAppBar(
    isDeleteMode: Boolean,
    selectedCount: Int,
    onCancelDeleteMode: () -> Unit,
    onConfirmDelete: () -> Unit,
    onEnterDeleteMode: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    if (isDeleteMode) {
        TopAppBar(
            title = { Text(stringResource(R.string.delete_mode_title, selectedCount)) },
            navigationIcon = {
                IconButton(onClick = onCancelDeleteMode) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                }
            },
            actions = {
                IconButton(
                    onClick = onConfirmDelete,
                    enabled = selectedCount > 0
                ) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
        )
    } else {
        TopAppBar(
            title = { Text(stringResource(R.string.weather_title)) },
            actions = {
                IconButton(onClick = onEnterDeleteMode) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_mode_enter))
                }
                IconButton(onClick = onNavigateToSearch) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_city))
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeatherListScreen(
    navController: NavController,
    viewModel: WeatherListViewModel = hiltViewModel()
) {
    val weatherList by viewModel.weatherList.collectAsState()

    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedForDelete = remember { mutableStateListOf<String>() }

    val listState = rememberLazyListState()

    val dragDropState = rememberDragDropState(
        lazyListState = listState,
        draggableItemsNum = weatherList.size,
        onMove = { fromIndex, toIndex ->
            viewModel.moveItem(fromIndex, toIndex)
        }
    )

    fun toggleSelection(cityName: String) {
        if (selectedForDelete.contains(cityName)) {
            selectedForDelete.remove(cityName)
        } else {
            selectedForDelete.add(cityName)
        }
    }

    Scaffold(
        topBar = {
            WeatherListTopAppBar(
                isDeleteMode = isDeleteMode,
                selectedCount = selectedForDelete.size,
                onCancelDeleteMode = {
                    isDeleteMode = false
                    selectedForDelete.clear()
                },
                onConfirmDelete = {
                    viewModel.deleteSelected(selectedForDelete.toList())
                    selectedForDelete.clear()
                    isDeleteMode = false
                },
                onEnterDeleteMode = { isDeleteMode = true },
                onNavigateToSearch = { navController.navigate("search") }
            )
        }
    ) { innerPadding ->
        val listModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .let { base ->
                if (!isDeleteMode) base.dragContainer(dragDropState) else base
            }

        if (weatherList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_registered_cities))
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = listModifier,
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                draggableItems(
                    items = weatherList,
                    dragDropState = dragDropState
                ) { modifier, item ->
                    WeatherCard(
                        modifier = modifier,
                        cityName = item.cityName,
                        temperature = item.temperature,
                        weatherType = item.weatherType,
                        isDeleteMode = isDeleteMode,
                        isSelected = selectedForDelete.contains(item.cityName),
                        onCardClick = {
                            if (isDeleteMode) {
                                toggleSelection(item.cityName)
                            } else {
                                navController.navigate("detail/${item.cityName}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    cityName: String,
    temperature: Int,
    weatherType: String,
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onCardClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            if (isDeleteMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onCardClick() }
                )
            }
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = weatherType,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = if (isDeleteMode) 8.dp else 0.dp, end = 12.dp)
            )
            Column {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$weatherType, $temperature°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
