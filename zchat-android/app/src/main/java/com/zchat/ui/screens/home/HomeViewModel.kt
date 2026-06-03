package com.zchat.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zchat.accessibility.AccessibilityHelper
import com.zchat.accessibility.FloatingButtonService
import com.zchat.data.repository.AuthRepository
import com.zchat.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val username: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFloatingActive: Boolean = false,
    val hasOverlayPermission: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val platforms = listOf(
        PlatformInfo("WhatsApp", "\uD83D\uDCF1"),
        PlatformInfo("Instagram", "\uD83D\uDCF8"),
        PlatformInfo("Telegram", "\uD83D\uDCE8"),
        PlatformInfo("Viber", "\uD83D\uDCDE"),
        PlatformInfo("TikTok", "\uD83C\uDFA5")
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try {
                val username = authRepository.currentUsername.first() ?: "Корбар"
                chatRepository.fetchAndSaveChats()
                chatRepository.fetchAndSaveContacts()
                _uiState.value = HomeUiState(username = username, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    error = "Хатогӣ: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun checkPermissions() {
        // This is called from the composable - the UI layer checks overlay permission
    }

    fun hasOverlayPermission(): Boolean {
        return _uiState.value.hasOverlayPermission
    }

    fun setOverlayPermission(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasOverlayPermission = granted)
    }

    fun requestOverlayPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun startFloatingService(context: Context) {
        try {
            FloatingButtonService.start(context)
            _uiState.value = _uiState.value.copy(isFloatingActive = true)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Хатогӣ оғоз кардани хидмат: ${e.message}"
            )
        }
    }

    fun stopFloatingService(context: Context) {
        try {
            FloatingButtonService.stop(context)
            _uiState.value = _uiState.value.copy(isFloatingActive = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Хатогӣ қатъ кардани хидмат: ${e.message}"
            )
        }
    }
}

data class PlatformInfo(
    val name: String,
    val emoji: String
)
