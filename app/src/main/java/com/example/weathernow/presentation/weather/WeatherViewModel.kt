package com.example.weathernow.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.domain.repository.WeatherRepository
import com.example.weathernow.domain.use_case.GetPopularCitiesWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val getPopularCitiesWeatherUseCase: GetPopularCitiesWeatherUseCase
): ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities

    private val _recentSearches = MutableStateFlow(listOf("San Francisco", "Los Angeles", "Chicago"))
    val recentSearches: StateFlow<List<String>> = _recentSearches

    data class PopularCity(
        val name: String,
        val country: String,
        val temperature: String
    )

    private val _popularCities = MutableStateFlow<List<PopularCity>>(emptyList())
    val popularCities: StateFlow<List<PopularCity>> = _popularCities

    init {
        loadPopularCitiesWeather()
    }



    fun loadWeather(city: String) {
        _state.value = WeatherState(isLoading = true)
        viewModelScope.launch {
            try {

                val info = repository.getWeatherByCity(city)
                _state.value = WeatherState(data = info)
            } catch (e: Exception) {
                _state.value = WeatherState(error = e.message ?: "An error occurred while fetching weather data.")
            }
        }
    }


    private fun loadPopularCitiesWeather() {
        viewModelScope.launch {
            try {

                val domainResults = getPopularCitiesWeatherUseCase()
                val presentationResults = domainResults.map { summary ->
                    PopularCity(
                        name = summary.name,
                        country = summary.country,
                        temperature = summary.temperature
                    )
                }
                _popularCities.value = presentationResults
            } catch (e: Exception) {
                _popularCities.value = emptyList()
            }
        }
    }

    fun loadCities(query: String) {
        if (query.length < 2) {
            _cities.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val result = repository.getCities(query)
                _cities.value = result
            } catch (e: Exception) {
                _cities.value = emptyList()
            }
        }
    }
}