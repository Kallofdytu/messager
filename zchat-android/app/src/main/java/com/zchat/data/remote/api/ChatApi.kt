package com.zchat.data.remote.api

import com.zchat.data.remote.dto.ChatDto
import com.zchat.data.remote.dto.ChatListDto
import com.zchat.data.remote.dto.ContactDto
import com.zchat.data.remote.dto.MessageDto
import com.zchat.data.remote.dto.PlatformDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {

    @GET("platforms/")
    suspend fun getPlatforms(): Response<List<PlatformDto>>

    @GET("chats/")
    suspend fun getChats(): Response<ChatListDto>

    @POST("chats/")
    suspend fun createChat(@Body chat: ChatDto): Response<ChatDto>

    @GET("messages/")
    suspend fun getMessages(): Response<List<MessageDto>>

    @POST("messages/")
    suspend fun sendMessage(@Body message: MessageDto): Response<MessageDto>

    @GET("contacts/")
    suspend fun getContacts(): Response<List<ContactDto>>
}
