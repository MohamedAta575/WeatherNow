package com.example.weathernow.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Location : Screen("location")
    object Weather : Screen("weather/{city}") {
        fun createRoute(city: String) = "weather/$city"
    }
    object Splash : Screen("splash")

}