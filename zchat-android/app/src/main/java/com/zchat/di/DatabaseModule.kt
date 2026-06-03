package com.zchat.di

import android.content.Context
import androidx.room.Room
import com.zchat.data.local.ZChatDatabase
import com.zchat.data.local.dao.ChatDao
import com.zchat.data.local.dao.ContactDao
import com.zchat.data.local.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZChatDatabase {
        return Room.databaseBuilder(
            context,
            ZChatDatabase::class.java,
            "zchat_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: ZChatDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: ZChatDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: ZChatDatabase): ContactDao {
        return database.contactDao()
    }
}
