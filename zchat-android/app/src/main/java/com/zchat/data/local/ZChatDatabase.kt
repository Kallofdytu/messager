package com.zchat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zchat.data.local.dao.ChatDao
import com.zchat.data.local.dao.ContactDao
import com.zchat.data.local.dao.MessageDao
import com.zchat.data.local.entity.ChatEntity
import com.zchat.data.local.entity.ContactEntity
import com.zchat.data.local.entity.MessageEntity

@Database(
    entities = [
        ChatEntity::class,
        MessageEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZChatDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    abstract fun messageDao(): MessageDao

    abstract fun contactDao(): ContactDao
}
