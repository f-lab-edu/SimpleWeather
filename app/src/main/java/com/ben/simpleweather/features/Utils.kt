package com.ben.simpleweather.features

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ben.simpleweather.features.detail.WEATHER_ICON_BASE_URL

@Composable
fun WeatherIcon(iconCode: String, size: Dp = 60.dp, modifier: Modifier = Modifier) {
    val iconUrl = "${WEATHER_ICON_BASE_URL}${iconCode}@2x.png"
    AsyncImage(
        model = iconUrl,
        contentDescription = null,
        modifier = modifier.size(size)
    )
}