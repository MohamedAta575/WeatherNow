package com.example.weathernow.presentation.splash

data class SplashState(
    val isChecking: Boolean = true,
    val isSignedIn: Boolean = false,
    val userEmail: String? = null
)