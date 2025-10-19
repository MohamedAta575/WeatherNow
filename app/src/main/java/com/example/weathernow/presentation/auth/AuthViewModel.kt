package com.example.weathernow.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        handleIntent(AuthIntent.CheckUser)
    }

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> signIn(intent.email, intent.password, intent.rememberMe)
            is AuthIntent.SignUp -> signUp(intent.userName, intent.email, intent.password)
            is AuthIntent.SignOut -> signOut()
            is AuthIntent.CheckUser -> checkUser()
        }
    }

    private fun signIn(email: String, password: String, rememberMe: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(
                emailError = if (email.isBlank()) "Email cannot be empty" else null,
                passwordError = if (password.isBlank()) "Password cannot be empty" else null
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repo.signIn(email, password)
            if (result.isSuccess) {
                if (rememberMe) {
                    repo.setRememberMe(true)
                }
                val name = repo.getCurrentUserName()
                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Login successful",
                    isSignedIn = true,
                    userEmail = email,
                    userName = name
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun signUp(userName: String,email: String, password: String,) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(
                emailError = if (email.isBlank()) "Email cannot be empty" else null,
                passwordError = if (password.isBlank()) "Password cannot be empty" else null
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repo.signUp(userName,email, password)
            _state.value = if (result.isSuccess) {
                _state.value.copy(
                    isLoading = false,
                    successMessage = "Account created successfully",
                    isSignedIn = true,
                    userEmail = email,
                    userName = userName

                )
            } else {
                _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
            }
        }
    }


    private fun signOut() {
        viewModelScope.launch {
            repo.signOut()
            repo.setRememberMe(false)
            _state.value = AuthState()
        }
    }

    private fun checkUser() {
        viewModelScope.launch {
            val rememberMe = repo.getRememberMe()
            rememberMe.collect { remember ->
                val email = repo.getCurrentUserEmail()
                val name = repo.getCurrentUserName()
                _state.value = _state.value.copy(
                    isSignedIn = remember && email != null,
                    userEmail = if (remember) email else null,
                    userName = name
                )
            }
        }
    }
}