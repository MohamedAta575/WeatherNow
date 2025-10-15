package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.repository.AuthRepository
import jakarta.inject.Inject


class SignInUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return try {
            repo.signIn(email, password)
                .map { "Welcome back ☀️" }
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
            msg.contains("no user record", ignoreCase = true) -> "No account found with this email."
            msg.contains("password is invalid", ignoreCase = true) -> "Incorrect password."
            msg.contains("badly formatted", ignoreCase = true) -> "Invalid email format."
            else -> msg
        }
    }
}