package com.example.weathernow.domain.repository

import com.example.weathernow.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun getCities(query: String): List<String>
    suspend fun getWeatherByCity(city: String): WeatherInfo?
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): WeatherInfo?

}