package com.example.weathernow.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.domain.repository.WeatherRepository
import com.example.weathernow.domain.use_case.GetPopularCitiesWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherEvent {
    data object NavigateToWeatherDetail : WeatherEvent()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val getPopularCitiesWeatherUseCase: GetPopularCitiesWeatherUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities

    private val _recentSearches = MutableStateFlow(listOf("San Francisco", "Los Angeles", "Chicago"))
    val recentSearches: StateFlow<List<String>> = _recentSearches
    private val _events = Channel<WeatherEvent>()
    val events = _events.receiveAsFlow()

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

    fun handleIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.LoadWeather -> loadWeatherByCity(intent.city)
            is WeatherIntent.LoadWeatherByLocation -> loadWeatherByLocation(intent.latitude, intent.longitude)
        }
    }

    private fun loadWeatherByCity(city: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val info = repository.getWeatherByCity(city)
                _state.update { it.copy(isLoading = false, data = info) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error loading weather") }
            }
        }
    }

    private fun loadWeatherByLocation(lat: Double, lon: Double) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val info = repository.getWeatherByCoordinates(lat, lon)

                _state.update { it.copy(isLoading = false, data = info) }

                _events.send(WeatherEvent.NavigateToWeatherDetail)

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error loading weather") }
            }
        }
    }

    private fun loadPopularCitiesWeather() {
        viewModelScope.launch {
            try {
                val domainResults = getPopularCitiesWeatherUseCase()
                _popularCities.value = domainResults.map { summary ->
                    PopularCity(summary.name, summary.country, summary.temperature)
                }
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