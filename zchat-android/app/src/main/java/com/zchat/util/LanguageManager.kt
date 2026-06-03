package com.zchat.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.langDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_prefs")

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val currentLanguage: Flow<String> = context.langDataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "tg"
    }

    suspend fun setLanguage(lang: String) {
        context.langDataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = lang
        }
    }

    suspend fun getCurrentLanguage(): String {
        return context.langDataStore.data.firstOrNull()?.get(LANGUAGE_KEY) ?: "tg"
    }
}
