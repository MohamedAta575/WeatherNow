package com.example.weathernow.presentation.auth

sealed class AuthIntent {
    data class SignIn(val email: String, val password: String, val rememberMe: Boolean) : AuthIntent()
    data class SignUp(val userName: String,val email: String, val password: String,) : AuthIntent()
    object SignOut : AuthIntent()
    object CheckUser : AuthIntent()
    data class ForgotPassword(val email: String) : AuthIntent()

}

