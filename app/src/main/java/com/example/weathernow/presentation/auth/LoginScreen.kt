package com.example.weathernow.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weathernow.presentation.component.CustomSnackbar
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    isLoginScreen: Boolean = true
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // ✅ success message
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            messageText = it
            isSuccess = true
            showMessage = true
            delay(2500)
            showMessage = false
            onLoginSuccess()
        }
    }

    // ✅ error message
    LaunchedEffect(state.error) {
        state.error?.let {
            messageText = it
            isSuccess = false
            showMessage = true
            delay(3000)
            showMessage = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4AA3FF), Color(0xFF6B63FF)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = "☀️", fontSize = 40.sp)
            Text(
                text = "WeatherNow",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Your personal weather companion",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))
            // 🔁 Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x552E85FF), RoundedCornerShape(24.dp))
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoginScreen) Color.White else Color.Transparent
                    )
                ) {
                    Text(
                        "Login", color = if (isLoginScreen) Color(0xFF007BFF) else Color.White
                    )
                }

                Button(
                    onClick = {
                        onNavigateToRegister()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLoginScreen) Color.White else Color.Transparent
                    )
                ) {
                    Text(
                        "Register",
                        color = if (!isLoginScreen) Color(0xFF007BFF) else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔑 Login Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Welcome back", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Enter your credentials to access your account",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        isError = showErrors && email.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                        )
                    )
                    AnimatedVisibility(showErrors && email.isBlank()) {
                        Text("Please enter your email", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = showErrors && password.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(showErrors && password.isBlank()) {
                        Text("Please enter your password", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text("Remember me")
                    }


                    Button(
                        onClick = {
                            showErrors = true
                            if (email.isNotBlank() && password.isNotBlank()) {
                                viewModel.handleIntent(AuthIntent.SignIn(email, password,rememberMe))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFF007BFF))
                    ) {
                        if (state.isLoading)
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        else
                            Text("Sign in")
                    }
                }
            }
        }

        CustomSnackbar(
            visible = showMessage,
            message = messageText,
            isSuccess = isSuccess,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
