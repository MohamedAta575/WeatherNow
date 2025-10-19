package com.example.weathernow.presentation.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.weathernow.domain.model.DailyWeather
import com.example.weathernow.domain.model.HourlyWeather
import com.example.weathernow.ui.theme.BackgroundBrush
import com.example.weathernow.ui.theme.CardBackgroundColor
import com.example.weathernow.ui.theme.CardBorderColor
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrush)
            .padding(top = 16.dp)
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )

            state.error != null -> Text(
                state.error ?: "An unexpected error occurred.",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )

            state.data != null -> {
                val w = state.data ?: return@Box
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    LocationAndCurrentTemp(w)
                    Spacer(Modifier.height(32.dp))

                    HourlyForecastCard(w.hourly)
                    Spacer(Modifier.height(16.dp))

                    WeatherDetailsCard(
                        windKph = w.windKph,
                        humidity = w.humidity,
                        visibilityKm = 10.0,
                        pressureMb = 1013
                    )
                    Spacer(Modifier.height(16.dp))

                    DailyForecastCard(w.daily)
                }
            }
            else -> Text(
                "Waiting for location data. Please check connection or try searching.",
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
fun LocationAndCurrentTemp(w: com.example.weathernow.domain.model.WeatherInfo) {
    val maxTemp = w.daily.firstOrNull()?.maxTemp?.roundToInt() ?: 0
    val minTemp = w.daily.firstOrNull()?.minTemp?.roundToInt() ?: 0

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(w.cityName, color = Color.White, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))

        Text(
            "${w.currentTemp.roundToInt()}°",
            fontSize = 96.sp,
            color = Color.White,
            fontWeight = FontWeight.Light,
            lineHeight = 96.sp
        )

        Text(w.condition, color = Color.White, fontSize = 20.sp)

        Text(
            "H:${maxTemp}° L:${minTemp}°",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun HourlyForecastCard(hourly: List<HourlyWeather>) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Text(
                "Hourly Forecast",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                hourly.take(5).forEachIndexed { index, h ->
                    HourlyItem(
                        time = if (index == 0) "Now" else h.time,
                        temp = h.temp.roundToInt(),
                        condition = h.condition,
                        iconUrl = getWeatherIconUrl(h.condition)
                    )
                    Spacer(Modifier.width(20.dp))
                }
            }
        }
    }
}

@Composable
fun HourlyItem(time: String, temp: Int, condition: String, iconUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(50.dp)
    ) {
        Text(time, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f))
        Image(
            painter = rememberAsyncImagePainter(iconUrl),
            contentDescription = condition,
            modifier = Modifier.size(40.dp)
        )
        Text("${temp}°", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}


@Composable
fun WeatherDetailsCard(
    windKph: Double,
    humidity: Int,
    visibilityKm: Double,
    pressureMb: Int
) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                DetailItem(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "${windKph.roundToInt()} km/h",
                    modifier = Modifier.weight(1f)
                )
                DetailItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "$humidity%",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                DetailItem(
                    icon = Icons.Default.Visibility,
                    label = "Visibility",
                    value = "${visibilityKm.roundToInt()} km",
                    modifier = Modifier.weight(1f)
                )
                DetailItem(
                    icon = Icons.Default.Compress,
                    label = "Pressure",
                    value = "$pressureMb mb",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f))
        }
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}


@Composable
fun DailyForecastCard(daily: List<DailyWeather>) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "7-Day Forecast",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(10.dp))

            daily.forEach { d ->
                DailyItem(d)
                if (d != daily.last()) {
                    Divider(
                        color = CardBorderColor.copy(alpha = 0.4f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyItem(day: DailyWeather) {
    val dayOfWeek = remember(day.date) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(day.date)
            date?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) } ?: day.date
        } catch (e: Exception) {
            day.date
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(120.dp)) {
            Text(
                dayOfWeek,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(16.dp))
            Image(
                painter = rememberAsyncImagePainter(getWeatherIconUrl(day.condition)),
                contentDescription = day.condition,
                modifier = Modifier.size(32.dp)
            )
        }

        Text("${day.minTemp.roundToInt()}°", color = Color.Black.copy(alpha = 0.6f))

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        )

        Text("${day.maxTemp.roundToInt()}°", color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}


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