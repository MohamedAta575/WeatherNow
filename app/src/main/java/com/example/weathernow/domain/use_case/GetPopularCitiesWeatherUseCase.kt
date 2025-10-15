package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.repository.WeatherRepository
import com.example.weathernow.domain.model.PopularCitySummary
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetPopularCitiesWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    private val defaultPopularCities = listOf(
        "New York, USA",
        "London, UK",
        "Tokyo, Japan",
        "Paris, France"
    )

    suspend operator fun invoke(): List<PopularCitySummary> = coroutineScope {
        val deferredResults = defaultPopularCities.map { city ->
            async {
                try {
                    val parts = city.split(", ")
                    val cityName = parts[0]
                    val countryName = parts.getOrElse(1) { "" }

                    val info = repository.getWeatherByCity(city)

                    PopularCitySummary(
                        name = cityName,
                        country = countryName,
                        temperature = "${info.currentTemp.toInt()}°C"
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }

        deferredResults.awaitAll().filterNotNull()
    }
}