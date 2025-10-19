package com.example.weathernow.data.store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[REMEMBER_ME] ?: false
        }

    suspend fun setRememberMe(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME] = value
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }

    val userNameFlow: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[USER_NAME_KEY]
        }

    suspend fun clearUserName() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_NAME_KEY)
        }
    }
}