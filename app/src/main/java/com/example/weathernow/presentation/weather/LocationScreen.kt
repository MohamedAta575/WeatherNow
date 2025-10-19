package com.example.weathernow.presentation.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weathernow.presentation.auth.AuthViewModel
import com.example.weathernow.ui.theme.DesignBlue
import com.google.android.gms.location.LocationServices
import com.example.weathernow.presentation.auth.AuthIntent

import android.Manifest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
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
    var searchQuery by remember { mutableStateOf("") }
    val cities by viewModel.cities.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val popularCities by viewModel.popularCities.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val authState by authViewModel.state.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getCurrentLocation(
                    context,
                    onLocationReceived = { lat, lon ->
                        viewModel.handleIntent(WeatherIntent.LoadWeatherByLocation(lat, lon))
                    }
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is WeatherEvent.NavigateToWeatherDetail) {
                onLocationWeatherLoaded()
            }
        }
    }


    val requestLocationPermission: () -> Unit = {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation(
                    context,
                    onLocationReceived = { lat, lon ->
                        viewModel.handleIntent(WeatherIntent.LoadWeatherByLocation(lat, lon))
                    }
                )
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFF4AA3FF), Color(0xFF6B63FF))))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

            Spacer(Modifier.height(40.dp))

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

                Box {
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.loadCities(it)
                },
                placeholder = { Text("Search for a city...", color = Color.Gray) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray) },
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

            Button(
                onClick = requestLocationPermission,
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
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = DesignBlue)
                    Spacer(Modifier.width(8.dp))
                    Text("Use Current Location", color = DesignBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))

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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onLocationReceived: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(
        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
        5000
    ).setMaxUpdates(1).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationReceived(location.latitude, location.longitude)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }
}


@Composable
fun UserDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    val principalText = if (userName.isNotEmpty()) userName else userEmail
    val secondaryText = if (userName.isNotEmpty()) userEmail else null

    if (principalText.isEmpty()) return

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = (-140).dp, y = 8.dp),
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .width(IntrinsicSize.Max)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User Avatar",
                    tint = DesignBlue,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = principalText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    secondaryText?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Logout Icon",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            onClick = {
                onDismissRequest()
                onLogout()
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}


@Composable
fun SearchResultItem(city: String, onCitySelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCitySelected(city) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(city, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
fun RecentSearchChip(city: String, onCitySelected: (String) -> Unit) {
    AssistChip(
        onClick = { onCitySelected(city) },
        label = { Text(city, fontSize = 14.sp) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            labelColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        border = null
    )
}

@Composable
fun PopularCityCard(city: WeatherViewModel.PopularCity, onCitySelected: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCitySelected(city.name) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = DesignBlue)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(city.name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                    Text(city.country, fontSize = 14.sp, color = Color.Gray)
                }
            }
            Text(city.temperature, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black)
        }
    }
}