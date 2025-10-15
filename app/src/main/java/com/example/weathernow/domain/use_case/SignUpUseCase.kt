package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.repository.AuthRepository
import jakarta.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return try {
            repo.signUp(email, password)
                .map { "Account created successfully 🌤" }
                .getOrElse { e ->
                    throw Exception(mapFirebaseError(e))
                }.let { Result.success(it) }
        } catch (e: Exception) {
            Result.failure(Throwable(mapFirebaseError(e)))
        }
    }

    private fun mapFirebaseError(e: Throwable): String {
        val msg = e.message ?: "Unknown error"
        return when {
            msg.contains("already in use", true) -> "This email is already in use. Try logging in instead."
            msg.contains("badly formatted", true) -> "Invalid email format."
            msg.contains("WEAK_PASSWORD", true) ||
                    msg.contains("Password should be at least", true) -> "Password should be at least 6 characters."
            else -> msg
        }
    }
}
