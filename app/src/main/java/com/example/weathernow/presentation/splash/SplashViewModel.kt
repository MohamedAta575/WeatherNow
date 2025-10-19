package com.example.weathernow.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state

    fun handleIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.CheckUser -> checkUser()
        }
    }

    private fun checkUser() {
        viewModelScope.launch {
            val remember = authRepository.getRememberMe().first()
            val email = authRepository.getCurrentUserEmail()
            _state.value = _state.value.copy(
                isChecking = false,
                isSignedIn = remember && email != null,
                userEmail = if (remember) email else null
            )
        }
    }
}
