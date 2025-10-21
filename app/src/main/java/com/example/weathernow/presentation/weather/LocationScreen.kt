package com.example.weathernow.presentation.weather

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weathernow.presentation.auth.AuthIntent
import com.example.weathernow.presentation.auth.AuthViewModel
import com.example.weathernow.presentation.utils.LocationManager
import com.example.weathernow.presentation.components.*
import com.example.weathernow.ui.theme.DesignBlue
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navToLogin: () -> Unit,
    onCitySelected: (String) -> Unit,
    onLocationWeatherLoaded: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 UI States
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(false) }

    // 🔹 Observing states from ViewModels
    val cities by viewModel.cities.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val popularCities by viewModel.popularCities.collectAsState()
    val authState by authViewModel.state.collectAsState()
    val state by viewModel.state.collectAsState()

    // 🔹 Location Manager
    val locationManager = remember { LocationManager(context) }

    // ✅ Handle permissions
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                scope.launch {
                    isLocating = true
                    val loc = locationManager.getUserLocation()
                    isLocating = false
                    loc?.let { (lat, lon) ->
                        Log.d("LocationScreen", "Got location: $lat, $lon")
                        viewModel.handleIntent(WeatherIntent.LoadWeatherByLocation(lat, lon))
                    }
                }
            }
        }
    )

    // ✅ Auth check once
    LaunchedEffect(Unit) {
        authViewModel.handleIntent(AuthIntent.CheckUser)
    }

    // ✅ Observe events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is WeatherEvent.NavigateToWeatherDetail) {
                val weatherData = viewModel.state.value.data
                if (weatherData != null) {
                    onCitySelected(weatherData.cityName)
                }
            }
        }
    }

    // ✅ UI Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4AA3FF), Color(0xFF6B63FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            // ===== Header =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weather Forecast",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Search for a city to see the weather",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // ===== Menu =====
                Box(modifier = Modifier.zIndex(1f)) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }

                    UserDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        userName = authState.userName ?: "",
                        userEmail = authState.userEmail ?: "",
                        onLogout = {
                            authViewModel.handleIntent(AuthIntent.SignOut)
                            navToLogin()
                        }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ===== Search Bar =====
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.loadCities(it)
                },
                placeholder = { Text("Search for a city...", color = Color.Gray) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search icon", tint = Color.Gray)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = DesignBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ===== Current Location Button =====
            Button(
                onClick = {
                    if (locationManager.hasLocationPermission()) {
                        scope.launch {
                            isLocating = true
                            val loc = locationManager.getUserLocation()
                            isLocating = false
                            loc?.let { (lat, lon) ->
                                viewModel.handleIntent(WeatherIntent.LoadWeatherByLocation(lat, lon))
                            }
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location icon", tint = DesignBlue)
                    Spacer(Modifier.width(8.dp))
                    Text("Use Current Location", color = DesignBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            // ✅ Loading Indicator while locating
            if (isLocating) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ===== Error Message =====
            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))
            }

            // ===== Search Results / Popular Cities =====
            if (cities.isNotEmpty() && searchQuery.isNotEmpty()) {
                Text("Search Results", color = Color.White, fontWeight = FontWeight.SemiBold)
                LazyColumn {
                    items(cities) { city ->
                        SearchResultItem(city = city, onCitySelected = onCitySelected)
                        Divider(color = Color.White.copy(alpha = 0.2f))
                    }
                }
            } else {
                LazyColumn {
                    item {
                        Text("Recent Searches", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            recentSearches.forEach { city ->
                                RecentSearchChip(city = city, onCitySelected = onCitySelected)
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Popular Cities", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    items(popularCities) { city ->
                        PopularCityCard(city = city, onCitySelected = { onCitySelected(city.name) })
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
