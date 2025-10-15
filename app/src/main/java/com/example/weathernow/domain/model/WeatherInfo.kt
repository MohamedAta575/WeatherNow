package com.example.weathernow.domain.model


data class WeatherInfo(
    val cityName: String,
    val currentTemp: Double,
    val condition: String,
    val humidity: Int,
    val windKph: Double,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>
)

data class HourlyWeather(
    val time: String,
    val temp: Double,
    val condition: String
)

data class DailyWeather(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val condition: String
)