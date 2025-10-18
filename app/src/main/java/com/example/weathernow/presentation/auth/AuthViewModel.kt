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

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> signIn(intent.email, intent.password)
            is AuthIntent.SignUp -> signUp(intent.email, intent.password)
            is AuthIntent.SignOut -> signOut()
            is AuthIntent.CheckUser -> checkUser()
        }
    }

    private fun signIn(email: String, password: String) {
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
            _state.value = if (result.isSuccess) {
                _state.value.copy(
                    isLoading = false,
                    successMessage = "Login successful",
                    isSignedIn = true
                )
            } else {
                _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
            }
        }
    }

    private fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(
                emailError = if (email.isBlank()) "Email cannot be empty" else null,
                passwordError = if (password.isBlank()) "Password cannot be empty" else null
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repo.signUp(email, password)
            _state.value = if (result.isSuccess) {
                _state.value.copy(
                    isLoading = false,
                    successMessage = "Account created successfully",
                    isSignedIn = true
                )
            } else {
                _state.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            repo.signOut()
            _state.value = AuthState()
        }
    }

    private fun checkUser() {
        val email = repo.getCurrentUserEmail()
        _state.value = _state.value.copy(isSignedIn = email != null, userEmail = email)
    }
}
