package com.zchat.data.remote.gemini

import android.util.Log
import com.zchat.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiClient @Inject constructor() {

    private val apiKey: String = BuildConfig.GEMINI_API_KEY
    private val tag = "GeminiClient"

    suspend fun analyzeChat(
        contactName: String,
        platform: String,
        chatHistory: String,
        language: String
    ): String {
        val prompt = PromptBuilder.buildPrompt(contactName, platform, chatHistory, language)

        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
            Log.w(tag, "Калиди API холӣ аст. Истифодаи ҷавоби озмоишӣ.")
            return getMockReply(contactName, platform)
        }

        return try {
            callGeminiApi(prompt)
        } catch (e: Exception) {
            Log.e(tag, "Хатогии Gemini API: ${e.message}")
            getMockReply(contactName, platform)
        }
    }

    private suspend fun callGeminiApi(prompt: String): String {
        // Интегратсияи расмии Gemini SDK
        // Барои истифодаи SDK китобхонаро илова кунед:
        // implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
        //
        // val generativeModel = GenerativeModel(
        //     modelName = "gemini-2.0-flash",
        //     apiKey = apiKey
        // )
        // val response = generativeModel.generateContent(prompt)
        // return response.text ?: ""

        // Ҳоло API-ро мустақиман даъват мекунем:
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
        val client = okhttp3.OkHttpClient()
        val json = """
            {
                "contents": [{
                    "parts": [{"text": ${prompt.jsonEncode()}}]
                }]
            }
        """.trimIndent()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return getMockReply()

        val regex = "\"text\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        val match = regex.find(body)
        return match?.groupValues?.getOrElse(1) { getMockReply() }
            ?.replace("\\n", "\n")
            ?.replace("\\\"", "\"")
            ?: getMockReply()
    }

    private fun String.jsonEncode(): String {
        return this
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun getMockReply(contactName: String = "", platform: String = ""): String {
        return "Салом! Чӣ хабар? Ба паёми шумо ҷавоб медиҳам."
    }
}

private fun String.toRequestBody(contentType: okhttp3.MediaType?): okhttp3.RequestBody {
    return okhttp3.RequestBody.create(contentType ?: "application/json".toMediaTypeOrNull(), this)
}
