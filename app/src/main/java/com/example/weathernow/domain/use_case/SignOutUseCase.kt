package com.example.weathernow.domain.use_case

import com.example.weathernow.domain.repository.AuthRepository
import jakarta.inject.Inject


class SignOutUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            repo.signOut()
                .map { "Signed out successfully 🌧" }
                .getOrElse { e ->
                    throw Exception("Sign-out failed: ${e.message ?: "Unknown error"}")
                }.let { Result.success(it) }
        } catch (e: Exception) {
            Result.failure(Throwable("Sign-out failed: ${e.message ?: "Unknown error"}"))
        }
    }
}