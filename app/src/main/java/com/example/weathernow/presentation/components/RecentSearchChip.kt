package com.example.weathernow.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

