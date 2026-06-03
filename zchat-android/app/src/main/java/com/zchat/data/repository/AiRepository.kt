package com.zchat.data.repository

import com.zchat.data.remote.api.AiApi
import com.zchat.data.remote.dto.AnalyzeRequest
import com.zchat.data.remote.gemini.GeminiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val aiApi: AiApi,
    private val geminiClient: GeminiClient
) {

    suspend fun getSuggestedReply(
        contactName: String,
        platform: String,
        chatHistory: String,
        language: String
    ): String {
        // Аввал ба сервери бекэнд кӯшиш мекунем
        val serverResult = try {
            val response = aiApi.analyze(
                AnalyzeRequest(
                    contactName = contactName,
                    platform = platform,
                    chatHistory = chatHistory,
                    language = language
                )
            )
            if (response.isSuccessful) {
                response.body()?.suggestedReply
            } else null
        } catch (e: Exception) {
            null
        }

        if (!serverResult.isNullOrBlank()) {
            return serverResult
        }

        // Агар сервер дастрас набошад, Gemini-ро истифода мебарем
        return geminiClient.analyzeChat(
            contactName = contactName,
            platform = platform,
            chatHistory = chatHistory,
            language = language
        )
    }
}
