package com.example.weathernow.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CitySearchDto(
    val name: String,
    val region: String,
    val country: String
)

@JsonClass(generateAdapter = true)
data class WeatherDto(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto
)

@JsonClass(generateAdapter = true)
data class LocationDto(val name: String)

@JsonClass(generateAdapter = true)
data class CurrentDto(
    @Json(name = "temp_c") val temp_c: Double,
    val condition: ConditionDto,
    @Json(name = "wind_kph") val wind_kph: Double,
    val humidity: Int
)

@JsonClass(generateAdapter = true)
data class ConditionDto(val text: String)

@JsonClass(generateAdapter = true)
data class ForecastDto(val forecastday: List<ForecastDayDto>)

@JsonClass(generateAdapter = true)
data class ForecastDayDto(
    val date: String,
    val day: DayDto,
    val hour: List<HourDto>
)

@JsonClass(generateAdapter = true)
data class DayDto(
    @Json(name = "maxtemp_c") val maxtemp_c: Double,
    @Json(name = "mintemp_c") val mintemp_c: Double,
    val condition: ConditionDto
)

@JsonClass(generateAdapter = true)
data class HourDto(
    val time: String,
    @Json(name = "temp_c") val temp_c: Double,
    val condition: ConditionDto
)