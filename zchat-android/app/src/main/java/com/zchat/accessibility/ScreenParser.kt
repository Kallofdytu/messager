package com.zchat.accessibility

import android.view.accessibility.AccessibilityEvent

data class ParsedChatData(
    val platform: String,
    val contactName: String,
    val messages: List<String>,
    val language: String = "tg"
)

interface ScreenParser {

    fun parse(event: AccessibilityEvent): ParsedChatData?

    fun getPlatformName(): String
}
