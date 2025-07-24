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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ben.simpleweather.R

// 날씨 상태에 따른 아이콘 매핑
private fun weatherIcon(type: String): ImageVector = when (type) {
    "Sunny" -> Icons.Filled.Done
    "Cloudy" -> Icons.Filled.Done
    "Rainy" -> Icons.Filled.Done
    "Snowy" -> Icons.Filled.Done
    "Windy" -> Icons.Filled.Done
    else -> Icons.Filled.Done
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
            if (isDeleteMode) {
                TopAppBar(
                    title = { Text(stringResource(R.string.delete_mode_title, selectedForDelete.size)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            isDeleteMode = false
                            selectedForDelete.clear()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.deleteSelected(selectedForDelete.toList())
                                selectedForDelete.clear()
                                isDeleteMode = false
                            },
                            enabled = selectedForDelete.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.weather_title)) },
                    actions = {
                        IconButton(onClick = { isDeleteMode = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_mode_enter))
                        }
                        IconButton(onClick = { navController.navigate("search") }) {
                            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_city))
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    // 삭제 모드가 아닐 때만 dragContainer 적용
                    .then(if (!isDeleteMode) Modifier.dragContainer(dragDropState) else Modifier),
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
                imageVector = weatherIcon(weatherType),
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
