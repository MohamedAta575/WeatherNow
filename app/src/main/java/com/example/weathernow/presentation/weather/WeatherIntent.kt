package com.example.weathernow.presentation.weather

sealed class WeatherIntent {
    data class LoadWeather(val city: String) : WeatherIntent()
}