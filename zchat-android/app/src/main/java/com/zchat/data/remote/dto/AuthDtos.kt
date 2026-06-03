package com.zchat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val username: String,
    val email: String,
    val phone: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val access: String,
    val refresh: String,
    val user: UserDto? = null
)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val phone: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String = ""
)

data class ProfileResponse(
    val user: UserDto
)
