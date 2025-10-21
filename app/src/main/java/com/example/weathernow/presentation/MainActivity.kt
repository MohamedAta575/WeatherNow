package com.example.weathernow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.weathernow.data.notifications.NotificationHelper
import com.example.weathernow.presentation.navigation.NavGraph
import com.example.weathernow.presentation.utils.NotificationPermissionHandler
import com.example.weathernow.ui.theme.WeatherNowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationHelper = NotificationHelper(this)

        setContent {
            WeatherNowTheme {
                NotificationPermissionHandler()
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }

    }

}
