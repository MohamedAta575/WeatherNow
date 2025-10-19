package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.model.WeatherInfo
import com.example.weathernow.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repo: WeatherRepository
) {
    suspend operator fun invoke(city: String): WeatherInfo? = repo.getWeatherByCity(city)
}
