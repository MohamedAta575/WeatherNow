package com.example.weathernow.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("user_prefs")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data
        .map { it[REMEMBER_ME_KEY] ?: false }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[USER_NAME_KEY] = name }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { it[USER_EMAIL_KEY] = email }
    }

    suspend fun setRememberMe(value: Boolean) {
        context.dataStore.edit { it[REMEMBER_ME_KEY] = value }
    }

    suspend fun clearUserName() {
        context.dataStore.edit { it.remove(USER_NAME_KEY) }
    }

    suspend fun clearUserEmail() {
        context.dataStore.edit { it.remove(USER_EMAIL_KEY) }
    }

    suspend fun clearRememberMe() {
        context.dataStore.edit { it.remove(REMEMBER_ME_KEY) }
    }
}
