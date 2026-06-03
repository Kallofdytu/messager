package com.zchat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zchat.data.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val platformName: String = "",
    val suggestedReply: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var contactName: String = "Корбар"
    private var chatHistory: String = ""
    private var language: String = "tg"

    fun initialize(platform: String) {
        _uiState.value = ChatUiState(platformName = platform)
    }

    fun updateContactName(name: String) {
        contactName = name
    }

    fun updateChatHistory(history: String) {
        chatHistory = history
    }

    fun updateLanguage(lang: String) {
        language = lang
    }

    fun analyzeChat() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val reply = aiRepository.getSuggestedReply(
                    contactName = contactName,
                    platform = _uiState.value.platformName,
                    chatHistory = chatHistory,
                    language = language
                )
                _uiState.value = _uiState.value.copy(
                    suggestedReply = reply,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Хатогӣ: ${e.message}"
                )
            }
        }
    }

    fun setSuggestedReply(reply: String) {
        _uiState.value = _uiState.value.copy(suggestedReply = reply)
    }
}
