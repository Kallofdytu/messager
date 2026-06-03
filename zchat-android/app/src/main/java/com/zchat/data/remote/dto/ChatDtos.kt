package com.zchat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlatformDto(
    val id: Long,
    val name: String,
    @SerializedName("display_name")
    val displayName: String,
    val icon: String = ""
)

data class ChatDto(
    val id: Long = 0,
    val platform: String,
    @SerializedName("contact_name")
    val contactName: String,
    val title: String = "",
    @SerializedName("platform_chat_id")
    val platformChatId: String = "",
    @SerializedName("last_message_at")
    val lastMessageAt: String = "",
    @SerializedName("created_at")
    val createdAt: String = ""
)

data class MessageDto(
    val id: Long = 0,
    val chat: Long = 0,
    val sender: String,
    val content: String,
    @SerializedName("message_type")
    val messageType: String = "text",
    @SerializedName("sent_at")
    val sentAt: String = "",
    @SerializedName("is_from_user")
    val isFromUser: Boolean = false
)

data class ContactDto(
    val id: Long = 0,
    val platform: String,
    val name: String,
    val username: String = "",
    val phone: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String = "",
    @SerializedName("platform_contact_id")
    val platformContactId: String = "",
    @SerializedName("last_seen")
    val lastSeen: String = ""
)

data class ChatListDto(
    val results: List<ChatDto> = emptyList(),
    val count: Int = 0
)
