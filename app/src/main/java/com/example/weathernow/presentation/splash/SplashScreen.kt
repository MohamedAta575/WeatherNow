package com.example.weathernow.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weathernow.R
import com.example.weathernow.ui.theme.BackgroundBrush


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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_weather),
                contentDescription = "WeatherNow App Logo",
                modifier = Modifier
                    .size(150.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "WeatherNow",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (state.isChecking) {
                Spacer(Modifier.height(48.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }
        }
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