package com.example.weathernow.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(SplashIntent.CheckUser)
    }

    // الشاشة نفسها: يمكن تصميم أي لوجو أو تأثير loading
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("WeatherNow", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        CircularProgressIndicator(modifier = Modifier.padding(top = 60.dp))
    }

    LaunchedEffect(state.isChecking) {
        if (!state.isChecking) {
            if (state.isSignedIn) {
                onNavigateToHome()
            } else {
                onNavigateToLogin()
            }
        }
    }
}
