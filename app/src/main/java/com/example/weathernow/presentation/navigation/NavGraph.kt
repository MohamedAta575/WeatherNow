package com.example.weathernow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.weathernow.presentation.auth.AuthViewModel
import com.example.weathernow.presentation.auth.LoginScreen
import com.example.weathernow.presentation.auth.RegisterScreen
import com.example.weathernow.presentation.splash.SplashScreen
import com.example.weathernow.presentation.splash.SplashViewModel
import com.example.weathernow.presentation.weather.LocationScreen
import com.example.weathernow.presentation.weather.WeatherIntent
import com.example.weathernow.presentation.weather.WeatherScreen
import com.example.weathernow.presentation.weather.WeatherViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // Splash Screen
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Location.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }


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
            LocationScreen(
                viewModel = hiltViewModel(),
                onCitySelected = { city ->
                    navController.navigate(Screen.Weather.createRoute(city))
                },
                onLocationWeatherLoaded = {
                    navController.navigate(Screen.Weather.createRoute("AutoLocation")) {
                        popUpTo(Screen.Location.route) { inclusive = true }
                    }
                },
                navToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Location.route) { inclusive = true }
                    }
                }
            )
        }

        // Weather Screen
        composable(
            route = Screen.Weather.route,
            arguments = listOf(navArgument("city") { type = NavType.StringType })
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: ""
            val viewModel: WeatherViewModel = hiltViewModel()
            LaunchedEffect(city) {
                if (city != "AutoLocation") {
                    viewModel.handleIntent(WeatherIntent.LoadWeather(city))
                }
            }
            WeatherScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack()}
            )
        }
    }
}