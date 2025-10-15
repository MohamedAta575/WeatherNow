package com.example.weathernow.presentation.weather

import com.example.weathernow.domain.model.WeatherInfo

data class WeatherState(
    val data: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
