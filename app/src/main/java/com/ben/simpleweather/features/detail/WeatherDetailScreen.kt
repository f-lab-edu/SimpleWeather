package com.ben.simpleweather.features.detail

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ben.simpleweather.R
import com.ben.simpleweather.data.ForecastItem
import com.ben.simpleweather.data.WeatherDetail
import com.ben.simpleweather.features.WeatherIcon
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val WEATHER_ICON_BASE_URL: String = "https://openweathermap.org/img/wn/"
val directions = listOf(
    "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
    "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    cityid: Int,
    navController: NavController,
    viewModel: WeatherDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cityName by viewModel.cityName.collectAsState()

    LaunchedEffect(cityid) {
        viewModel.loadWeather(cityid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = cityName ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is WeatherUiState.Success -> {
                    WeatherDetailContent(
                        modifier = Modifier.fillMaxSize(),
                        weather = state.weather,
                        forecast = state.forecast
                    )
                }

                is WeatherUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailContent(
    modifier: Modifier = Modifier,
    weather: WeatherDetail,
    forecast: List<ForecastItem>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            WeatherIcon(iconCode = weather.iconCode, size = 60.dp)
            Column {
                Text(
                    text = "${weather.temperature}°C",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(text = weather.description, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = stringResource(id = R.string.feels_like, weather.feelsLike),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            text = stringResource(R.string.hourly_forecast),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(forecast) { item ->
                ForecastHourCard(item)
            }
        }

        Text(
            text = stringResource(R.string.details),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        DetailRow(
            iconResId = R.drawable.outline_rainy_24,
            label = stringResource(R.string.chance_of_rain),
            value = "${(weather.precipitationChance * 100).toInt()}%"
        )
        DetailRow(
            iconResId = R.drawable.outline_water_drop_24,
            label = stringResource(R.string.humidity),
            value = "${weather.humidity}%"
        )
        DetailRow(
            iconResId = R.drawable.outline_air_24,
            label = stringResource(R.string.wind),
            value = "${weather.windSpeed} km/h"
        )
        DetailRow(
            iconResId = R.drawable.outline_visibility_24,
            label = stringResource(R.string.visibility),
            value = "${weather.visibility / 1000.0} km"
        )

        DetailRow(
            iconResId = R.drawable.outline_cloud_24,
            label = stringResource(R.string.cloudiness),
            value = "${weather.cloudiness}%"
        )

        DetailRow(
            iconResId = R.drawable.outline_explore_24,
            label = stringResource(R.string.wind_direction),
            value = degToCompass(weather.windDegree)
        )

        DetailRow(
            iconResId = R.drawable.outline_wb_twilight_24,
            label = stringResource(R.string.sunrise),
            value = formatTime(weather.sunrise)
        )

        DetailRow(
            iconResId = R.drawable.outline_nights_stay_24,
            label = stringResource(R.string.sunset),
            value = formatTime(weather.sunset)
        )

        weather.rainAmount?.let {
            DetailRow(
                iconResId = R.drawable.outline_grain_24,
                label = stringResource(R.string.rain_amount),
                value = "$it mm"
            )
        }

        weather.snowAmount?.let {
            DetailRow(
                iconResId = R.drawable.outline_ac_unit_24,
                label = stringResource(R.string.snow_amount),
                value = "$it mm"
            )
        }
    }
}

@Composable
fun ForecastHourCard(item: ForecastItem) {
    Column(
        modifier = Modifier
            .size(width = 80.dp, height = 140.dp)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WeatherIcon(
            iconCode = item.iconCode,
            size = 46.dp
        )

        Spacer(modifier = Modifier.height(8.dp)) // 아이콘과 텍스트 사이 간격

        Text(
            text = "${item.temperature}°",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = item.time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun DetailRow(
    @DrawableRes iconResId: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun degToCompass(deg: Int): String {
    val index = ((deg / 22.5) + 0.5).toInt() % 16
    return directions[index]
}

fun formatTime(unixTime: Long): String {
    val instant = Instant.ofEpochSecond(unixTime)
    val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
    return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
}