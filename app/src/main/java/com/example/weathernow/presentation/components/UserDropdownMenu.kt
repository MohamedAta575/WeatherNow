package com.example.weathernow.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.PopupProperties

@Composable
fun UserDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(230.dp)
            .shadow(10.dp, RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9FB), RoundedCornerShape(12.dp)),
        offset = DpOffset(x = (-160).dp, y = 0.dp),
        properties = PopupProperties(focusable = true)
    ) {
        DropdownMenuItem(
            text = {
                Column {
                    Text(
                        text = userName.ifEmpty { "Guest" },
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userEmail.ifEmpty { "Not logged in" },
                        color = Color(0xFF777777),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = Color(0xFF5A5A5A)
                )
            },
            onClick = {}
        )

        Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 4.dp))

        DropdownMenuItem(
            text = {
                Text(
                    text = "Logout",
                    color = Color(0xFFe53935),
                    fontWeight = FontWeight.SemiBold
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout Icon",
                    tint = Color(0xFFe53935)
                )
            },
            onClick = {
                onLogout()
                onDismissRequest()
            }
        )
    }
}
