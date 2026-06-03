package com.zchat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val platformName: String,
    val name: String,
    val username: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val platformContactId: String = "",
    val lastSeen: Long = System.currentTimeMillis()
)
