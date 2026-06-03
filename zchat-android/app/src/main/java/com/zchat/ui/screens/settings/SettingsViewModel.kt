package com.zchat.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zchat.data.repository.AuthRepository
import com.zchat.util.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val currentLanguage: String = "tg",
    val isDarkTheme: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageManager: LanguageManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val lang = languageManager.getCurrentLanguage()
            _uiState.value = _uiState.value.copy(currentLanguage = lang)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            languageManager.setLanguage(lang)
            _uiState.value = _uiState.value.copy(currentLanguage = lang)
        }
    }

    fun toggleTheme() {
        _uiState.value = _uiState.value.copy(
            isDarkTheme = !_uiState.value.isDarkTheme
        )
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogout()
        }
    }
}
