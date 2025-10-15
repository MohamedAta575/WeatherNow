package com.example.weathernow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.weathernow.presentation.auth.LoginScreen
import com.example.weathernow.presentation.auth.RegisterScreen
import com.example.weathernow.presentation.navigation.NavGraph
import com.example.weathernow.presentation.weather.LocationScreen
import com.example.weathernow.ui.theme.WeatherNowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherNowTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}