package com.zchat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val platformName: String,
    val contactName: String,
    val title: String,
    val platformChatId: String,
    val lastMessageAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
