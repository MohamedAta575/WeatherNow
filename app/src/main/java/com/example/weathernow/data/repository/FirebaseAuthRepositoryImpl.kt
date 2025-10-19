package com.example.weathernow.data.repository

import com.example.weathernow.data.store.UserPreferences
import com.example.weathernow.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun signUp(userName: String, email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()

            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build()
            user?.updateProfile(profileUpdates)?.await()

            userPreferences.setUserName(userName)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()

            val userName = auth.currentUser?.displayName
            if (userName != null) {
                userPreferences.setUserName(userName)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            userPreferences.setRememberMe(false)
            userPreferences.clearUserName()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }

    override fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    override suspend fun setRememberMe(value: Boolean) {
        userPreferences.setRememberMe(value)
    }

    override fun getRememberMe(): Flow<Boolean> {
        return userPreferences.rememberMeFlow
    }
}