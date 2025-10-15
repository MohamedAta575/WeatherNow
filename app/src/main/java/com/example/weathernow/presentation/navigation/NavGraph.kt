package com.example.weathernow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.weathernow.presentation.auth.AuthViewModel
import com.example.weathernow.presentation.auth.LoginScreen
import com.example.weathernow.presentation.auth.RegisterScreen
import com.example.weathernow.presentation.weather.LocationScreen
import com.example.weathernow.presentation.weather.WeatherScreen
import com.example.weathernow.presentation.weather.WeatherViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        // Login Screen
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Location.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                isLoginScreen = true
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Location.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                isLoginScreen = false
            )
        }

        // Location Screen
        composable(Screen.Location.route) {
            LocationScreen(onCitySelected = { city ->
                navController.navigate(Screen.Weather.createRoute(city)) // تمرير المدينة للشاشة التالية
            })
        }

        // Weather Screen
        composable(
            route = Screen.Weather.route,
            arguments = listOf(navArgument("city") { type = NavType.StringType }) // ✅ نحدد argument city
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: ""
            val viewModel: WeatherViewModel = hiltViewModel()
            viewModel.loadWeather(city)
            WeatherScreen(viewModel = viewModel)
        }
    }
}
