package com.example.weathernow.data.remote.api

import com.example.weathernow.data.remote.dto.CitySearchDto
import com.example.weathernow.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/search.json")
    suspend fun searchCities(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<CitySearchDto>

    @GET("v1/forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 7,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherDto

    @GET("v1/forecast.json")
    suspend fun getWeatherByCoordinates(
        @Query("key") apiKey: String,
        @Query("q") latLon: String,
        @Query("days") days: Int = 7,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherDto
}