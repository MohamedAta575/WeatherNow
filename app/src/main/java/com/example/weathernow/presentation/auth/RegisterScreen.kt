package com.example.weathernow.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weathernow.presentation.component.CustomSnackbar
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    isLoginScreen: Boolean
) {
    val state by viewModel.state.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    // ✅ Success Message
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            messageText = it
            isSuccess = true
            showMessage = true
            delay(2500)
            showMessage = false
            onRegisterSuccess()
        }
    }

    // ✅ Error Message
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("☀️", fontSize = 40.sp)
            Text("WeatherNow", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "Your personal weather companion",
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
                    onClick = { onNavigateToLogin() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoginScreen) Color.White else Color.Transparent
                    )
                ) {
                    Text("Login", color = if (isLoginScreen) Color(0xFF007BFF) else Color.White)
                }

                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLoginScreen) Color.White else Color.Transparent
                    )
                ) {
                    Text("Register", color = if (!isLoginScreen) Color(0xFF007BFF) else Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🧾 Register Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Create an account", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Get started with your weather forecast",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        isError = showErrors && fullName.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(showErrors && fullName.isBlank()) {
                        Text("Please enter your full name", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        isError = showErrors && email.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(showErrors && email.isBlank()) {
                        Text("Please enter your email", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = showErrors && password.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(showErrors && password.isBlank()) {
                        Text("Please enter your password", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle confirm password visibility"
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = showErrors && (confirmPassword.isBlank() || confirmPassword != password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(showErrors && confirmPassword != password) {
                        Text("Passwords do not match", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Terms Checkbox
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = agreeTerms, onCheckedChange = { agreeTerms = it })
                        Text(
                            text = "I agree to the Terms of Service and Privacy Policy",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    AnimatedVisibility(showErrors && !agreeTerms) {
                        Text("You must agree to the terms", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Register Button
                    Button(
                        onClick = {
                            showErrors = true
                            when {
                                fullName.isBlank() || email.isBlank() || password.isBlank() -> Unit
                                password != confirmPassword -> Unit
                                !agreeTerms -> Unit
                                else -> viewModel.handleIntent(AuthIntent.SignUp(email, password))
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
                            Text("Create account")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Need help? Contact support@weathernow.com",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }

        // ✅ Snackbar
        CustomSnackbar(
            visible = showMessage,
            message = messageText,
            isSuccess = isSuccess,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
