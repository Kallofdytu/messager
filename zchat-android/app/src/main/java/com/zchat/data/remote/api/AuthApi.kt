package com.zchat.data.remote.api

import com.zchat.data.remote.dto.AuthResponse
import com.zchat.data.remote.dto.LoginRequest
import com.zchat.data.remote.dto.ProfileResponse
import com.zchat.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh/")
    suspend fun refresh(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("profile/")
    suspend fun getProfile(): Response<ProfileResponse>
}
