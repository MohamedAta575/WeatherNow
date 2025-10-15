package com.example.weathernow.presentation.auth

data class AuthState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val error: String? = null
)
