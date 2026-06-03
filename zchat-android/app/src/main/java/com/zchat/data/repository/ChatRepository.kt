package com.zchat.data.repository

import com.zchat.data.local.dao.ChatDao
import com.zchat.data.local.dao.ContactDao
import com.zchat.data.local.dao.MessageDao
import com.zchat.data.local.entity.ChatEntity
import com.zchat.data.local.entity.ContactEntity
import com.zchat.data.local.entity.MessageEntity
import com.zchat.data.remote.api.ChatApi
import com.zchat.data.remote.dto.ChatDto
import com.zchat.data.remote.dto.ContactDto
import com.zchat.data.remote.dto.MessageDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatApi: ChatApi,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val contactDao: ContactDao
) {

    // ─═══ Чатҳо ═══─

    val allChats: Flow<List<ChatEntity>> = chatDao.getAll()

    suspend fun fetchAndSaveChats(): Result<Unit> {
        return try {
            val response = chatApi.getChats()
            if (response.isSuccessful) {
                val chatDtos = response.body()?.chats ?: emptyList()
                val entities = chatDtos.map { it.toEntity() }
                chatDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Хатогии боркунии чатҳо"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии пайвастшавӣ: ${e.message}"))
        }
    }

    suspend fun getChatById(id: Long): ChatEntity? {
        return chatDao.getById(id)
    }

    // ─═══ Паёмҳо ═══─

    fun getMessagesByChatId(chatId: Long): Flow<List<MessageEntity>> {
        return messageDao.getMessagesByChatId(chatId)
    }

    suspend fun fetchAndSaveMessages(chatId: Long, platformChatId: String): Result<Unit> {
        return try {
            val response = chatApi.getMessages(platformChatId)
            if (response.isSuccessful) {
                val messageDtos = response.body() ?: emptyList()
                val entities = messageDtos.map { it.toEntity(chatId) }
                messageDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Хатогии боркунии паёмҳо"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии пайвастшавӣ: ${e.message}"))
        }
    }

    suspend fun saveMessage(message: MessageEntity) {
        messageDao.insert(message)
    }

    // ─═══ Контактҳо ═══─

    val allContacts: Flow<List<ContactEntity>> = contactDao.getAll()

    suspend fun fetchAndSaveContacts(): Result<Unit> {
        return try {
            val response = chatApi.getContacts()
            if (response.isSuccessful) {
                val contactDtos = response.body() ?: emptyList()
                val entities = contactDtos.map { it.toEntity() }
                contactDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Хатогии боркунии контактҳо"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Хатогии пайвастшавӣ: ${e.message}"))
        }
    }
}

// ─═══ Extension Functions ═══─

private fun ChatDto.toEntity() = ChatEntity(
    platformName = platform,
    contactName = contactName,
    title = title,
    platformChatId = platformChatId,
    lastMessageAt = parseTime(lastMessageAt),
    createdAt = parseTime(createdAt)
)

private fun MessageDto.toEntity(chatId: Long) = MessageEntity(
    chatId = chatId,
    sender = sender,
    content = content,
    messageType = messageType,
    sentAt = parseTime(sentAt),
    isFromUser = isFromUser
)

private fun ContactDto.toEntity() = ContactEntity(
    platformName = platform,
    name = name,
    username = username,
    phone = phone,
    avatarUrl = avatarUrl,
    platformContactId = platformContactId,
    lastSeen = parseTime(lastSeen)
)

private fun parseTime(timeStr: String): Long {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        sdf.parse(timeStr)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}
