package com.example.weathernow.presentation.weather

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.weathernow.domain.model.DailyWeather
import com.example.weathernow.domain.model.HourlyWeather
import com.example.weathernow.ui.theme.BackgroundBrush
import com.example.weathernow.ui.theme.CardBackgroundColor
import com.example.weathernow.ui.theme.CardBorderColor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val weatherState by viewModel.state.collectAsState()

    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrush)
            .padding(top = 16.dp)
    ) {
        when {
            weatherState.isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )

            weatherState.error != null -> Text(
                weatherState.error ?: "An unexpected error occurred.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )

            weatherState.data != null -> {
                val weatherInfo = weatherState.data!!
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    WeatherTopBar(cityName = weatherInfo.cityName, onBackPressed = onBack)
                    Spacer(Modifier.height(16.dp))
                    CurrentTemperatureSection(weatherInfo)
                    Spacer(Modifier.height(32.dp))
                    HourlyForecastSection(weatherInfo.hourly)
                    Spacer(Modifier.height(16.dp))
                    WeatherDetailsSection(
                        windSpeedKph = weatherInfo.windKph,
                        humidityPercentage = weatherInfo.humidity,
                        visibilityKm = 10.0,
                        pressureMb = 1013
                    )
                    Spacer(Modifier.height(16.dp))
                    DailyForecastSection(weatherInfo.daily)
                }
            }

            else -> Text(
                "Waiting for location data. Please check connection or try searching.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun WeatherTopBar(cityName: String, onBackPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackPressed) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            cityName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun CurrentTemperatureSection(weatherInfo: com.example.weathernow.domain.model.WeatherInfo) {
    val maxTemp = weatherInfo.daily.firstOrNull()?.maxTemp?.roundToInt() ?: 0
    val minTemp = weatherInfo.daily.firstOrNull()?.minTemp?.roundToInt() ?: 0

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                weatherInfo.cityName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "${weatherInfo.currentTemp.roundToInt()}°",
            fontSize = 96.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Light,
            lineHeight = 96.sp
        )

        Text(
            weatherInfo.condition,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            "H:$maxTemp° L:$minTemp°",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

// ===== Hourly Forecast Horizontal Scroll =====
@Composable
fun HourlyForecastSection(hourlyForecasts: List<HourlyWeather>) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Text(
                "Hourly Forecast",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(hourlyForecasts.take(10)) { index, hourly ->
                    val displayTime = if (index == 0) "Now" else hourly.time
                    HourlyForecastItem(displayTime, hourly.temp.roundToInt(), hourly.condition)
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(timeLabel: String, temperature: Int, condition: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(50.dp)
    ) {
        Text(timeLabel, style = MaterialTheme.typography.bodySmall)
        AsyncImage(
            model = getWeatherIconUrl(condition),
            contentDescription = condition,
            modifier = Modifier.size(40.dp)
        )
        Text(
            "$temperature°",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ===== Weather Details =====
@Composable
fun WeatherDetailsSection(
    windSpeedKph: Double,
    humidityPercentage: Int,
    visibilityKm: Double,
    pressureMb: Int
) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                WeatherDetailItem(
                    Icons.Default.Air,
                    "Wind",
                    "${windSpeedKph.roundToInt()} km/h",
                    Modifier.weight(1f)
                )
                WeatherDetailItem(
                    Icons.Default.WaterDrop,
                    "Humidity",
                    "$humidityPercentage%",
                    Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                WeatherDetailItem(
                    Icons.Default.Visibility,
                    "Visibility",
                    "${visibilityKm.roundToInt()} km",
                    Modifier.weight(1f)
                )
                WeatherDetailItem(
                    Icons.Default.Compress,
                    "Pressure",
                    "$pressureMb mb",
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

// ===== Daily Forecast Vertical Scroll =====
@Composable
fun DailyForecastSection(dailyForecasts: List<DailyWeather>) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "7-Day Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(dailyForecasts) { day ->
                DailyForecastItem(day)
                Divider(color = CardBorderColor.copy(alpha = 0.4f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun DailyForecastItem(dayForecast: DailyWeather) {
    val dayLabel = remember(dayForecast.date) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dayForecast.date)
            date?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) }
                ?: dayForecast.date
        } catch (e: Exception) {
            dayForecast.date
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(dayLabel, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(16.dp))
            AsyncImage(
                model = getWeatherIconUrl(dayForecast.condition),
                contentDescription = dayForecast.condition,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            "${dayForecast.minTemp.roundToInt()}°",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
        )

        Text(
            "${dayForecast.maxTemp.roundToInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ===== App Card =====
@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = content
    )
}

// ===== Weather Icons =====
private fun getWeatherIconUrl(condition: String): String {
    return when {
        condition.contains(
            "rain",
            ignoreCase = true
        ) -> "https://cdn.weatherapi.com/weather/64x64/day/308.png"

        condition.contains(
            "cloudy",
            ignoreCase = true
        ) -> "https://cdn.weatherapi.com/weather/64x64/day/116.png"

        condition.contains(
            "sun",
            ignoreCase = true
        ) -> "https://cdn.weatherapi.com/weather/64x64/day/113.png"

        else -> "https://cdn.weatherapi.com/weather/64x64/day/113.png"
    }
}
