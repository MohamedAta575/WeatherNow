package com.example.weathernow.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

class UserPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("user_prefs")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        val RECENT_CITIES_KEY = stringSetPreferencesKey("recent_cities")

        private const val MAX_RECENT_CITIES = 5
    }

    val recentCitiesFlow: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[RECENT_CITIES_KEY]?.toList()?.reversed() ?: emptyList()
        }
        .distinctUntilChanged()

    suspend fun saveRecentCity(city: String) {
        val normalizedCity = city.trim()
        if (normalizedCity.isEmpty()) return

        context.dataStore.edit { preferences ->
            val currentCities = preferences[RECENT_CITIES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentCities.remove(normalizedCity)
            currentCities.add(normalizedCity)
            val updatedList = currentCities.toList().takeLast(MAX_RECENT_CITIES)
            preferences[RECENT_CITIES_KEY] = updatedList.toSet()
        }
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