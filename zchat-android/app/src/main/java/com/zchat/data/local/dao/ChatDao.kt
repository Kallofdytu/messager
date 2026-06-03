package com.zchat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zchat.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chats: List<ChatEntity>)

    @Update
    suspend fun update(chat: ChatEntity)

    @Delete
    suspend fun delete(chat: ChatEntity)

    @Query("SELECT * FROM chats ORDER BY lastMessageAt DESC")
    fun getAll(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getById(id: Long): ChatEntity?

    @Query("SELECT * FROM chats WHERE platformName = :platform ORDER BY lastMessageAt DESC")
    fun getByPlatform(platform: String): Flow<List<ChatEntity>>
}
