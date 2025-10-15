package com.example.weathernow.presentation.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weathernow.ui.theme.DesignBlue


@Composable
fun LocationScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    onCitySelected: (String) -> Unit,
    onUseCurrentLocation: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val cities by viewModel.cities.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val popularCities by viewModel.popularCities.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DesignBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Weather Forecast",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Search for a city to see the weather",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = DesignBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onUseCurrentLocation,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Use Current Location",
                        tint = DesignBlue
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Use Current Location",
                        color = DesignBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            if (cities.isNotEmpty() && searchQuery.isNotEmpty()) {
                Text(
                    text = "Search Results",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(cities) { city ->
                        SearchResultItem(city = city, onCitySelected = onCitySelected)
                        Divider(color = Color.White.copy(alpha = 0.2f))
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = "Recent Searches",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            recentSearches.forEach { city ->
                                RecentSearchChip(city = city, onCitySelected = onCitySelected)
                            }
                        }
                        Spacer(Modifier.height(32.dp))

                        Text(
                            text = "Popular Cities",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
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


@Composable
fun SearchResultItem(city: String, onCitySelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCitySelected(city) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            city,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light
        )
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
        border = null,
    )
}

@Composable
fun PopularCityCard(city: WeatherViewModel.PopularCity, onCitySelected: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCitySelected(city.name) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    tint = DesignBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))

                Column {
                    Text(city.name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                    Text(city.country, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Text(
                city.temperature,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}