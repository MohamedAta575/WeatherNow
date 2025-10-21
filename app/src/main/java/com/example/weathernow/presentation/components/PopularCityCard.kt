package com.example.weathernow.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathernow.presentation.weather.WeatherViewModel
import com.example.weathernow.ui.theme.DesignBlue

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
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
