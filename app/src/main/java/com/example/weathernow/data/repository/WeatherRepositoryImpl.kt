package com.example.weathernow.data.repository

import com.example.weathernow.data.remote.api.WeatherApiService
import com.example.weathernow.domain.model.*
import com.example.weathernow.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val apiKey: String
): WeatherRepository {

    override suspend fun getCities(query: String): List<String> {
        val response = api.searchCities(apiKey, query)
        return response.map { "${it.name}, ${it.country}" }
    }

    override suspend fun getWeatherByCity(city: String): WeatherInfo {
        val response = api.getWeather(apiKey, city, days = 7)
        return WeatherInfo(
            cityName = response.location.name,
            currentTemp = response.current.temp_c,
            condition = response.current.condition.text,
            humidity = response.current.humidity,
            windKph = response.current.wind_kph,
            hourly = response.forecast.forecastday.firstOrNull()?.hour?.map {
                HourlyWeather(
                    time = it.time.takeLast(5),
                    temp = it.temp_c,
                    condition = it.condition.text
                )
            } ?: emptyList(),
            daily = response.forecast.forecastday.map {
                DailyWeather(
                    date = it.date,
                    minTemp = it.day.mintemp_c,
                    maxTemp = it.day.maxtemp_c,
                    condition = it.day.condition.text
                )
            }
        )
    }
}
