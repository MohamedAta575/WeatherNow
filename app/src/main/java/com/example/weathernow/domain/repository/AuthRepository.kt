package com.example.weathernow.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(userName: String,email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun setCurrentUserName(name: String)
    suspend fun setCurrentUserEmail(email: String)
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
    suspend fun setRememberMe(value: Boolean)
    fun getRememberMe(): Flow<Boolean>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

}