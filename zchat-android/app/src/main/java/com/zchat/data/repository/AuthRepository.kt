package com.zchat.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zchat.data.remote.api.AuthApi
import com.zchat.data.remote.dto.LoginRequest
import com.zchat.data.remote.dto.RegisterRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    @ApplicationContext private val context: Context
) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY] != null
    }

    val currentUsername: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY]
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY]
    }

    suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = authApi.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    context.dataStore.edit { prefs ->
                        prefs[ACCESS_TOKEN_KEY] = body.access
                        prefs[REFRESH_TOKEN_KEY] = body.refresh
                        prefs[USERNAME_KEY] = body.user?.username ?: username
                        prefs[EMAIL_KEY] = body.user?.email ?: ""
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Ҷавоб холӣ аст"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Ном ё гузарвожа нодуруст аст"
                    403 -> "Дастрасӣ манъ аст"
                    404 -> "Сервер ёфт нашуд"
                    else -> "Хатогии сервер: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии пайвастшавӣ: ${e.message}"))
        }
    }

    suspend fun register(username: String, email: String, phone: String, password: String): Result<Unit> {
        return try {
            val response = authApi.register(RegisterRequest(username, email, phone, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    context.dataStore.edit { prefs ->
                        prefs[ACCESS_TOKEN_KEY] = body.access
                        prefs[REFRESH_TOKEN_KEY] = body.refresh
                        prefs[USERNAME_KEY] = body.user?.username ?: username
                        prefs[EMAIL_KEY] = body.user?.email ?: email
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Ҷавоб холист"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Хатогии сабти ном: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии пайвастшавӣ: ${e.message}"))
        }
    }

    suspend fun refreshToken(): Result<String> {
        return try {
            val currentRefresh = context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: ""
            val response = authApi.refresh(mapOf("refresh" to currentRefresh))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    context.dataStore.edit { prefs ->
                        prefs[ACCESS_TOKEN_KEY] = body.access
                        prefs[REFRESH_TOKEN_KEY] = body.refresh
                    }
                    Result.success(body.access)
                } else {
                    Result.failure(Exception("Ҷавоби refresh холист"))
                }
            } else {
                Result.failure(Exception("Навсозии токен ноком шуд"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии навсозии токен: ${e.message}"))
        }
    }

    suspend fun getProfile(): Result<Unit> {
        return try {
            val response = authApi.getProfile()
            if (response.isSuccessful) {
                val body = response.body()
                body?.user?.let { user ->
                    context.dataStore.edit { prefs ->
                        prefs[USERNAME_KEY] = user.username
                        prefs[EMAIL_KEY] = user.email
                    }
                }
                Result.success(Unit)
            } else {
                if (response.code() == 401) {
                    val refreshResult = refreshToken()
                    if (refreshResult.isSuccess) {
                        return getProfile()
                    }
                }
                Result.failure(Exception("Хатогии боркунии профил"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии пайвастшавӣ: ${e.message}"))
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
