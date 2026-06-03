package com.zchat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnalyzeRequest(
    @SerializedName("contact_name")
    val contactName: String,
    val platform: String,
    @SerializedName("chat_history")
    val chatHistory: String,
    val language: String = "tg"
)

data class AnalyzeResponse(
    @SerializedName("suggested_reply")
    val suggestedReply: String,
    val confidence: Double = 0.0,
    val explanation: String = ""
)
