package com.example.weathernow.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)
