package com.example.weathernow.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.domain.use_case.GetCurrentUserUseCase
import com.example.weathernow.domain.use_case.SignInUseCase
import com.example.weathernow.domain.use_case.SignOutUseCase
import com.example.weathernow.domain.use_case.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess


    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> signIn(intent.email, intent.password)
            is AuthIntent.SignUp -> signUp(intent.email, intent.password)
            is AuthIntent.SignOut -> signOut()
            is AuthIntent.CheckUser -> checkUser()
        }
    }


    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = signInUseCase(email, password)
            val currentState = _state.value

            result.fold(
                onSuccess = {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isSignedIn = true,
                        error = null
                    )
                    _loginSuccess.value = true

                },
                onFailure = { throwable ->

                    val message = when {
                        throwable.message?.contains("no user record", true) == true ->
                            "No account found with this email."

                        throwable.message?.contains("password is invalid", true) == true ||
                                throwable.message?.contains("invalid password", true) == true ->
                            "Incorrect password. Please try again."

                        throwable.message?.contains("badly formatted", true) == true ->
                            "Invalid email format."

                        throwable.message?.contains("blocked all requests", true) == true ->
                            "Too many failed attempts. Try again later."

                        else ->
                            "Sign-in failed: ${throwable.message ?: "Unknown error"}"
                    }

                    _state.value = currentState.copy(
                        isLoading = false,
                        isSignedIn = false,
                        error = message
                    )
                }
            )
        }
    }

    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = signUpUseCase(email, password)
            val currentState = _state.value

            result.fold(
                onSuccess = {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isSignedIn = true,
                        error = null
                    )
                    _loginSuccess.value = true
                },
                onFailure = { throwable ->
                    val message = when {
                        throwable.message?.contains("already in use", true) == true ->
                            "This email is already in use. Try logging in instead."

                        throwable.message?.contains("badly formatted", true) == true ->
                            "Invalid email format."

                        throwable.message?.contains("WEAK_PASSWORD", true) == true ||
                                throwable.message?.contains(
                                    "Password should be at least",
                                    true
                                ) == true ->
                            "Password should be at least 6 characters."

                        else ->
                            "Sign-up failed: ${throwable.message ?: "Unknown error"}"
                    }

                    _state.value = currentState.copy(
                        isLoading = false,
                        isSignedIn = false,
                        error = message
                    )
                }
            )
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = signOutUseCase()
            val currentState = _state.value

            result.fold(
                onSuccess = {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isSignedIn = false,
                        error = null
                    )
                },
                onFailure = {
                    _state.value = currentState.copy(
                        isLoading = false,
                        error = it.message,
                        isSignedIn = false
                    )
                }
            )
        }
    }

    private fun checkUser() {
        val currentUser = getCurrentUserUseCase()
        _state.value = _state.value.copy(isSignedIn = currentUser != null)
    }
}
