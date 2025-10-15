package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(query: String): List<String> {
        return repository.getCities(query)
    }
}
